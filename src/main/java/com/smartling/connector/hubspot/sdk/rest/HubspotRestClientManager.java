package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotClientManager;
import com.smartling.connector.hubspot.sdk.HubspotFormClient;
import com.smartling.connector.hubspot.sdk.HubspotPageClient;
import com.smartling.connector.hubspot.sdk.rest.AbstractHubspotRestClient.RestExecutor;
import com.smartling.connector.hubspot.sdk.rest.token.HubspotTokenProvider;
import com.smartling.connector.hubspot.sdk.rest.token.RedisCachedTokenProvider;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class HubspotRestClientManager implements HubspotClientManager, RestExecutor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HubspotRestClientManager.class);

    private final Configuration configuration;
    private final TokenProvider tokenProvider;

    public HubspotRestClientManager(final Configuration configuration, TokenProvider tokenProvider)
    {
        this.tokenProvider = tokenProvider;
        this.configuration = configuration;
    }

    @Override
    public HubspotPageClient getPageClient()
    {
        return new HubspotRestPageClient(configuration, this);
    }

    @Override
    public HubspotFormClient getFormClient()
    {
        return new HubspotRestFormClient(configuration, this);
    }

    @Override
    public <T> T execute(Function<String, T> apiCall) throws HubspotApiException
    {
        try
        {
            return apiCall.apply(tokenProvider.getTokenData().getAccessToken());
        }
        catch (FeignException e)
        {
            throw new HubspotApiException("Call to Hubspot API failed!", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static TokenProvider createTokenProvider(final Configuration configuration)
    {
        TokenProvider provider = new HubspotTokenProvider(configuration);
        try
        {
            provider = new RedisCachedTokenProvider(configuration, provider);
            LOGGER.info("tokenProvider is decorated by {} class", "RedisCachedTokenProvider");
        }
        catch (Exception e)
        {
            LOGGER.error("Cannot decorate tokenProvider by {} decorator", "RedisCachedTokenProvider", e);
        }
        return provider;
    }
}
