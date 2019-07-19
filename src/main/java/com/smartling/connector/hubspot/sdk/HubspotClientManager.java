package com.smartling.connector.hubspot.sdk;

public interface HubspotClientManager
{
    HubspotPageClient getPageClient();
    HubspotFormClient getFormClient();
    HubspotBlogPostClient getBlogPostClient();
    HubspotMarketingEmailClient getMarketingEmailClient();
}
