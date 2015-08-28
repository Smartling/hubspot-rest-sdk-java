package com.smartling.connector.hubspot.sdk.rest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    private static final String GUID_PROPERTY_NAME = "guid";
    private static final String NAME_PROPERTY_NAME = "name";
    private static final String CLONED_NAME_TEMPLATE = "%s - Cloned - %d";

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
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        String name = obj.get(NAME_PROPERTY_NAME).getAsString();
        obj.addProperty(NAME_PROPERTY_NAME, String.format(CLONED_NAME_TEMPLATE, name, System.currentTimeMillis()));
        String newBody = execute(token -> formsRawApi.create(obj.toString(), token));
        JsonObject newObj = parser.parse(newBody).getAsJsonObject();
        return execute(token -> formsEntityApi.formDetail(newObj.get(GUID_PROPERTY_NAME).getAsString(), token));
    }


    @Override
    public String updateFormContent(String guid, String content) throws HubspotApiException
    {
        return execute(token -> formsRawApi.update(guid, content, token));
    }


    @Override
    public List<FormDetail> listFormsByTmsId(String tmsId) throws HubspotApiException
    {
        return execute(token -> formsEntityApi.forms(token)).stream().filter(e -> Objects.equals(tmsId, e.getTmsId())).collect(Collectors.toList());
    }

    @Override
    public ResultInfo delete(String guid) throws HubspotApiException
    {
        execute(token -> formsEntityApi.delete(guid, token));
        ResultInfo result = new ResultInfo();
        result.setSucceeded(true);
        return result;
    }
}
