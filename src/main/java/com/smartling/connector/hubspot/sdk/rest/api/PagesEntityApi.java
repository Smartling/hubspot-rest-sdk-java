package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.page.Language;
import com.smartling.connector.hubspot.sdk.page.PageDetail;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface PagesEntityApi
{
    @RequestLine("GET /content/api/v2/pages?limit={limit}&offset={offset}&order={order_by}&property=" + PageDetail.FIELDS)
    ListWrapper<PageDetail> listPages(@Param("limit") int limit, @Param("offset") int offset, @Param("order_by") String orderBy, @QueryMap Map<String, Object> queryMap);

    @RequestLine("DELETE /content/api/v2/pages/{page_id}")
    ResultInfo delete(@Param("page_id") long pageId);

    @RequestLine("GET /content/api/v2/pages/{page_id}?property=" + PageDetail.FIELDS)
    PageDetail pageDetail(@Param("page_id") long pageId);

    @RequestLine("POST /content/api/v2/pages/{page_id}/clone?property=" + PageDetail.FIELDS)
    @Headers("Content-Type: application/json")
    PageDetail clone(@Param("page_id") long pageId, String pageAsJson);

    @RequestLine("POST /content/api/v2/pages/{page_id}/create-language-variation")
    @Headers("Content-Type: application/json")
    PageDetail createLanguageVariation(@Param("page_id") long pageId, CreateLanguageVariationRequest createLanguageVariationRequest);

    @RequestLine("GET /content/api/v2/pages/supported-languages")
    ListWrapper<Language> getSupportedLanguages();
}
