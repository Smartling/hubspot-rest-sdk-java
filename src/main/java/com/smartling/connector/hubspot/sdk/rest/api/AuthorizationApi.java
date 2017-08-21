package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface AuthorizationApi
{
    @RequestLine("POST /auth/v1/refresh")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("client_id={client_id}&grant_type=refresh_token&refresh_token={refresh_token}")
    RefreshTokenData newToken(@Param("client_id") final String clientId, @Param("refresh_token") final String refreshToken);
}
