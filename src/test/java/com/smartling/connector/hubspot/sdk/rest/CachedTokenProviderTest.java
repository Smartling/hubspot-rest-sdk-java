package com.smartling.connector.hubspot.sdk.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.InOrder;
import org.redisson.Redisson;
import org.redisson.core.RBucket;
import org.redisson.core.RLock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClient.Configuration;

public class CachedTokenProviderTest extends TokenProviderTest
{
    private static final String REDIS_URL = "redis.endpoint.com:6379";
    private static final String TOKEN_BUCKET_NAME = "com.smartling.connector.hubspot." + CLIENT_ID + ".accesskey";
    private static final String TOKEN_LOCK_NAME = "com.smartling.connector.hubspot." + CLIENT_ID + ".accesskey.lock";

    private Random random = new Random();
    private Redisson redisson;
    private RBucket<String> bucket;
    private RLock lock;

    @Override
    public void setUp() throws Exception
    {
        this.redisson = mock(Redisson.class);
        this.bucket = mock(RBucket.class);
        this.lock = mock(RLock.class);
        doReturn(this.bucket).when(this.redisson).getBucket(TOKEN_BUCKET_NAME);
        doReturn(this.lock).when(this.redisson).getLock(TOKEN_LOCK_NAME);
        super.setUp();
    }

    @Override
    protected TokenProvider createTokenProvider() throws Exception
    {
        Configuration configuration = Configuration.build(BASE_URL, CLIENT_ID, REFRESH_TOKEN);
        configuration.setProperties(Collections.singletonMap(CachedTokenProvider.REDIS_SINGLE_SERVER_ADDRESS, REDIS_URL));
        return new CachedTokenProvider(configuration)
        {
            protected Redisson createRedissonClient()
            {
                return CachedTokenProviderTest.this.redisson;
            }
        };
    }

    @Override
    public void testGetTokenData()
    {
        doReturn(true).when(this.lock).tryLock();
        super.testGetTokenData();
        InOrder inOrder = inOrder(this.bucket, this.lock, this.redisson);
        inOrder.verify(this.bucket).exists();
        inOrder.verify(this.bucket, never()).get();
        inOrder.verify(this.lock).tryLock();
        inOrder.verify(this.bucket).set(ACCESS_TOKEN, EXPIRES_IN_TOKEN, TimeUnit.SECONDS);
        inOrder.verify(this.lock).unlock();
        inOrder.verify(this.redisson).shutdown();
    }

    @Test
    public void testGetTokenDataCached()
    {
        String accessToken = Long.toString(this.random.nextLong());
        int expiresIn = this.random.nextInt();
        doReturn(true).when(this.bucket).exists();
        doReturn(accessToken).when(this.bucket).get();
        doReturn((long)expiresIn).when(this.bucket).remainTimeToLive();

        RefreshTokenData token = this.tokenProvider.getTokenData();

        assertEquals(accessToken, token.getAccessToken());
        assertEquals(expiresIn, token.getExpiresIn());
        WireMock.verify(0, WireMock.postRequestedFor(urlStartingWith("/auth"))
                .withRequestBody(withFormParam("client_id", CLIENT_ID))
                .withRequestBody(withFormParam("refresh_token", REFRESH_TOKEN))
                .withRequestBody(withFormParam("grant_type", "refresh_token")) );

        verify(this.bucket).exists();
        verify(this.bucket).get();
        verify(this.bucket).remainTimeToLive();
        verifyNoMoreInteractions(this.lock, this.bucket);
        verify(this.redisson).shutdown();
    }

    @Test
    public void testGetTokenDataLocked()
    {
        String accessToken = Long.toString(this.random.nextLong());
        int expiresIn = this.random.nextInt();
        when(this.bucket.exists()).thenReturn(false).thenReturn(true);
        doReturn(accessToken).when(this.bucket).get();
        doReturn((long)expiresIn).when(this.bucket).remainTimeToLive();

        RefreshTokenData token = this.tokenProvider.getTokenData();

        assertEquals(accessToken, token.getAccessToken());
        assertEquals(expiresIn, token.getExpiresIn());
        WireMock.verify(0, WireMock.postRequestedFor(urlStartingWith("/auth"))
                .withRequestBody(withFormParam("client_id", CLIENT_ID))
                .withRequestBody(withFormParam("refresh_token", REFRESH_TOKEN))
                .withRequestBody(withFormParam("grant_type", "refresh_token")) );

        verify(this.bucket, times(2)).exists();
        verify(this.bucket).get();
        verify(this.bucket).remainTimeToLive();
        verify(this.lock).tryLock();
        verifyNoMoreInteractions(this.lock, this.bucket);
        verify(this.redisson).shutdown();
    }

    @Test
    public void testExpireTokenData()
    {
        doReturn(true).when(this.lock).tryLock();

        this.tokenProvider.expireTokenData();

        InOrder inOrder = inOrder(this.bucket, this.lock, this.redisson);
        inOrder.verify(this.lock).tryLock();
        inOrder.verify(this.bucket).delete();
        inOrder.verify(this.lock).unlock();
        inOrder.verify(this.redisson).shutdown();
    }

    @Test
    public void testExpireTokenDataLocked()
    {
        this.tokenProvider.expireTokenData();

        verify(this.lock).tryLock();
        verifyNoMoreInteractions(this.lock, this.bucket);
        verifyShutdown();
    }

    private void verifyShutdown()
    {
        try
        {
            Thread.sleep(5);
        }
        catch (InterruptedException e)
        {
        }
        verify(this.redisson).shutdown();
    }
}
