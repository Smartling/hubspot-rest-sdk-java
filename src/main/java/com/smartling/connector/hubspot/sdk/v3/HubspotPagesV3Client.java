package com.smartling.connector.hubspot.sdk.v3;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.v3.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.v3.page.PageDetail;
import com.smartling.connector.hubspot.sdk.v3.page.SchedulePublishRequest;

import java.util.Map;

public interface HubspotPagesV3Client extends HubspotClient
{
    String getPageById(long pageId) throws HubspotApiException;

    String getPageDraftById(long pageId) throws HubspotApiException;

    PageDetail getPageDetailById(long pageId) throws HubspotApiException;

    PageDetail getPageDetailDraftById(long pageId) throws HubspotApiException;

    PageDetail createLanguageVariation(CreateLanguageVariationRequest createLanguageVariationRequest) throws HubspotApiException;

    String updatePage(String page, long updatePageId) throws HubspotApiException;

    String updatePageDraft(String page, long updatePageId) throws HubspotApiException;

    ListWrapper<PageDetail> listPages(int offset, int limit, String orderBy, Map<String, Object> queryMap) throws HubspotApiException;

    void publish(SchedulePublishRequest publishActionRequest) throws HubspotApiException;

    ResultInfo delete(long pageId) throws HubspotApiException;
}
