package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.blog.BlogTagDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogTagDetails;
import com.smartling.connector.hubspot.sdk.blog.CloneBlogPostTagRequest;
import com.smartling.connector.hubspot.sdk.blog.UpdateBlogPostTagRequest;

public interface HubspotBlogTagsClient extends HubspotClient
{
    BlogTagDetails listBlogTags(int limit, String after, String sort) throws HubspotApiException;
    BlogTagDetail blogPostTag(String tagId) throws HubspotApiException;
    BlogTagDetail createLanguageVariation(CloneBlogPostTagRequest cloneBlogPostTagRequest) throws HubspotApiException;
    BlogTagDetail updateBlogPostTag(String tagId, UpdateBlogPostTagRequest updateBlogPostTagRequest) throws HubspotApiException;
    void deleteBlogPostTag(String tagId) throws HubspotApiException;
}
