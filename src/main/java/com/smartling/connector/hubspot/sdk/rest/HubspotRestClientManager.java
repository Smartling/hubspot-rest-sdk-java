package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotBlogPostsClient;
import com.smartling.connector.hubspot.sdk.HubspotBlogPostsEntityClient;
import com.smartling.connector.hubspot.sdk.HubspotBlogTagsClient;
import com.smartling.connector.hubspot.sdk.HubspotClientManager;
import com.smartling.connector.hubspot.sdk.HubspotDomainClient;
import com.smartling.connector.hubspot.sdk.HubspotEmailsClient;
import com.smartling.connector.hubspot.sdk.HubspotFormsClient;
import com.smartling.connector.hubspot.sdk.HubspotPagesClient;
import com.smartling.connector.hubspot.sdk.rest.token.HubspotTokenProvider;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import com.smartling.connector.hubspot.sdk.v3.HubspotPagesV3Client;
import com.smartling.connector.hubspot.sdk.v3.page.PageType;
import com.smartling.connector.hubspot.sdk.v3.rest.HubspotRestPagesV3Client;

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
    public HubspotPagesV3Client getPagesV3Client(PageType pageType)
    {
        return new HubspotRestPagesV3Client(pageType, configuration, tokenProvider);
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

    @Override
    public HubspotBlogTagsClient getBlogTagsClient() {
        return new HubspotRestBlogPostTagsClient(configuration, tokenProvider);
    }

    @Override
    public HubspotDomainClient getDomainClient() {
        return new HubspotRestDomainClient(configuration, tokenProvider);
    }

    public static TokenProvider createTokenProvider(final Configuration configuration)
    {
        return new HubspotTokenProvider(configuration);
    }
}
