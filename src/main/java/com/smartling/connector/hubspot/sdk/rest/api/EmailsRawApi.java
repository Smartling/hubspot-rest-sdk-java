package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.email.CreateVariationRequest;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface EmailsRawApi
{
    @RequestLine("GET /marketing-emails/v1/emails/{email_id}")
    String getContent(@Param("email_id") String emailId);

    @RequestLine("PUT /marketing-emails/v1/emails/{email_id}")
    @Headers("Content-Type: application/json")
    String update(@Param("email_id") String emailId, String emailAsJson);

    @RequestLine("POST /marketing-emails/v1/emails/{email_id}/create-variation")
    @Headers("Content-Type: application/json")
    String createVariation(@Param("email_id") String emailId, CreateVariationRequest createVariationRequest);

    @RequestLine("GET /marketing-emails/v1/emails/{email_id}/buffer")
    String getBufferedContent(@Param("email_id") String emailId);
}
