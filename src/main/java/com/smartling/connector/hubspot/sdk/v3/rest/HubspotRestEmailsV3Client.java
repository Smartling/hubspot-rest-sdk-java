package com.smartling.connector.hubspot.sdk.v3.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.logger.FeignLogger;
import com.smartling.connector.hubspot.sdk.rest.AbstractHubspotRestClient;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import com.smartling.connector.hubspot.sdk.v3.HubspotEmailsV3Client;
import com.smartling.connector.hubspot.sdk.v3.email.CloneEmailRequest;
import com.smartling.connector.hubspot.sdk.v3.email.EmailDetail;
import com.smartling.connector.hubspot.sdk.v3.email.ListWrapper;
import com.smartling.connector.hubspot.sdk.v3.rest.api.EmailsEntityApi;
import com.smartling.connector.hubspot.sdk.v3.rest.api.EmailsRawApi;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.httpclient.ApacheHttpClient;
import lombok.NonNull;

import java.util.Collections;
import java.util.Map;

public class HubspotRestEmailsV3Client extends AbstractHubspotRestClient implements HubspotEmailsV3Client
{
    private final EmailsEntityApi emailsEntityApi;
    private final EmailsRawApi emailsRawApi;

    public HubspotRestEmailsV3Client(final Configuration configuration, final TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Request.Options connectionConfig = new Request.Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        emailsRawApi = Feign.builder()
                .client(new ApacheHttpClient())
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .logger(new FeignLogger(EmailsRawApi.class))
                .logLevel(Logger.Level.FULL)
                .target(EmailsRawApi.class, configuration.getApiUrl());

        emailsEntityApi = Feign.builder()
                .client(new ApacheHttpClient())
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .encoder(new GsonEncoder(camelCaseGsonWithISODate()))
                .decoder(new GsonDecoder(camelCaseGsonWithISODate()))
                .logger(new FeignLogger(EmailsEntityApi.class))
                .logLevel(Logger.Level.FULL)
                .target(EmailsEntityApi.class, configuration.getApiUrl());
    }

    @Override
    public ListWrapper<EmailDetail> listEmails(int limit, String after, String orderBy, Map<String, Object> queryMap) throws HubspotApiException
    {
        Map<String, Object> safeQueryMap = queryMap != null ? queryMap : Collections.emptyMap();
        return execute(() -> emailsEntityApi.list(limit, after, orderBy, safeQueryMap));
    }

    @Override
    public EmailDetail getDetail(@NonNull String emailId) throws HubspotApiException
    {
        return execute(() -> emailsEntityApi.getDetail(emailId));
    }

    @Override
    public EmailDetail getDraftDetail(@NonNull String emailId) throws HubspotApiException
    {
        return execute(() -> emailsEntityApi.getDraftDetail(emailId));
    }

    @Override
    public EmailDetail getAbTestVariation(@NonNull String emailId) throws HubspotApiException
    {
        return execute(() -> emailsEntityApi.getAbTestVariation(emailId));
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
    public EmailDetail clone(@NonNull CloneEmailRequest cloneEmailRequest) throws HubspotApiException
    {
        return execute(() -> emailsEntityApi.clone(cloneEmailRequest));
    }

}
