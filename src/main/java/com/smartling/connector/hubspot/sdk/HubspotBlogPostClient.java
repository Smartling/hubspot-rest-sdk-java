package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostFilter;

public interface HubspotBlogPostClient extends HubspotClient
{

    BlogPostDetails listBlogPosts(int limit, int offset) throws HubspotApiException;

    BlogPostDetails listBlogPosts(int limit, int offset, BlogPostFilter filter) throws HubspotApiException;

    BlogPostDetail getBlogPostById(Long id) throws HubspotApiException;

    void createBlogPost(BlogPostDetail blogPostDetail);
}
