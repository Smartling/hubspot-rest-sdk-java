package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotEmailsClient;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.email.CloneEmailRequest;
import com.smartling.connector.hubspot.sdk.email.CreateVariationRequest;
import com.smartling.connector.hubspot.sdk.email.EmailDetail;
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

        emailsRawApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .target(EmailsRawApi.class, configuration.getApiUrl());

        emailsEntityApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .encoder(new GsonEncoder(camelCaseGson()))
                .decoder(new GsonDecoder(camelCaseGson()))
                .target(EmailsEntityApi.class, configuration.getApiUrl());
    }

    @Override
    public ListWrapper<EmailDetail> listEmails(int offset, int limit, String orderBy, Map<String, Object> queryMap) throws HubspotApiException
    {
        Map<String, Object> safeQueryMap = queryMap != null ? queryMap : Collections.emptyMap();
        return execute(() -> emailsEntityApi.list(offset, limit, orderBy, safeQueryMap));
    }

    @Override
    public EmailDetail getDetail(@NonNull String emailId) throws HubspotApiException
    {
        return execute(() -> emailsEntityApi.getDetail(emailId));
    }

    @Override
    public String getContent(@NonNull String emailId) throws HubspotApiException
    {
        return execute(() -> emailsRawApi.getContent(emailId));
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

    @Override
    public EmailDetail createVariation(@NonNull String emailId, @NonNull String variationName) throws HubspotApiException
    {
        return execute(() -> emailsEntityApi.createVariation(emailId, new CreateVariationRequest(variationName)));
    }

    @Override
    public String getBufferedContent(@NonNull String emailId) throws HubspotApiException
    {
        return execute(() -> emailsRawApi.getBufferedContent(emailId));
    }
}
