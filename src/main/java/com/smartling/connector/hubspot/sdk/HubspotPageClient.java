package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.page.PageDetail;
import com.smartling.connector.hubspot.sdk.page.PageDetails;
import com.smartling.connector.hubspot.sdk.page.PageSearchFilter;

public interface HubspotPageClient extends HubspotClient
{
    String getPageById(long pageId) throws HubspotApiException;

    PageDetail getPageDetailById(long pageId) throws HubspotApiException;

    String clonePage(long originalPageId) throws HubspotApiException;

    PageDetail clonePageAsDetail(long originalPageId) throws HubspotApiException;

    String updatePage(final String page, final long updatePageId) throws HubspotApiException;

    PageDetails listPages(final int offset, final int limit) throws HubspotApiException;

    PageDetails listPages(int offset, int limit, PageSearchFilter filter) throws HubspotApiException;

    PageDetails listPagesByTmsId(final String tmsId) throws HubspotApiException;

    ResultInfo delete(long pageId) throws HubspotApiException;
}
