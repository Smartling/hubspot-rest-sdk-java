package com.smartling.connector.hubspot.sdk.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotMarketingEmailsClient;
import com.smartling.connector.hubspot.sdk.marketingEmail.MarketingEmailDetail;
import com.smartling.connector.hubspot.sdk.marketingEmail.MarketingEmailDetails;
import com.smartling.connector.hubspot.sdk.marketingEmail.MarketingEmailFilter;
import com.smartling.connector.hubspot.sdk.rest.api.MarketingEmailApi;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.Feign;
import feign.Request;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.NonNull;

public class HubspotRestMarketingEmailsClient extends AbstractHubspotRestClient implements HubspotMarketingEmailsClient
{
    private final MarketingEmailApi marketingEmailApi;

    public HubspotRestMarketingEmailsClient(final Configuration configuration, final TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Request.Options connectionConfig = new Request.Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        marketingEmailApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .encoder(new GsonEncoder(configuredGson()))
                .decoder(new GsonDecoder(configuredGson()))
                .target(MarketingEmailApi.class, configuration.getApiUrl());
    }

    @Override
    protected Gson configuredGson()
    {
        return new GsonBuilder().create();
    }


    @Override
    public MarketingEmailDetails listEmails(int offset, int limit, @NonNull MarketingEmailFilter filter, String orderBy) throws HubspotApiException
    {
        return execute(() -> marketingEmailApi.emails(filter.getArchived(), filter.getCampaign(),
                filter.getEmailName(), offset, limit, orderBy));
    }

    @Override
    public MarketingEmailDetail getEmailById(String id) throws HubspotApiException
    {
        return execute(() -> marketingEmailApi.emailDetail(id));
    }

    @Override
    public MarketingEmailDetail createEmail(MarketingEmailDetail emailDetail) throws HubspotApiException
    {
        return execute(() -> marketingEmailApi.createEmail(emailDetail));
    }

    @Override
    public MarketingEmailDetail updateEmail(MarketingEmailDetail emailDetail) throws HubspotApiException
    {
        return execute(() -> marketingEmailApi.updateEmail(emailDetail.getId(), emailDetail));
    }
}
