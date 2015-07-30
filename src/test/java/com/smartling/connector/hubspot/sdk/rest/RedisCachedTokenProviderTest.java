package com.smartling.connector.hubspot.sdk.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.redisson.Redisson;
import org.redisson.core.RBucket;
import org.redisson.core.RLock;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClient.Configuration;
import com.smartling.connector.hubspot.sdk.rest.token.RedisCachedTokenProvider;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;

public class RedisCachedTokenProviderTest
{
    private static final int TOKEN_LENGHT = 64;
    private static final int TOKEN_EXPIRES_FROM = 2;
    private static final int TOKEN_EXPIRES_TO = 90000;
    private static final String REDIS_URL = "redis.endpoint.com:6379";
    private static final String TOKEN_BUCKET_NAME_FORMAT = "com.smartling.connector.hubspot.%s.accesskey";
    private static final String TOKEN_LOCK_NAME_FORMAT = "com.smartling.connector.hubspot.%s.accesskey.lock";

    @Mock
    private Redisson redisson;
    @Mock
    private RBucket<String> bucket;
    @Mock
    private RLock lock;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private RefreshTokenData originalToken;
    @Mock
    private ExecutorService shutdownExecutor;

    private String clientId;
    private RedisCachedTokenProvider cachedDecorator;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        this.clientId = RandomStringUtils.randomAlphanumeric(32);

        Configuration configuration = Configuration.build(RandomStringUtils.randomAlphanumeric(32), this.clientId, RandomStringUtils.randomAlphanumeric(64));
        configuration.setProperties(Collections.singletonMap(RedisCachedTokenProvider.REDIS_SINGLE_SERVER_ADDRESS, REDIS_URL));
        this.cachedDecorator = new RedisCachedTokenProvider(configuration, this.tokenProvider)
        {
            protected Redisson createRedissonClient()
            {
                return RedisCachedTokenProviderTest.this.redisson;
            }

            protected ExecutorService createExecutorService()
            {
                return RedisCachedTokenProviderTest.this.shutdownExecutor;
            }
        };

        doReturn(this.originalToken).when(this.tokenProvider).getTokenData();
        doReturn(RandomStringUtils.random(TOKEN_LENGHT)).when(this.originalToken).getAccessToken();
        doReturn(RandomUtils.nextInt(TOKEN_EXPIRES_FROM, TOKEN_EXPIRES_TO)).when(this.originalToken).getExpiresIn();

        doReturn(this.bucket).when(this.redisson).getBucket(String.format(TOKEN_BUCKET_NAME_FORMAT, this.clientId));
        doReturn(this.lock).when(this.redisson).getLock(String.format(TOKEN_LOCK_NAME_FORMAT, this.clientId));

        doAnswer(new Answer<Void>()
        {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable
            {
                Runnable runnable = invocation.getArgumentAt(0, Runnable.class);
                runnable.run();
                return null;
            }
        }).when(this.shutdownExecutor).execute(any(Runnable.class));
    }

    @Test
    public void testGetTokenData() throws Exception
    {
        doReturn(true).when(this.lock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));

        RefreshTokenData token = this.cachedDecorator.getTokenData();

        assertEquals(this.originalToken, token);
        InOrder inOrder = inOrder(this.bucket, this.lock, this.redisson);
        inOrder.verify(this.bucket).exists();
        inOrder.verify(this.bucket, never()).get();
        inOrder.verify(this.lock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        inOrder.verify(this.bucket).set(this.originalToken.getAccessToken(), this.originalToken.getExpiresIn(), TimeUnit.SECONDS);
        inOrder.verify(this.lock).unlock();
        verifyShutdown(inOrder);
    }

    @Test
    public void testGetTokenDataCached() throws Exception
    {
        String accessToken = RandomStringUtils.random(TOKEN_LENGHT);
        long expiresIn = RandomUtils.nextInt(TOKEN_EXPIRES_FROM, TOKEN_EXPIRES_TO);
        doReturn(true).when(this.bucket).exists();
        doReturn(accessToken).when(this.bucket).get();
        doReturn(expiresIn).when(this.bucket).remainTimeToLive();

        RefreshTokenData token = this.cachedDecorator.getTokenData();

        assertEquals(accessToken, token.getAccessToken());
        assertEquals(expiresIn, token.getExpiresIn());
        verify(this.bucket).exists();
        verify(this.bucket).get();
        verify(this.bucket).remainTimeToLive();
        verifyNoMoreInteractions(this.lock, this.bucket, this.tokenProvider);
        verifyShutdown(null);
    }

    @Test
    public void testGetTokenDataLocked() throws Exception
    {
        when(this.bucket.exists()).thenReturn(false);
        doReturn(false).when(this.lock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));

        try
        {
            this.cachedDecorator.getTokenData();
            fail("HubspotApiException is expected!");
        }
        catch (HubspotApiException ex)
        {
        }

        verify(this.bucket).exists();
        verify(this.lock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        verifyNoMoreInteractions(this.lock, this.bucket, this.tokenProvider);
        verifyShutdown(null);
    }

    @Test
    public void testGetTokenDataInterrupted() throws Exception
    {
        when(this.bucket.exists()).thenReturn(false);
        doThrow(InterruptedException.class).when(this.lock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));

        try
        {
            this.cachedDecorator.getTokenData();
            fail("HubspotApiException is expected!");
        }
        catch (HubspotApiException ex)
        {
        }

        assertEquals(true, Thread.currentThread().isInterrupted());
        verify(this.bucket).exists();
        verify(this.lock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        verifyNoMoreInteractions(this.lock, this.bucket, this.tokenProvider);
        verifyShutdown(null);
    }

    private void verifyShutdown(InOrder inOrder)
    {
        if (null == inOrder)
            verify(this.redisson).shutdown();
        else
            inOrder.verify(this.redisson).shutdown();
    }
}
