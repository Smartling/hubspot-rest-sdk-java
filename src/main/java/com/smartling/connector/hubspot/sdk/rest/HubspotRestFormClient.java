package com.smartling.connector.hubspot.sdk.rest;

import java.util.List;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotFormClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.form.FormDetail;
import com.smartling.connector.hubspot.sdk.rest.api.FormsEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.FormsRawApi;

import feign.Feign;
import feign.Request.Options;
import feign.gson.GsonDecoder;

public class HubspotRestFormClient extends AbstractHubspotRestClient implements HubspotFormClient
{
    private final FormsRawApi formsRawApi;
    private final FormsEntityApi formsEntityApi;

    public HubspotRestFormClient(final Configuration configuration, final RestExecutor executor)
    {
        super(executor);

        Options connectionConfig = new Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        formsRawApi = Feign.builder()
                .options(connectionConfig)
                .target(FormsRawApi.class, configuration.getApiUrl());

        formsEntityApi = Feign.builder()
                              .options(connectionConfig)
                              .decoder(new GsonDecoder(configuredGson()))
                              .target(FormsEntityApi.class, configuration.getApiUrl());
    }


    @Override
    public List<FormDetail> listForms() throws HubspotApiException
    {
        return execute(token -> formsEntityApi.forms(token));
    }


    @Override
    public String getFormContentById(String guid) throws HubspotApiException
    {
        return execute(token -> formsRawApi.form(guid, token));
    }


    @Override
    public FormDetail getFormDetailById(String guid) throws HubspotApiException
    {
        return execute(token -> formsEntityApi.formDetail(guid, token));
    }


    @Override
    public FormDetail cloneFormAsDetail(String guid) throws HubspotApiException
    {
        String body = execute(token -> formsRawApi.form(guid, token));
        String newGuid = execute(token -> formsRawApi.create(body, token));
        return execute(token -> formsEntityApi.formDetail(newGuid, token));
    }


    @Override
    public String updateFormContent(String guid, String content) throws HubspotApiException
    {
        return execute(token -> formsRawApi.update(guid, content, token));
    }


    @Override
    public List<FormDetail> listFormsByTmsId(String tmsId) throws HubspotApiException
    {
        return execute(token -> formsEntityApi.findByTmsId(tmsId, token));
    }


    @Override
    public ResultInfo delete(String guid) throws HubspotApiException
    {
        return execute(token -> formsEntityApi.delete(guid, token));
    }
}
