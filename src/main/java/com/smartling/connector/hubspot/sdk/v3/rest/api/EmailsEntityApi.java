package com.smartling.connector.hubspot.sdk.v3.rest.api;

import com.smartling.connector.hubspot.sdk.v3.email.CloneEmailRequest;
import com.smartling.connector.hubspot.sdk.v3.email.EmailDetail;
import com.smartling.connector.hubspot.sdk.v3.email.ListWrapper;
import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface EmailsEntityApi
{
    @RequestLine("GET /marketing/v3/emails?limit={limit}&after={after}&sort={sortBy}&" + EmailDetail.INCLUDED_PROPERTIES)
    ListWrapper<EmailDetail> list(@Param("limit") int limit, @Param("after") String after,
                                  @Param("sortBy") String sortBy, @QueryMap Map<String, Object> queryMap);

    @RequestLine("GET /marketing/v3/emails/{emailId}?" + EmailDetail.INCLUDED_PROPERTIES)
    EmailDetail getDetail(@Param("emailId") String emailId);

    @RequestLine("GET /marketing/v3/emails/{emailId}/draft")
    EmailDetail getDraftDetail(@Param("emailId") String emailId);

    @RequestLine("POST /marketing/v3/emails/clone")
    @Headers("Content-Type: application/json")
    EmailDetail clone(CloneEmailRequest cloneEmailRequest);

    @RequestLine("GET /marketing/v3/emails/{emailId}/ab-test/get-variation")
    EmailDetail getAbTestVariation(@Param("emailId") String emailId);
}
