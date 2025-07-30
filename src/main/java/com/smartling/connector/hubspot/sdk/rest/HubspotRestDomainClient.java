package com.smartling.connector.hubspot.sdk.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotDomainClient;
import com.smartling.connector.hubspot.sdk.domain.DomainDetails;
import com.smartling.connector.hubspot.sdk.logger.FeignLogger;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import com.smartling.connector.hubspot.sdk.rest.util.InstantTypeAdapter;
import com.smartling.connector.hubspot.sdk.v3.rest.api.DomainsApi;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.gson.GsonDecoder;

import java.time.Instant;

public class HubspotRestDomainClient extends AbstractHubspotRestClient implements HubspotDomainClient
{
    private final DomainsApi domainsApi;

    public HubspotRestDomainClient(final Configuration configuration, final TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Request.Options connectionConfig = new Request.Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .create();

        domainsApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .decoder(new GsonDecoder(gson))
                .logger(new FeignLogger(DomainsApi.class))
                .logLevel(Logger.Level.FULL)
                .target(DomainsApi.class,   configuration.getApiUrl());
    }

    @Override
    public DomainDetails getDomainDetails(int limit, String after, String sort) throws HubspotApiException {
        return execute(() -> domainsApi.listDomains(limit, after, sort));
    }
}
