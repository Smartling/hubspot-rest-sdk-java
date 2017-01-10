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
    @Body("grant_type=authorization_code&client_id={client_id}&client_secret={client_secret}&redirect_uri={redirect_uri}&code={code}")
    RefreshTokenData getTokenFromCode(@Param ("client_id") final String clientId, @Param ("client_secret") final String clientSecret,
            @Param ("redirect_uri") final String redirectUri, @Param ("code") final String code);

    @RequestLine("POST /oauth/v1/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("grant_type=refresh_token&client_id={clientId}&client_secret={clientSecret}&redirect_uri={redirectUri}&refresh_token={refreshToken}")
    RefreshTokenData newToken(@Param("clientId") final String clientId, @Param("clientSecret") final String clientSecret, @Param("redirectUri") final String redirectUri,
            @Param("refreshToken") final String refreshToken);
}
