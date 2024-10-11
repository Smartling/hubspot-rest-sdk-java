package com.smartling.connector.hubspot.sdk.v3.rest.api;

import com.smartling.connector.hubspot.sdk.domain.DomainDetails;
import feign.Param;
import feign.RequestLine;

public interface DomainsApi
{
    @RequestLine("GET /cms/v3/domains?limit={limit}&after={after}&sort={sort}")
    DomainDetails listDomains(@Param("limit") int limit, @Param("after") String after, @Param("sort") String sort);
}
