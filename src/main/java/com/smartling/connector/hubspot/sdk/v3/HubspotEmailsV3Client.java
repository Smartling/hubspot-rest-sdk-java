package com.smartling.connector.hubspot.sdk.v3;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.v3.email.CloneEmailRequest;
import com.smartling.connector.hubspot.sdk.v3.email.EmailDetail;
import com.smartling.connector.hubspot.sdk.v3.email.ListWrapper;
import lombok.NonNull;

import java.util.Map;

/**
 * Interface for Hubspot Email API operations using V3 endpoints
 */
public interface HubspotEmailsV3Client extends HubspotClient
{
    /**
     * List emails with pagination and filtering options
     */
    ListWrapper<EmailDetail> listEmails(int limit, String after, String orderBy, Map<String, Object> queryMap) throws HubspotApiException;

    /**
     * Get email details by ID
     */
    EmailDetail getDetail(@NonNull String emailId) throws HubspotApiException;

    /**
     * Get draft version of email details by ID
     */
    EmailDetail getDraftDetail(@NonNull String emailId) throws HubspotApiException;

    /**
     * Get A/B test variation details for an email
     */
    EmailDetail getAbTestVariation(@NonNull String emailId) throws HubspotApiException;

    /**
     * Get raw content of an email
     */
    String getContent(@NonNull String emailId) throws HubspotApiException;

    /**
     * Update content of an email
     */
    String updateContent(@NonNull String emailId, @NonNull String emailContent) throws HubspotApiException;

    /**
     * Clone an email
     */
    EmailDetail clone(@NonNull CloneEmailRequest cloneEmailRequest) throws HubspotApiException;
}
