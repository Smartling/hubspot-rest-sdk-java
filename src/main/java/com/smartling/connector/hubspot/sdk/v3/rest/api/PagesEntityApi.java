package com.smartling.connector.hubspot.sdk.v3.rest.api;

import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.v3.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.v3.page.ListWrapper;
import com.smartling.connector.hubspot.sdk.v3.page.PageDetail;
import com.smartling.connector.hubspot.sdk.v3.page.SchedulePublishRequest;
import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface PagesEntityApi
{
    @RequestLine("GET /cms/v3/pages/{pageType}?limit={limit}&offset={offset}&sort={sortBy}&property=" + PageDetail.FIELDS)
    ListWrapper<PageDetail> listPages(@Param("pageType") String pageType, @Param("limit") int limit, @Param("offset") int offset, @Param("sortBy") String sortBy, @QueryMap Map<String, Object> queryMap);

    @RequestLine("GET /cms/v3/pages/{pageType}/{pageId}?property=" + PageDetail.FIELDS)
    PageDetail pageDetail(@Param("pageType") String pageType, @Param("pageId") String pageId);

    @RequestLine("GET /cms/v3/pages/{pageType}/{pageId}/draft?property=" + PageDetail.FIELDS)
    PageDetail pageDetailDraft(@Param("pageType") String pageType, @Param("pageId") String pageId);

    @RequestLine("POST /cms/v3/pages/{pageType}/multi-language/create-language-variation")
    @Headers("Content-Type: application/json")
    PageDetail createLanguageVariation(@Param("pageType") String pageType, CreateLanguageVariationRequest createLanguageVariationRequest);

    @RequestLine("POST /cms/v3/pages/{pageType}/schedule")
    @Headers("Content-Type: application/json")
    void publish(@Param("pageType") String pageType, SchedulePublishRequest schedulePublishRequest);

    @RequestLine("DELETE /cms/v3/pages/{pageType}/{pageId}")
    ResultInfo delete(@Param("pageType") String pageType, @Param("pageId") String pageId);
}
