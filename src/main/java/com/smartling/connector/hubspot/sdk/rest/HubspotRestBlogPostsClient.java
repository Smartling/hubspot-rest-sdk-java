package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotBlogPostsClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.blog.BlogDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostFilter;
import com.smartling.connector.hubspot.sdk.blog.CloneBlogPostRequest;
import com.smartling.connector.hubspot.sdk.rest.api.BlogsApi;
import com.smartling.connector.hubspot.sdk.rest.api.BlogPostsApi;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.Feign;
import feign.Request.Options;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.NonNull;

public class HubspotRestBlogPostsClient extends AbstractHubspotRestClient implements HubspotBlogPostsClient
{
    private final BlogPostsApi blogPostsApi;
    private final BlogsApi blogsApi;

    public HubspotRestBlogPostsClient(final Configuration configuration, final TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Options connectionConfig = new Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        blogPostsApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .encoder(new GsonEncoder(configuredGson()))
                .decoder(new GsonDecoder(configuredGson()))
                .target(BlogPostsApi.class, configuration.getApiUrl());

        blogsApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .decoder(new GsonDecoder(configuredGson()))
                .target(BlogsApi.class, configuration.getApiUrl());

    }

    @Override
    public BlogDetails listBlogs(int offset, int limit) throws HubspotApiException
    {
        return execute(() -> blogsApi.blogs(offset, limit));
    }

    @Override
    public BlogDetail getBlogById(String blogId) throws HubspotApiException
    {
        return execute(() -> blogsApi.blogById(blogId));
    }

    @Override
    public BlogPostDetails listBlogPosts(int offset, int limit, @NonNull BlogPostFilter filter, String orderBy) throws HubspotApiException
    {
        return execute(() -> blogPostsApi.blogPosts(filter.getArchived(), filter.getCampaign(), filter.getBlogId(),
                filter.getPostName(), filter.getSlug(), filter.getState() != null ? filter.getState().name() : null,
                offset, limit, orderBy));
    }

    @Override
    public BlogPostDetail getBlogPostById(String id) throws HubspotApiException
    {
        return execute(() -> blogPostsApi.blogPostDetail(id));
    }

    @Override
    public BlogPostDetail createBlogPost(BlogPostDetail blogPostDetail) throws HubspotApiException
    {
       return execute(() -> blogPostsApi.createBlogPost(blogPostDetail));
    }

    @Override
    public BlogPostDetail updateBlogPost(BlogPostDetail blogPostDetail) throws HubspotApiException
    {
        return execute(() -> blogPostsApi.updateBlogPost(blogPostDetail.getId(), blogPostDetail));
    }

    @Override
    public BlogPostDetail cloneBlogPost(String blogPostId, String name) throws HubspotApiException
    {
        return execute(() -> blogPostsApi.cloneBlogPost(blogPostId, new CloneBlogPostRequest(name)));
    }

    @Override
    public ResultInfo deleteBlogPost(String blogPostId) throws HubspotApiException
    {
        execute(() -> blogPostsApi.deleteBlogPost(blogPostId));
        ResultInfo result = new ResultInfo();
        result.setSucceeded(true);
        return result;
    }
}
