package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.rest.api.PageDetails;

public interface HubspotClient
{
    String getPageById(long pageId);

    String clonePage(long originalPageId);

    String updatePage(String page);

    PageDetails listPages(final int limit, final int offset);
}
