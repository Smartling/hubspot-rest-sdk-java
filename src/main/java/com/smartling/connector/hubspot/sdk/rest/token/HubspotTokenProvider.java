package com.smartling.connector.hubspot.sdk.rest.token;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.api.AuthorizationApi;

import feign.Feign;
import feign.FeignException;
import feign.Request.Options;
import feign.gson.GsonDecoder;

public class HubspotTokenProvider implements TokenProvider
{
    private final AuthorizationApi authorizationApi;
    private final String           refreshToken;
    private final String           clientId;

    public HubspotTokenProvider(final Configuration configuration)
    {
        this.clientId = configuration.getClientId();
        this.refreshToken = configuration.getRefreshToken();

        Options connectionConfig = new Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        authorizationApi = Feign.builder()
                                .options(connectionConfig)
                                .decoder(new GsonDecoder())
                                .target(AuthorizationApi.class, configuration.getApiUrl());
    }

    public RefreshTokenData getTokenData() throws HubspotApiException
    {
        try
        {
            return this.authorizationApi.newToken(this.clientId, this.refreshToken);
        }
        catch (FeignException e)
        {
            throw new HubspotApiException("Auth call to Hubspot API failed!", e);
        }
    }
}
