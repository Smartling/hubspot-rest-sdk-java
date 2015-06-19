package com.smartling.connector.hubspot.sdk.rest.api;

import feign.Param;
import feign.RequestLine;

public interface PagesEntityApi
{
    @RequestLine("GET /content/api/v2/pages?access_token={access_token}&limit={limit}&offset={offset}")
    PageDetails pages(@Param("limit") int limit, @Param("offset") int offset, @Param("access_token") String accessToken);
}
