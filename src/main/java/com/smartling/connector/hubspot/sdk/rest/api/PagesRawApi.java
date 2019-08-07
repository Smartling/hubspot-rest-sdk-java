package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.page.CreateLanguageVariationRequest;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface PagesRawApi
{
    @RequestLine("GET /content/api/v2/pages/{page_id}")
    String page(@Param("page_id") long pageId);

    @RequestLine("POST /content/api/v2/pages/{page_id}/clone")
    @Headers("Content-Type: application/json")
    String clone(@Param("page_id") long pageId, String pageAsJson);

    @RequestLine("POST /content/api/v2/pages/{page_id}/create-language-variation")
    @Headers("Content-Type: application/json")
    String createLanguageVariation(@Param("page_id") long pageId, CreateLanguageVariationRequest createLanguageVariationRequest);

    @RequestLine("PUT /content/api/v2/pages/{page_id}")
    @Headers("Content-Type: application/json")
    String update(@Param("page_id") long pageId, String pageAsJson);
}
