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
    private static final String DELETABLE_PROPERTY_NAME = "deletable";
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
        JsonObject srcForm = parseForm(body);

        setupCloneFields(srcForm);

        String newBody = execute(token -> formsRawApi.create(srcForm.toString(), token));
        JsonObject newObj = parseForm(newBody);
        return execute(token -> formsEntityApi.formDetail(newObj.get(GUID_PROPERTY_NAME).getAsString(), token));
    }

    private static void setupCloneFields(JsonObject form) {
        String name = form.get(NAME_PROPERTY_NAME).getAsString();
        form.addProperty(NAME_PROPERTY_NAME, String.format(CLONED_NAME_TEMPLATE, name, System.currentTimeMillis()));
        resetDeletableField(form);
    }

    @Override
    public String updateFormContent(String guid, String content) throws HubspotApiException
    {
        JsonObject form = parseForm(content);

        resetDeletableField(form);

        return execute(token -> formsRawApi.update(guid, form.toString(), token));
    }

    private static void resetDeletableField(JsonObject form) {
        // we can use REST API to create forms with deletable = true only
        form.addProperty(DELETABLE_PROPERTY_NAME, true);
    }

    private static JsonObject parseForm(String content) {
        JsonParser parser = new JsonParser();
        return parser.parse(content).getAsJsonObject();
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
