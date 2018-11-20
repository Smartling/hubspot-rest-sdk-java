package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostFilter;

public interface HubspotBlogPostClient extends HubspotClient
{

    BlogPostDetails listBlogPosts(int offset, int limit) throws HubspotApiException;

    BlogPostDetails listBlogPosts(int offset, int limit, BlogPostFilter filter) throws HubspotApiException;

    BlogPostDetail getBlogPostById(String id) throws HubspotApiException;

    void createBlogPost(BlogPostDetail blogPostDetail);
}
