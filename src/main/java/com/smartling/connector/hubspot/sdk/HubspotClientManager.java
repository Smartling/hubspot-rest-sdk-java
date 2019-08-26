package com.smartling.connector.hubspot.sdk;

public interface HubspotClientManager
{
    HubspotPagesClient getPagesClient();
    HubspotFormsClient getFormsClient();
    HubspotBlogPostsClient getBlogPostsClient();
    HubspotEmailsClient getMarketingEmailsClient();
}
