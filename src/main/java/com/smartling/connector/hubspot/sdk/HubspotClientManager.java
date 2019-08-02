package com.smartling.connector.hubspot.sdk;

public interface HubspotClientManager
{
    HubspotPagesClient getPagesClient();
    HubspotFormClient getFormClient();
    HubspotBlogPostClient getBlogPostClient();
}
