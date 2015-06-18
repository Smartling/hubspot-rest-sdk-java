package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.rest.api.Page;

public interface HubspotClient
{
    Page getPageById(long pageId);

    Page clonePage(long originalPageId);

    Page updatePage(Page page);
}
