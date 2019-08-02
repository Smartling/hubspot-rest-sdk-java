package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.page.PageDetail;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface PagesEntityApi
{
    @RequestLine("GET /content/api/v2/pages?limit={limit}&offset={offset}&order={order_by}&property=id,name,current_state,archived,created,updated,slug,language,html_title,subcategory,campaign,campaign_name,url,folder_id,portal_id,translated_content")
    ListWrapper<PageDetail> listPages(@Param("limit") int limit, @Param("offset") int offset, @Param("order_by") String orderBy, @QueryMap Map<String, Object> queryMap);

    @RequestLine("DELETE /content/api/v2/pages/{page_id}")
    ResultInfo delete(@Param("page_id") long pageId);

    @RequestLine("GET /content/api/v2/pages/{page_id}?property=id,name,current_state,archived,created,updated,slug,language,html_title,subcategory,campaign,campaign_name,url,folder_id,portal_id,translated_content")
    PageDetail pageDetail(@Param("page_id") long pageId);

    @RequestLine("POST /content/api/v2/pages/{page_id}/clone?property=id,name,current_state,archived,created,updated,slug,language,html_title,subcategory,campaign,campaign_name,url,folder_id,portal_id,translated_content")
    @Headers("Content-Type: application/json")
    PageDetail clone(@Param("page_id") long pageId, String pageAsJson);
}
