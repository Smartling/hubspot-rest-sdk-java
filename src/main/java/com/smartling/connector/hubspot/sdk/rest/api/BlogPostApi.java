package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface BlogPostApi
{
    @RequestLine("GET /content/api/v2/blog-posts?limit={limit}&offset={offset}")
    BlogPostDetails blogPosts(@Param("limit") int limit, @Param("offset") int offset);

    @RequestLine("GET /content/api/v2/blog-posts?limit={limit}&offset={offset}&archived={archived}&campaign={campaign}&content_group_id={content_group_id}&name={name}&slug={slug}&state={state}")
    BlogPostDetails blogPosts(@Param("limit") int limit, @Param("offset") int offset, @Param("archived") Boolean archived, @Param("campaign") String campaign,
                              @Param("content_group_id") Long blogId, @Param("name") String postName, @Param("slug") String slug, @Param("state") String state);

    @RequestLine("GET /content/api/v2/blog-posts/{blog_post_id}")
    BlogPostDetail blogPostDetail(@Param("blog_post_id") Long blogPostId);

    @RequestLine("POST /content/api/v2/blog-posts")
    @Headers("Content-Type: application/json")
    void createBlogPost(BlogPostDetail postDetail);
}
