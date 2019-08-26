package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.email.CloneEmailRequest;
import com.smartling.connector.hubspot.sdk.email.EmailDetail;
import com.smartling.connector.hubspot.sdk.email.EmailDetails;
import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface EmailsEntityApi
{
    @RequestLine("GET /marketing-emails/v1/emails?offset={offset}&limit={limit}&order_by={order_by}")
    EmailDetails emails(@Param("offset") int offset, @Param("limit") int limit,
                        @Param("order_by") String orderBy, @QueryMap Map<String, Object> queryMap);

    @RequestLine("GET /marketing-emails/v1/emails/{email_id}")
    EmailDetail emailDetail(@Param("email_id") String emailId);

    @RequestLine("POST /marketing-emails/v1/emails/{email_id}/clone")
    @Headers("Content-Type: application/json")
    EmailDetail clone(@Param("email_id") String email_id, CloneEmailRequest cloneFormRequest);
}
