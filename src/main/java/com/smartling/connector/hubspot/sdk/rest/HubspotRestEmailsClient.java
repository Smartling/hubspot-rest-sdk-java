package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotEmailsClient;
import com.smartling.connector.hubspot.sdk.email.CloneEmailRequest;
import com.smartling.connector.hubspot.sdk.email.EmailDetail;
import com.smartling.connector.hubspot.sdk.email.EmailDetails;
import com.smartling.connector.hubspot.sdk.rest.api.EmailsEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.EmailsRawApi;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.Feign;
import feign.Request;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.NonNull;

import java.util.Collections;
import java.util.Map;

public class HubspotRestEmailsClient extends AbstractHubspotRestClient implements HubspotEmailsClient
{
    private final EmailsEntityApi emailsEntityApi;
    private final EmailsRawApi emailsRawApi;

    public HubspotRestEmailsClient(final Configuration configuration, final TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Request.Options connectionConfig = new Request.Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        emailsEntityApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .encoder(new GsonEncoder(configuredGson()))
                .decoder(new GsonDecoder(configuredGson()))
                .target(EmailsEntityApi.class, configuration.getApiUrl());

        emailsRawApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .encoder(new GsonEncoder(configuredGson()))
                .decoder(new GsonDecoder(configuredGson()))
                .target(EmailsRawApi.class, configuration.getApiUrl());
    }

    @Override
    public EmailDetails listEmails(int offset, int limit, String orderBy, Map<String, Object> queryMap) throws HubspotApiException
    {
        Map<String, Object> safeQueryMap = queryMap != null ? queryMap : Collections.emptyMap();
        return execute(() -> emailsEntityApi.emails(offset, limit, orderBy, safeQueryMap));
    }

    @Override
    public EmailDetail getDetail(@NonNull String emailId) throws HubspotApiException
    {
        return execute(() -> emailsEntityApi.emailDetail(emailId));
    }

    @Override
    public String getContent(@NonNull String emailId) throws HubspotApiException
    {
        return execute(() -> emailsRawApi.email(emailId));
    }

    @Override
    public String updateContent(@NonNull String emailId, @NonNull String emailContent) throws HubspotApiException
    {
        return execute(() -> emailsRawApi.update(emailId, emailContent));
    }

    @Override
    public EmailDetail clone(@NonNull String emailId, @NonNull String name) throws HubspotApiException
    {
        return execute(() -> emailsEntityApi.clone(emailId, new CloneEmailRequest(name)));
    }
}