package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.blog.*;
import lombok.NonNull;

public interface HubspotBlogPostsClient extends HubspotClient
{
    BlogDetails listBlogs(int offset, int limit) throws HubspotApiException;

    BlogDetail getBlogById(String blogId) throws HubspotApiException;

    BlogPostDetails listBlogPosts(int offset, int limit, @NonNull BlogPostFilter filter, String orderBy) throws HubspotApiException;

    BlogPostDetail getBlogPostById(String id) throws HubspotApiException;

    ResultInfo deleteBlogPost(String blogPostId) throws HubspotApiException;

    String getBlogPost(String id) throws HubspotApiException;

    String updateBlogPost(String id, String blogPostAsJson) throws HubspotApiException;

    BlogPostDetail cloneBlogPost(String blogPostId) throws HubspotApiException;

}
