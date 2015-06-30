package com.smartling.connector.hubspot.sdk;

public interface HubspotClient
{
    String getPageById(long pageId) throws HubspotApiException;

    PageDetail getPageDetailById(long pageId) throws HubspotApiException;

    String clonePage(long originalPageId) throws HubspotApiException;

    PageDetail clonePageAsDetail(long originalPageId) throws HubspotApiException;

    String updatePage(String page) throws HubspotApiException;

    PageDetails listPages(final int offset, final int limit) throws HubspotApiException;

    PageDetails listPagesByTmsId(final String tmsId) throws HubspotApiException;

    RefreshTokenData refreshToken() throws HubspotApiException;

    DeletePageInfo delete(long pageId) throws HubspotApiException;
}
