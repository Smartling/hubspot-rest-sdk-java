package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.page.PageDetail;
import com.smartling.connector.hubspot.sdk.page.PageDetails;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface PagesEntityApi
{
    @RequestLine("GET /content/api/v2/pages?limit={limit}&offset={offset}")
    PageDetails pages(@Param("limit") int limit, @Param("offset") int offset);

    @RequestLine("GET /content/api/v2/pages?limit={limit}&offset={offset}&name__icontains={name}"
            + "&campaign={campaign}&archived={archived}&is_draft={is_draft}")
    PageDetails pages(@Param("archived") Boolean archived, @Param("is_draft") Boolean draft,
            @Param("name") String name, @Param("campaign") String campaign, @Param("limit") int limit,
            @Param("offset") int offset);

    @RequestLine("GET /content/api/v2/pages?tms_id={tms_id}")
    PageDetails findByTmsId(@Param("tms_id") String tmsId);

    @RequestLine("DELETE /content/api/v2/pages/{page_id}")
    ResultInfo delete(@Param("page_id") long pageId);

    @RequestLine("GET /content/api/v2/pages/{page_id}")
    PageDetail pageDetail(@Param("page_id") long pageId);

    @RequestLine("POST /content/api/v2/pages/{page_id}/clone")
    @Headers("Content-Type: application/json")
    PageDetail clone(@Param("page_id") long pageId, String pageAsJson);
}
