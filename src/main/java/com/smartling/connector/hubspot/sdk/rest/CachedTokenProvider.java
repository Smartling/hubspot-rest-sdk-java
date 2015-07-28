package com.smartling.connector.hubspot.sdk.rest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RBucket;
import org.redisson.core.RLock;

import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClient.Configuration;

public class CachedTokenProvider extends TokenProvider
{
    public static final String REDIS_SINGLE_SERVER_ADDRESS = "redis.singleServer.address";

    private static final int TRY_DELAY = 10000;
    private static final String ACCESSKEY_NAME = "com.smartling.connector.hubspot.%s.accesskey";
    private static final String ACCESSKEY_LOCK_NAME = "com.smartling.connector.hubspot.%s.accesskey.lock";

    private String redisSingleServerAddress;

    public CachedTokenProvider(Configuration configuration) throws ConfigurationException
    {
        super(configuration);
        this.redisSingleServerAddress = configuration.getPropertyValue(REDIS_SINGLE_SERVER_ADDRESS);
        if (null == this.redisSingleServerAddress || this.redisSingleServerAddress.isEmpty())
            throw new ConfigurationException(this, configuration);
    }

    @Override
    public void expireTokenData()
    {
        Redisson redisson = createRedissonClient();
        try
        {
            RBucket<String> bucket = redisson.getBucket(String.format(ACCESSKEY_NAME, getClientId()));
            RLock lock = redisson.getLock(String.format(ACCESSKEY_LOCK_NAME, getClientId()));
            // If locking is failed current token is refreshing
            if (lock.tryLock())
            {
                try
                {
                    bucket.delete();
                }
                finally
                {
                    lock.unlock();
                }
            }
        }
        finally
        {
            shutdown(redisson);
        }
    }

    @Override
    public RefreshTokenData getTokenData()
    {
        RefreshTokenData token = null;
        Redisson redisson = createRedissonClient();
        try
        {
            while (null == token)
            {
                token = getRefreshTokenData(redisson);
                if (null == token)
                {
                    try
                    {
                        Thread.sleep(TRY_DELAY);
                    }
                    catch (InterruptedException e)
                    {
                        // Do nothing just continue working
                    }
                }
            }
        }
        finally
        {
            shutdown(redisson);
        }
        return token;
    }

    private RefreshTokenData getRefreshTokenData(Redisson redisson)
    {
        RBucket<String> bucket = redisson.getBucket(String.format(ACCESSKEY_NAME, getClientId()));
        RefreshTokenData token = createRefreshToken(bucket);
        if (null == token)
        {
            RLock lock = redisson.getLock(String.format(ACCESSKEY_LOCK_NAME, getClientId()));
            if (lock.tryLock())
            {
                try
                {
                    token = createRefreshToken(bucket);
                    if (null == token)
                    {
                        token = super.getTokenData();
                        bucket.set(token.getAccessToken(), token.getExpiresIn(), TimeUnit.SECONDS);
                    }
                }
                finally
                {
                    lock.unlock();
                }
            }
        }
        return token;
    }

    private RefreshTokenData createRefreshToken(RBucket<String> bucket)
    {
        RefreshTokenData token = null;
        if (bucket.exists())
        {
            token = new RefreshTokenData();
            token.setAccessToken(bucket.get());
            token.setExpiresIn((int)bucket.remainTimeToLive());
        }
        return token;
    }

    protected Redisson createRedissonClient()
    {
        Config config = new Config();
        config.useSingleServer().setAddress(this.redisSingleServerAddress);
        return Redisson.create(config);
    }

    protected void shutdown(Redisson redisson)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                redisson.shutdown();
            }
        });
        executor.shutdown();
    }
}
