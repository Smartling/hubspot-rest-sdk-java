package com.smartling.connector.hubspot.sdk.v3.rest.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface PagesRawApi
{
    @RequestLine("GET /cms/v3/pages/{pageType}/{pageId}")
    String page(@Param("pageType") String pageType, @Param("pageId") String pageId);

    @RequestLine("GET /cms/v3/pages/{pageType}/{pageId}/draft")
    String pageDraft(@Param("pageType") String pageType, @Param("pageId") String pageId);

    @RequestLine("PATCH /cms/v3/pages/{pageType}/{pageId}")
    @Headers("Content-Type: application/json")
    String update(@Param("pageType") String pageType, @Param("pageId") String pageId, String pageAsJson);

    @RequestLine("PATCH /cms/v3/pages/{pageType}/{pageId}/draft")
    @Headers("Content-Type: application/json")
    String updateDraft(@Param("pageType") String pageType, @Param("pageId") String pageId, String pageAsJson);

    @RequestLine("POST /cms/v3/pages/{pageType}/{pageId}/draft/push-live")
    @Headers("Content-Type: application/json")
    String pushLive(@Param("pageType") String pageType, @Param("pageId") String pageId);
}
