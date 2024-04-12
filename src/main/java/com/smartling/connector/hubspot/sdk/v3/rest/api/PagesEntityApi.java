package com.smartling.connector.hubspot.sdk.v3.rest.api;

import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.v3.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.v3.page.PageDetail;
import com.smartling.connector.hubspot.sdk.v3.page.PageType;
import com.smartling.connector.hubspot.sdk.v3.page.SchedulePublishRequest;
import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface PagesEntityApi
{
    @RequestLine("GET /cms/v3/pages/{pageType}?limit={limit}&offset={offset}&order={orderBy}&property=" + PageDetail.FIELDS)
    ListWrapper<PageDetail> listPages(@Param("pageType") String pageType, @Param("limit") int limit, @Param("offset") int offset, @Param("orderBy") String orderBy, @QueryMap Map<String, Object> queryMap);

    @RequestLine("GET /cms/v3/pages/{pageType}/{pageId}?property=" + PageDetail.FIELDS)
    PageDetail pageDetail(@Param("pageType") String pageType, @Param("pageId") long pageId);

    @RequestLine("GET /cms/v3/pages/{pageType}/{pageId}/draft?property=" + PageDetail.FIELDS)
    PageDetail pageDetailDraft(@Param("pageType") String pageType, @Param("pageId") long pageId);

    @RequestLine("POST /cms/v3/pages/{pageType}/multi-language/create-language-variation")
    @Headers("Content-Type: application/json")
    PageDetail createLanguageVariation(@Param("pageType") String pageType, CreateLanguageVariationRequest createLanguageVariationRequest);

    @RequestLine("POST /cms/v3/pages/{pageType}/schedule")
    @Headers("Content-Type: application/json")
    void publish(@Param("pageType") String pageType, SchedulePublishRequest schedulePublishRequest);

    @RequestLine("DELETE /cms/v3/pages/{pageType}/{pageId}")
    ResultInfo delete(@Param("pageType") String pageType, @Param("pageId") long pageId);
}
