package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.*;
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
    public HubspotFormsClient getFormsClient()
    {
        return new HubspotRestFormsClient(configuration, tokenProvider);
    }

    @Override
    public HubspotBlogPostsClient getBlogPostsClient()
    {
        return new HubspotRestBlogPostsClient(configuration, tokenProvider);
    }

    @Override
    public HubspotEmailsClient getEmailsClient()
    {
        return new HubspotRestEmailsClient(configuration, tokenProvider);
    }

    @Override
    public HubspotBlogPostsEntityClient getBlogPostsEntityClient() {
        return new HubspotRestBlogPostEntityClient(configuration, tokenProvider);
    }

    @SuppressWarnings("unchecked")
    public static TokenProvider createTokenProvider(final Configuration configuration)
    {
        return new HubspotTokenProvider(configuration);
    }
}
