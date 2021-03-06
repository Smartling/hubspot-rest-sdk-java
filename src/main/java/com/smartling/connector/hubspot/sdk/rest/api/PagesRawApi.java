package com.smartling.connector.hubspot.sdk.rest.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface PagesRawApi
{
    @RequestLine("GET /content/api/v2/pages/{page_id}")
    String page(@Param("page_id") long pageId);

    @RequestLine("GET /content/api/v2/pages/{page_id}/buffer")
    String pageBuffer(@Param("page_id") long pageId);

    @RequestLine("POST /content/api/v2/pages/{page_id}/clone")
    @Headers("Content-Type: application/json")
    String clone(@Param("page_id") long pageId, String pageAsJson);

    @RequestLine("PUT /content/api/v2/pages/{page_id}")
    @Headers("Content-Type: application/json")
    String update(@Param("page_id") long pageId, String pageAsJson);

    @RequestLine("PUT /content/api/v2/pages/{page_id}/buffer")
    @Headers("Content-Type: application/json")
    String updateBuffer(@Param("page_id") long pageId, String pageAsJson);
}
