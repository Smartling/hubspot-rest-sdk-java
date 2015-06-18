package com.smartling.connector.hubspot.sdk.rest.api;

import feign.Body;
import feign.Param;
import feign.RequestLine;

public interface AuthorizationApi
{
    @RequestLine("POST /auth/v1/refresh")
    @Body("client_id={client_id}&grant_type=refresh_token&refresh_token={refresh_token}")
    RefreshData newToken(@Param("client_id") final String clientId, @Param("refresh_token") final String refreshToken);
}
