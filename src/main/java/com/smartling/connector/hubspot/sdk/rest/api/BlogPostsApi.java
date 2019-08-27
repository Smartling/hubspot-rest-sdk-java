package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import com.smartling.connector.hubspot.sdk.blog.CloneBlogPostRequest;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface BlogPostsApi
{
    @RequestLine("GET /content/api/v2/blog-posts?archived={archived}&campaign={campaign}&content_group_id={content_group_id}&name__icontains={name}&slug={slug}&state={state}&offset={offset}&limit={limit}&order_by={order_by}")
    BlogPostDetails blogPosts(@Param("archived") Boolean archived, @Param("campaign") String campaign,
                              @Param("content_group_id") String blogId, @Param("name") String postName,
                              @Param("slug") String slug, @Param("state") String state,
                              @Param("offset") int offset, @Param("limit") int limit,
                              @Param("order_by") String orderBy);

    @RequestLine("GET /content/api/v2/blog-posts/{blog_post_id}")
    BlogPostDetail blogPostDetail(@Param("blog_post_id") String blogPostId);

    @RequestLine("POST /content/api/v2/blog-posts")
    @Headers("Content-Type: application/json")
    BlogPostDetail createBlogPost(BlogPostDetail postDetail);

    @RequestLine("PUT /content/api/v2/blog-posts/{blog_post_id}")
    @Headers("Content-Type: application/json")
    BlogPostDetail updateBlogPost(@Param("blog_post_id") String blogPostId, BlogPostDetail postDetail);

    @RequestLine("POST /content/api/v2/blog-posts/{blog_post_id}/clone")
    @Headers("Content-Type: application/json")
    BlogPostDetail cloneBlogPost(@Param("blog_post_id") String blogPostId, CloneBlogPostRequest cloneBlogPostRequest);

    @RequestLine("DELETE /content/api/v2/blog-posts/{blog_post_id}")
    ResultInfo deleteBlogPost(@Param("blog_post_id") String blogPostId);
}
