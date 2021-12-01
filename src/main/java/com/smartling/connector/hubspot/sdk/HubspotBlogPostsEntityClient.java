package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.blog.BlogDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostFilter;
import com.smartling.connector.hubspot.sdk.common.Language;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.common.PublishActionRequest;
import lombok.NonNull;

public interface HubspotBlogPostsEntityClient extends HubspotClient
{
    BlogDetails listBlogs(int offset, int limit) throws HubspotApiException;

    BlogDetail getBlogById(String blogId) throws HubspotApiException;

    ListWrapper<BlogPostDetail> listBlogPosts(int offset, int limit, @NonNull BlogPostFilter filter, String orderBy) throws HubspotApiException;

    BlogPostDetail getBlogPostDetailById(String id) throws HubspotApiException;

    BlogPostDetail getBlogPostDetailBufferById(String id) throws HubspotApiException;

    String getBlogPost(String id) throws HubspotApiException;

    String getBlogPostBuffer(String id) throws HubspotApiException;

    String updateBlogPost(String id, String blogPostAsJson) throws HubspotApiException;

    String updateBlogPostBuffer(String blogPostAsJson, String updateBlogPostId) throws HubspotApiException;

    ListWrapper<Language> getSupportedLanguages() throws HubspotApiException;

    void publish(String blogPostId, PublishActionRequest publishActionRequest) throws HubspotApiException;

    BlogPostDetail createLanguageVariation(String blogPostId, String language) throws HubspotApiException;
}
