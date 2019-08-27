package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.blog.BlogDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostFilter;
import lombok.NonNull;

public interface HubspotBlogPostsClient extends HubspotClient
{
    BlogDetails listBlogs(int offset, int limit) throws HubspotApiException;

    BlogDetail getBlogById(String blogId) throws HubspotApiException;

    BlogPostDetails listBlogPosts(int offset, int limit, @NonNull BlogPostFilter filter, String orderBy) throws HubspotApiException;

    BlogPostDetail getBlogPostById(String id) throws HubspotApiException;

    BlogPostDetail createBlogPost(BlogPostDetail blogPostDetail) throws HubspotApiException;

    BlogPostDetail updateBlogPost(BlogPostDetail blogPostDetail) throws HubspotApiException;

    BlogPostDetail cloneBlogPost(String blogPostId, String name) throws HubspotApiException;

    ResultInfo deleteBlogPost(String blogPostId) throws HubspotApiException;
}
