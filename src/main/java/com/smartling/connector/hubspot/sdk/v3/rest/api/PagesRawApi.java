package com.smartling.connector.hubspot.sdk.v3.rest.api;

import com.smartling.connector.hubspot.sdk.v3.page.PageType;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface PagesRawApi
{
    @RequestLine("GET /cms/v3/pages/{pageType}/{pageId}")
    String page(@Param("pageType") String pageType, @Param("pageId") long pageId);

    @RequestLine("GET /cms/v3/pages/{pageType}/{pageId}/draft")
    String pageDraft(@Param("pageType") String pageType, @Param("pageId") long pageId);

    @RequestLine("PATCH /cms/v3/pages/{pageType}/{pageId}")
    @Headers("Content-Type: application/json")
    String update(@Param("pageType") String pageType, @Param("pageId") long pageId, String pageAsJson);

    @RequestLine("PATCH /cms/v3/pages/{pageType}/{pageId}/draft")
    @Headers("Content-Type: application/json")
    String updateDraft(@Param("pageType") String pageType, @Param("pageId") long pageId, String pageAsJson);
}
