package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.marketingEmail.MarketingEmailDetail;
import com.smartling.connector.hubspot.sdk.marketingEmail.MarketingEmailDetails;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface MarketingEmailApi {
    @RequestLine("GET /marketing-emails/v1/emails?archived={archived}&campaign={campaign}&name__icontains={name}&offset={offset}&limit={limit}&order_by={order_by}")
    MarketingEmailDetails emails(@Param("archived") Boolean archived, @Param("campaign") String campaign,
                                    @Param("name") String name,
                                    @Param("offset") int offset, @Param("limit") int limit,
                                    @Param("order_by") String orderBy);

    @RequestLine("GET /marketing-emails/v1/emails/{email_id}")
    MarketingEmailDetail emailDetail(@Param("email_id") String emailId);

    @RequestLine("POST /marketing-emails/v1/emails")
    @Headers("Content-Type: application/json")
    MarketingEmailDetail createEmail(MarketingEmailDetail emailDetail);

    @RequestLine("PUT /marketing-emails/v1/emails/{email_id}")
    @Headers("Content-Type: application/json")
    MarketingEmailDetail updateEmail(@Param("email_id") String emailId, MarketingEmailDetail emailDetail);
}
