package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.rest.api.PageDetails;
import com.smartling.connector.hubspot.sdk.rest.api.RefreshTokenData;

public interface HubspotClient
{
    String getPageById(long pageId) throws HubspotApiException;

    String clonePage(long originalPageId) throws HubspotApiException;

    String updatePage(String page) throws HubspotApiException;

    PageDetails listPages(final int offset, final int limit) throws HubspotApiException;

    PageDetails listPagesByTmsId(final String tmsId) throws HubspotApiException;

    RefreshTokenData refreshToken() throws HubspotApiException;
}
