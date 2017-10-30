package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface AuthorizationApi
{
    @RequestLine("POST /oauth/v1/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("grant_type=refresh_token&client_id={client_id}&client_secret={client_secret}&redirect_uri={redirect_uri}&refresh_token={refresh_token}")

    RefreshTokenData newToken(@Param("client_id") final String clientId, @Param("client_secret") final String clientSecret, @Param("redirect_uri") final String redirectUri, @Param("refresh_token") final String refreshToken);
}
