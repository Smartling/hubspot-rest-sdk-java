package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotBlogPostClient;
import com.smartling.connector.hubspot.sdk.HubspotClientManager;
import com.smartling.connector.hubspot.sdk.HubspotFormClient;
import com.smartling.connector.hubspot.sdk.HubspotPagesClient;
import com.smartling.connector.hubspot.sdk.rest.token.HubspotTokenProvider;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;

public class HubspotRestClientManager implements HubspotClientManager
{

    private final Configuration configuration;
    private final TokenProvider tokenProvider;

    public HubspotRestClientManager(final Configuration configuration, TokenProvider tokenProvider)
    {
        this.tokenProvider = tokenProvider;
        this.configuration = configuration;
    }

    @Override
    public HubspotPagesClient getPagesClient()
    {
        return new HubspotRestPagesClient(configuration, tokenProvider);
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
        return new HubspotTokenProvider(configuration);
    }
}
