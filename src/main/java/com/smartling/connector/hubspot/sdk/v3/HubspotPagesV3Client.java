package com.smartling.connector.hubspot.sdk.v3;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.v3.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.v3.page.ListWrapper;
import com.smartling.connector.hubspot.sdk.v3.page.PageDetail;
import com.smartling.connector.hubspot.sdk.v3.page.SchedulePublishRequest;

import java.util.Map;

public interface HubspotPagesV3Client extends HubspotClient
{
    String getPageById(String pageId) throws HubspotApiException;

    String getPageDraftById(String pageId) throws HubspotApiException;

    PageDetail getPageDetailById(String pageId) throws HubspotApiException;

    PageDetail getPageDetailDraftById(String pageId) throws HubspotApiException;

    PageDetail createLanguageVariation(CreateLanguageVariationRequest createLanguageVariationRequest) throws HubspotApiException;

    String updatePage(String page, String updatePageId) throws HubspotApiException;

    String updatePageDraft(String page, String updatePageId) throws HubspotApiException;

    String pushLive(String updatePageId) throws HubspotApiException;

    ListWrapper<PageDetail> listPages(int offset, int limit, String orderBy, Map<String, Object> queryMap) throws HubspotApiException;

    void publish(SchedulePublishRequest publishActionRequest) throws HubspotApiException;

    ResultInfo delete(String pageId) throws HubspotApiException;
}
