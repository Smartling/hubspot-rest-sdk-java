package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface BlogPostApi
{
    @RequestLine("GET /content/api/v2/blog-posts?archived={archived}&campaign={campaign}&content_group_id={content_group_id}&name={name}&slug={slug}&state={state}&offset={offset}&limit={limit}&order_by={order_by}")
    BlogPostDetails blogPosts(@Param("archived") Boolean archived, @Param("campaign") String campaign,
                              @Param("content_group_id") String blogId, @Param("name") String postName,
                              @Param("slug") String slug, @Param("state") String state,
                              @Param("offset") int offset, @Param("limit") int limit,
                              @Param("order_by") String orderBy);

    @RequestLine("GET /content/api/v2/blog-posts/{blog_post_id}")
    BlogPostDetail blogPostDetail(@Param("blog_post_id") String blogPostId);

    @RequestLine("POST /content/api/v2/blog-posts")
    @Headers("Content-Type: application/json")
    Void createBlogPost(BlogPostDetail postDetail);

    @RequestLine("PUT /content/api/v2/blog-posts/{blog_post_id}")
    @Headers("Content-Type: application/json")
    Void updateBlogPost(@Param("blog_post_id") String blogPostId, BlogPostDetail postDetail);
}
