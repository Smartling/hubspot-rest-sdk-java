package com.smartling.connector.hubspot.sdk.v3.rest.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface EmailsRawApi
{
    @RequestLine("GET /marketing/v3/emails/{emailId}")
    String getContent(@Param("emailId") String emailId);

    @RequestLine("PATCH /marketing/v3/emails/{emailId}")
    @Headers("Content-Type: application/json")
    String update(@Param("emailId") String emailId, String emailAsJson);
}
