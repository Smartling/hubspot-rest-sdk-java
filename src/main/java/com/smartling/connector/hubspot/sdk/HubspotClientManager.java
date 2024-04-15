package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.v3.HubspotPagesV3Client;
import com.smartling.connector.hubspot.sdk.v3.page.PageType;

public interface HubspotClientManager
{
    HubspotPagesClient getPagesClient();
    HubspotPagesV3Client getPagesV3Client(PageType pageType);
    HubspotFormsClient getFormsClient();
    HubspotBlogPostsClient getBlogPostsClient();
    HubspotEmailsClient getEmailsClient();
    HubspotBlogPostsEntityClient getBlogPostsEntityClient();
}
