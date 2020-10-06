package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.page.Language;
import com.smartling.connector.hubspot.sdk.page.PageDetail;
import com.smartling.connector.hubspot.sdk.page.PublishActionRequest;

import java.util.Map;

public interface HubspotPagesClient extends HubspotClient
{
    String getPageById(long pageId) throws HubspotApiException;

    String getPageBufferById(long pageId) throws HubspotApiException;

    PageDetail getPageDetailById(long pageId) throws HubspotApiException;

    PageDetail getPageDetailBufferById(long pageId) throws HubspotApiException;

    String clonePage(long originalPageId) throws HubspotApiException;

    PageDetail createLanguageVariation(long pageId, CreateLanguageVariationRequest createLanguageVariationRequest) throws HubspotApiException;

    PageDetail clonePageAsDetail(long originalPageId) throws HubspotApiException;

    String updatePage(final String page, final long updatePageId) throws HubspotApiException;

    String updatePageBuffer(String page, long updatePageId) throws HubspotApiException;

    ListWrapper<PageDetail> listPages(int offset, int limit, String orderBy, Map<String, Object> queryMap) throws HubspotApiException;

    ResultInfo delete(long pageId) throws HubspotApiException;

    ListWrapper<Language> getSupportedLanguages() throws HubspotApiException;

    void publish(long pageId, PublishActionRequest publishActionRequest) throws HubspotApiException;
}
