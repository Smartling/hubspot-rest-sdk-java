package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.email.CloneEmailRequest;
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

    @RequestLine("POST /marketing-emails/v1/emails/")
    @Headers("Content-Type: application/json")
    String create(String emailAsJson);

    @RequestLine("POST /marketing-emails/v1/emails/{email_id}/clone")
    @Headers("Content-Type: application/json")
    String clone(@Param("email_id") String email_id, CloneEmailRequest cloneFormRequest);
}
