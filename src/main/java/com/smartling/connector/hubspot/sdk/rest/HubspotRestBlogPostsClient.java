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
import com.smartling.connector.hubspot.sdk.logger.FeignLogger;
import com.smartling.connector.hubspot.sdk.rest.api.BlogPostsApi;
import com.smartling.connector.hubspot.sdk.rest.api.BlogPostsRawApi;
import com.smartling.connector.hubspot.sdk.rest.api.BlogsApi;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.Feign;
import feign.Logger;
import feign.Request.Options;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.NonNull;

public class HubspotRestBlogPostsClient extends AbstractHubspotRestClient implements HubspotBlogPostsClient
{
    private final BlogPostsApi blogPostsApi;
    private final BlogsApi blogsApi;
    private final BlogPostsRawApi blogPostsRawApi;

    public HubspotRestBlogPostsClient(final Configuration configuration, final TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Options connectionConfig = new Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        blogPostsApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .encoder(new GsonEncoder(snakeCaseGson()))
                .decoder(new GsonDecoder(snakeCaseGson()))
                .logger(new FeignLogger(BlogPostsApi.class))
                .logLevel(Logger.Level.FULL)
                .target(BlogPostsApi.class, configuration.getApiUrl());

        blogsApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .decoder(new GsonDecoder(snakeCaseGson()))
                .logger(new FeignLogger(BlogsApi.class))
                .logLevel(Logger.Level.FULL)
                .target(BlogsApi.class, configuration.getApiUrl());

        blogPostsRawApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .logger(new FeignLogger(BlogPostsRawApi.class))
                .logLevel(Logger.Level.FULL)
                .target(BlogPostsRawApi.class, configuration.getApiUrl());

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
    public ResultInfo deleteBlogPost(String blogPostId) throws HubspotApiException
    {
        execute(() -> blogPostsApi.deleteBlogPost(blogPostId));
        ResultInfo result = new ResultInfo();
        result.setSucceeded(true);
        return result;
    }

    @Override
    public String getBlogPost(String id) throws HubspotApiException
    {
        return execute(() -> blogPostsRawApi.blogPost(id));
    }

    @Override
    public String updateBlogPost(String id, String blogPostAsJson) throws HubspotApiException
    {
        return execute(() -> blogPostsRawApi.update(id, blogPostAsJson));
    }

    @Override
    public BlogPostDetail cloneBlogPost(String blogPostId) throws HubspotApiException
    {
        return execute(() -> blogPostsApi.cloneBlogPost(blogPostId, new CloneBlogPostRequest()));
    }
}
