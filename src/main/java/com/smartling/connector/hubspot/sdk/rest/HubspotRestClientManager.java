package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotBlogPostClient;
import com.smartling.connector.hubspot.sdk.HubspotClientManager;
import com.smartling.connector.hubspot.sdk.HubspotFormClient;
import com.smartling.connector.hubspot.sdk.HubspotPageClient;
import com.smartling.connector.hubspot.sdk.rest.token.HubspotTokenProvider;
import com.smartling.connector.hubspot.sdk.rest.token.RedisCachedTokenProvider;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HubspotRestClientManager implements HubspotClientManager
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
        return new HubspotRestPageClient(configuration, tokenProvider);
    }

    @Override
    public HubspotFormClient getFormClient()
    {
        return new HubspotRestFormClient(configuration, tokenProvider);
    }

    @Override
    public HubspotBlogPostClient getBlogPostClient()
    {
        return new HubspotRestBlogPostClient(configuration, tokenProvider);
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
