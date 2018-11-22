package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotBlogPostClient;
import com.smartling.connector.hubspot.sdk.blog.BlogDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostFilter;
import com.smartling.connector.hubspot.sdk.rest.api.BlogApi;
import com.smartling.connector.hubspot.sdk.rest.api.BlogPostApi;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.Feign;
import feign.Request.Options;
import feign.gson.GsonDecoder;

public class HubspotRestBlogPostClient extends AbstractHubspotRestClient implements HubspotBlogPostClient
{
    private final BlogPostApi blogPostApi;
    private final BlogApi blogApi;

    public HubspotRestBlogPostClient(final Configuration configuration, final TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Options connectionConfig = new Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        blogPostApi = Feign.builder()
                              .requestInterceptor(getAuthenticationInterceptor())
                              .options(connectionConfig)
                              .decoder(new GsonDecoder(configuredGson()))
                              .target(BlogPostApi.class, configuration.getApiUrl());

        blogApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .decoder(new GsonDecoder(configuredGson()))
                .target(BlogApi.class, configuration.getApiUrl());

    }

    @Override
    public BlogDetails listBlogs(int offset, int limit) throws HubspotApiException
    {
        return execute(() -> blogApi.blogs(offset, limit));
    }

    @Override
    public BlogPostDetails listBlogPosts(int offset, int limit) throws HubspotApiException
    {
        return execute(() -> blogPostApi.blogPosts(offset, limit));
    }

    @Override
    public BlogPostDetails listBlogPosts(int offset, int limit, BlogPostFilter filter) throws HubspotApiException
    {
        return execute(() -> blogPostApi.blogPosts(filter.getArchived(), filter.getCampaign(), filter.getBlogId(),
                filter.getPostName(), filter.getSlug(), filter.getState() != null ? filter.getState().name() : null,
                offset, limit));
    }

    @Override
    public BlogPostDetail getBlogPostById(String id) throws HubspotApiException
    {
        return execute(() -> blogPostApi.blogPostDetail(id));
    }

    @Override
    public void createBlogPost(BlogPostDetail blogPostDetail)
    {

    }
}
