package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.blog.BlogDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogDetails;
import feign.Param;
import feign.RequestLine;

public interface BlogsApi
{
    @RequestLine("GET /content/api/v2/blogs?offset={offset}&limit={limit}")
    BlogDetails blogs(@Param("offset") int offset, @Param("limit") int limit);

    @RequestLine("GET /content/api/v2/blogs/{blog_id}")
    BlogDetail blogById(@Param("blog_id") String blogId);
}
