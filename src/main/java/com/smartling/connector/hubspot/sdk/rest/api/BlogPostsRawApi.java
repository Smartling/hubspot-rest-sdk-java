package com.smartling.connector.hubspot.sdk.rest.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface BlogPostsRawApi
{
    @RequestLine("GET /content/api/v2/blog-posts/{blog_post_id}")
    String blogPost(@Param("blog_post_id") String blogPostId);

    @RequestLine("POST /content/api/v2/blog-posts")
    @Headers("Content-Type: application/json")
    String create(String blogPostAsJson);

    @RequestLine("PUT /content/api/v2/blog-posts/{blog_post_id}")
    @Headers("Content-Type: application/json")
    String update(@Param("blog_post_id") String blogPostId, String blogPostAsJson);
}
