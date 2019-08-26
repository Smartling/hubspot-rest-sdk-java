package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.email.EmailDetail;
import com.smartling.connector.hubspot.sdk.email.EmailDetails;
import lombok.NonNull;

import java.util.Map;

public interface HubspotEmailsClient extends HubspotClient
{
    EmailDetails listEmails(int offset, int limit, String orderBy, Map<String, Object> queryMap) throws HubspotApiException;

    EmailDetail getDetail(@NonNull String emailId) throws HubspotApiException;

    String getContent(@NonNull String emailId) throws HubspotApiException;

    String updateContent(@NonNull String emailId, @NonNull String emailContent) throws HubspotApiException;

    EmailDetail clone(@NonNull String emailId, @NonNull String name) throws HubspotApiException;
}
