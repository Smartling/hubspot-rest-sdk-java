package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.blog.BlogDetails;
import feign.Param;
import feign.RequestLine;

public interface BlogApi
{
    @RequestLine("GET /content/api/v2/blogs?offset={offset}&limit={limit}")
    BlogDetails blogs(@Param("offset") int offset, @Param("limit") int limit);
}
