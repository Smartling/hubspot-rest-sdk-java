package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClient.Configuration;
import com.smartling.connector.hubspot.sdk.rest.api.AuthorizationApi;

import feign.Feign;
import feign.Request.Options;
import feign.gson.GsonDecoder;

public class TokenProvider
{
    private final AuthorizationApi authorizationApi;
    private final String           refreshToken;
    private final String           clientId;

    public TokenProvider(final Configuration configuration)
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

    public RefreshTokenData getTokenData()
    {
        return this.authorizationApi.newToken(this.clientId, this.refreshToken);
    }

    public void expireTokenData()
    {
    }

    protected String getClientId()
    {
        return this.clientId;
    }

    protected final static class ConfigurationException extends Exception
    {
        public ConfigurationException(TokenProvider provider, Configuration configuration)
        {
            super("TokenProvider " + provider.getClass().getName() + " isn't support " + configuration.getClass().getName() + " configuration!");
        }
    }
}
