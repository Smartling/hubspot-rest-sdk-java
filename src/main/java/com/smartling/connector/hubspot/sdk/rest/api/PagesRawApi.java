package com.smartling.connector.hubspot.sdk.rest.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface PagesRawApi
{
    @RequestLine("GET /content/api/v2/pages/{page_id}?access_token={access_token}")
    String page(@Param("page_id") long pageId, @Param("access_token") String accessToken);

    @RequestLine("POST /content/api/v2/pages/{page_id}/clone?access_token={access_token}")
    @Headers("Content-Type: application/json")
    String clone(@Param("page_id") long pageId, @Param("access_token") String accessToken, String pageAsJson);

    @RequestLine("PUT /content/api/v2/pages/{page_id}?access_token={access_token}")
    @Headers("Content-Type: application/json")
    String update(@Param("page_id") long pageId, String pageAsJson, @Param("access_token") String accessToken);
}
