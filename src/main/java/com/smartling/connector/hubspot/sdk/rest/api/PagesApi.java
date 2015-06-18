package com.smartling.connector.hubspot.sdk.rest.api;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface PagesApi
{
    @RequestLine("GET /content/api/v2/pages/{page_id}?access_token={access_token}")
    Page page(@Param("page_id") long pageId, @Param("access_token") String accessToken);

    @RequestLine("POST /content/api/v2/pages/{page_id}/clone?access_token={access_token}")
    Page clone(@Param("page_id") long pageId, @Param("access_token") String accessToken);

    @RequestLine("PUT /content/api/v2/pages/{page_id}?access_token={access_token}")
    @Headers("Content-Type: application/json")
    @Body("{page}")
    Page update(@Param("page_id") long pageId, @Param("access_token") String accessToken, @Param(value = "page", expander = Page.Expander.class) Page page);
}