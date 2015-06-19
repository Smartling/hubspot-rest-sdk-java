package com.smartling.connector.hubspot.sdk;

public interface HubspotClient
{
    String getPageById(long pageId);

    String clonePage(long originalPageId);

    String updatePage(String page);
}
