package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.api.AuthorizationApi;
import feign.Feign;
import feign.Request;
import feign.gson.GsonDecoder;

public class OAuthClient
{
    private final Configuration configuration;
    private final AuthorizationApi authorizationApi;

    public OAuthClient(final Configuration configuration)
    {
        this.configuration = configuration;
        Request.Options connectionConfig = new Request.Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        authorizationApi = Feign.builder()
                .options(connectionConfig)
                .decoder(new GsonDecoder())
                .target(AuthorizationApi.class, configuration.getApiUrl());

    }

    public RefreshTokenData getTokenFromCode(String code)
    {
        return authorizationApi.getTokenFromCode(configuration.getClientId(), configuration.getClientSecret(), configuration.getRedirectUri(), code);
    }
}
