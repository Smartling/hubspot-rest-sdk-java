package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.email.EmailDetail;
import lombok.NonNull;

import java.util.Map;

public interface HubspotEmailsClient extends HubspotClient
{
    ListWrapper<EmailDetail> listEmails(int offset, int limit, String orderBy, Map<String, Object> queryMap) throws HubspotApiException;

    EmailDetail getDetail(@NonNull String emailId) throws HubspotApiException;

    String getContent(@NonNull String emailId) throws HubspotApiException;

    String updateContent(@NonNull String emailId, @NonNull String emailContent) throws HubspotApiException;

    String updateAbContent(@NonNull String emailId, @NonNull String emailContent) throws HubspotApiException;

    EmailDetail clone(@NonNull String emailId, @NonNull String name) throws HubspotApiException;

    EmailDetail createVariation(@NonNull String emailId, @NonNull String variationName) throws HubspotApiException;

    EmailDetail getBufferedDetail(@NonNull String emailId) throws HubspotApiException;
}
