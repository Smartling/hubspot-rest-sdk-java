package com.smartling.connector.hubspot.sdk.rest.token;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.logger.FeignLogger;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.api.AuthorizationApi;
import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.Request.Options;
import feign.gson.GsonDecoder;

public class HubspotTokenProvider implements TokenProvider
{
    private final AuthorizationApi authorizationApi;
    private final String           refreshToken;
    private final String           clientId;
    private final String           clientSecret;
    private final String           redirectUri;

    public HubspotTokenProvider(final Configuration configuration)
    {
        this.clientId = configuration.getClientId();
        this.clientSecret = configuration.getClientSecret();
        this.redirectUri = configuration.getRedirectUri();
        this.refreshToken = configuration.getRefreshToken();

        Options connectionConfig = new Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        authorizationApi = Feign.builder()
                .options(connectionConfig)
                .decoder(new GsonDecoder())
                .logger(new FeignLogger(AuthorizationApi.class))
                .logLevel(Logger.Level.FULL)
                .target(AuthorizationApi.class, configuration.getApiUrl());
    }

    @Override
    public RefreshTokenData getTokenData() throws HubspotApiException
    {
        try
        {
            return this.authorizationApi.newToken(this.clientId, this.clientSecret, this.redirectUri, this.refreshToken);
        }
        catch (FeignException e)
        {
            throw new HubspotApiException("Auth call to Hubspot API failed!", e);
        }
    }

    public RefreshTokenData getTokenUsingGrantCode(String redirectUri, String grantCode) throws HubspotApiException
    {
        try
        {
            return authorizationApi.getTokenUsingGrantCode(this.clientId, this.clientSecret, redirectUri, grantCode);
        }
        catch (FeignException e)
        {
            throw new HubspotApiException("Requesting refresh token by grant code failed!", e);
        }
    }
}
