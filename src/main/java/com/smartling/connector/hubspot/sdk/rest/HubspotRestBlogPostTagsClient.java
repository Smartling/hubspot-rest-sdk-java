package com.smartling.connector.hubspot.sdk.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotBlogTagsClient;
import com.smartling.connector.hubspot.sdk.blog.BlogTagDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogTagDetails;
import com.smartling.connector.hubspot.sdk.blog.CloneBlogPostTagRequest;
import com.smartling.connector.hubspot.sdk.blog.UpdateBlogPostTagRequest;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import com.smartling.connector.hubspot.sdk.rest.util.InstantTypeAdapter;
import com.smartling.connector.hubspot.sdk.v3.rest.api.BlogPostTagsApi;
import feign.Feign;
import feign.Request;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.httpclient.ApacheHttpClient;

import java.time.Instant;

public class HubspotRestBlogPostTagsClient extends AbstractHubspotRestClient implements HubspotBlogTagsClient
{

    private final BlogPostTagsApi blogPostTagsApi;

    public HubspotRestBlogPostTagsClient(final Configuration configuration, final TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Request.Options connectionConfig = new Request.Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .create();

        blogPostTagsApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .decoder(new GsonDecoder(gson))
                .encoder(new GsonEncoder(gson))
                .client(new ApacheHttpClient())
                .target(BlogPostTagsApi.class,   configuration.getApiUrl());
    }

    @Override
    public BlogTagDetails listBlogTags(int limit, String after, String sort) throws HubspotApiException {
        return execute(() -> blogPostTagsApi.blogPostTagList(limit, after, sort));
    }

    @Override
    public BlogTagDetail blogPostTag(String tagId) throws HubspotApiException {
        return execute(() -> blogPostTagsApi.blogPostTag(tagId));
    }

    @Override
    public BlogTagDetail createLanguageVariation(CloneBlogPostTagRequest cloneBlogPostTagRequest) throws HubspotApiException {
        return execute(() -> blogPostTagsApi.createLanguageVariation(cloneBlogPostTagRequest));
    }

    @Override
    public BlogTagDetail updateBlogPostTag(String tagId, UpdateBlogPostTagRequest updateBlogPostTagRequest) throws HubspotApiException {
        return execute(() -> blogPostTagsApi.updateBlogPostTag(tagId, updateBlogPostTagRequest));
    }

    @Override
    public void deleteBlogPostTag(String tagId) throws HubspotApiException {
        execute(() -> blogPostTagsApi.deleteBlogPostTag(tagId));
    }
}
