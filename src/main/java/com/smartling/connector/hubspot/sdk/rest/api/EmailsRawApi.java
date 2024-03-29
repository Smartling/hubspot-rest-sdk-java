package com.smartling.connector.hubspot.sdk.rest.api;

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

    // This is a workaround specifically made for updating A/B Emails. The standard endpoint throws an error
    @RequestLine("PUT /cosemail/v1/emails/{email_id}")
    @Headers("Content-Type: application/json")
    String updateAb(@Param("email_id") String emailId, String emailAsJson);
}
