package com.smartling.connector.hubspot.sdk.rest.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface BlogPostsEntityRawApi
{
    @RequestLine("GET /blogs/v3/blog-posts/{blog_post_id}")
    String blogPost(@Param("blog_post_id") String blogPostId);

    @RequestLine("GET /blogs/v3/blog-posts/{blog_post_id}/buffer")
    String blogPostBuffer(@Param("blog_post_id") String blogPostId);

    @RequestLine("PUT /blogs/v3/blog-posts/{blog_post_id}")
    @Headers("Content-Type: application/json")
    String update(@Param("blog_post_id") String blogPostId, String blogPostAsJson);

    @RequestLine("PUT /blogs/v3/blog-posts/{blog_post_id}/buffer")
    @Headers("Content-Type: application/json")
    String updateBuffer(@Param("blog_post_id") String blogPostId, String blogPostAsJson);
}
