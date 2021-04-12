package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotBlogPostsEntityClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.blog.*;
import com.smartling.connector.hubspot.sdk.common.Language;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.common.PublishActionRequest;
import com.smartling.connector.hubspot.sdk.rest.api.BlogPostsEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.BlogPostsEntityRawApi;
import com.smartling.connector.hubspot.sdk.rest.api.BlogsApi;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.Feign;
import feign.Request;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.NonNull;

import static java.lang.String.format;

public class HubspotRestBlogPostEntityClient extends AbstractHubspotRestClient implements HubspotBlogPostsEntityClient
{
    private final BlogPostsEntityApi entityApi;
    private final BlogPostsEntityRawApi entityRawApi;
    private final BlogsApi blogsApi;

    public HubspotRestBlogPostEntityClient(final Configuration configuration, final TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Request.Options connectionConfig = new Request.Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        entityApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .decoder(new GsonDecoder(camelCaseGson()))
                .encoder(new GsonEncoder(camelCaseGson()))
                .target(BlogPostsEntityApi.class, configuration.getApiUrl());

        entityRawApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .target(BlogPostsEntityRawApi.class, configuration.getApiUrl());

        blogsApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .decoder(new GsonDecoder(snakeCaseGson()))
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
    public ListWrapper<BlogPostDetail> listBlogPosts(int offset, int limit, @NonNull BlogPostFilter filter, String orderBy) throws HubspotApiException
    {
        return execute(() -> entityApi.listBlogPosts(
                filter.getArchived(),
                filter.getCampaign(),
                filter.getBlogId(),
                filter.getPostName(),
                filter.getSlug(),
                filter.getState() != null ? filter.getState().name() : null,
                offset,
                limit,
                orderBy)
        );
    }

    @Override
    public BlogPostDetail getBlogPostDetailBufferById(String id) throws HubspotApiException
    {
        return execute(() -> entityApi.blogPostDetailBuffer(id));
    }

    @Override
    public BlogPostDetail getBlogPostDetailById(String id) throws HubspotApiException
    {
        return execute(() -> entityApi.blogPostDetail(id));
    }

    @Override
    public ResultInfo deleteBlogPost(String blogPostId) throws HubspotApiException
    {
        return execute(() -> entityApi.delete(blogPostId));
    }

    @Override
    public String getBlogPost(String id) throws HubspotApiException
    {
        return execute(() -> entityRawApi.blogPost(id));
    }

    @Override
    public String getBlogPostBuffer(String id) throws HubspotApiException
    {
        return execute(() -> entityRawApi.blogPostBuffer(id));
    }

    @Override
    public String updateBlogPost(String id, String blogPostAsJson) throws HubspotApiException
    {
        return execute(() -> entityRawApi.update(id, blogPostAsJson));
    }

    @Override
    public String updateBlogPostBuffer(String blogPostAsJson, String updateBlogPostId) throws HubspotApiException
    {
        return execute(() -> entityRawApi.updateBuffer(updateBlogPostId, blogPostAsJson));
    }

    @Override
    public ListWrapper<Language> getSupportedLanguages() throws HubspotApiException
    {
        BlogDetails blogDetails = listBlogs(0, 1);
        if (blogDetails.getDetailList().size() != 1)
        {
            throw new HubspotApiException("Can't load languages as no blogs created");
        }
        String portalId = blogDetails.getDetailList().get(0).getPortalId();
        return execute(() -> entityApi.getSupportedLanguages(portalId));
    }

    @Override
    public void publish(String blogPostId, PublishActionRequest publishActionRequest) throws HubspotApiException
    {
        execute(() -> entityApi.publish(blogPostId, publishActionRequest));
    }

    @Override
    public BlogPostDetail createLanguageVariation(String blogPostId, String name, String language) throws HubspotApiException
    {
        BlogPostDetail blogPostDetails = getBlogPostDetailById(blogPostId);
        BlogDetail blog = getBlogById(Long.toString(blogPostDetails.getContentGroupId()));

        if (!blog.getTranslations().containsKey(language))
        {
            throw new HubspotApiException(format("Language %s not supported for blog with id %s", language, blog.getId()));
        }

        return execute(() -> entityApi.createLanguageVariation(new CreateLanguageVariationRequest(name, language, blog.getTranslations().get(language).getId(), blogPostId)));
    }
}
