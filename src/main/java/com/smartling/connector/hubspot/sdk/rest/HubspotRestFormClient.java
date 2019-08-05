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
import com.smartling.connector.hubspot.sdk.form.FormFilter;
import com.smartling.connector.hubspot.sdk.rest.api.FormsEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.FormsRawApi;

import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.Feign;
import feign.Request.Options;
import feign.gson.GsonDecoder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

public class HubspotRestFormClient extends AbstractHubspotRestClient implements HubspotFormClient
{
    private static final String GUID_PROPERTY_NAME = "guid";
    private static final String NAME_PROPERTY_NAME = "name";
    private static final String DELETABLE_PROPERTY_NAME = "deletable";
    private static final String CLONED_NAME_TEMPLATE = "%s - Cloned - %d";

    private final FormsRawApi formsRawApi;
    private final FormsEntityApi formsEntityApi;

    public HubspotRestFormClient(final Configuration configuration, final TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Options connectionConfig = new Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        formsRawApi = Feign.builder()
                .requestInterceptor(getAuthenticationInterceptor())
                .options(connectionConfig)
                .target(FormsRawApi.class, configuration.getApiUrl());

        formsEntityApi = Feign.builder()
                              .requestInterceptor(getAuthenticationInterceptor())
                              .options(connectionConfig)
                              .decoder(new GsonDecoder(configuredGson()))
                              .target(FormsEntityApi.class, configuration.getApiUrl());
    }


    @Override
    public List<FormDetail> listForms(int offset, int limit, @NonNull FormFilter filter, String orderBy) throws HubspotApiException
    {
        return execute(() -> formsEntityApi.forms(
                filter.getFormType() == null ? "ALL" : filter.getFormType(),
                filter.getName(),
                offset,
                limit,
                orderBy
        ));
    }


    @Override
    public String getFormContentById(String guid) throws HubspotApiException
    {
        return execute(() -> formsRawApi.form(guid));
    }


    @Override
    public FormDetail getFormDetailById(String guid) throws HubspotApiException
    {
        return execute(() -> formsEntityApi.formDetail(guid));
    }


    @Override
    public FormDetail cloneFormAsDetail(String guid) throws HubspotApiException
    {
        String body = execute(() -> formsRawApi.form(guid));
        JsonObject srcForm = parseForm(body);

        setupCloneFields(srcForm);

        String newBody = execute(() -> formsRawApi.create(srcForm.toString()));
        JsonObject newObj = parseForm(newBody);
        return execute(() -> formsEntityApi.formDetail(newObj.get(GUID_PROPERTY_NAME).getAsString()));
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

        return execute(() -> formsRawApi.update(guid, form.toString()));
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
        return execute(() -> formsEntityApi.forms("ALL", StringUtils.EMPTY, 0, 0, StringUtils.EMPTY))
            .stream().filter(e -> Objects.equals(tmsId, e.getTmsId())).collect(Collectors.toList());
    }

    @Override
    public ResultInfo delete(String guid) throws HubspotApiException
    {
        execute(() -> formsEntityApi.delete(guid));
        ResultInfo result = new ResultInfo();
        result.setSucceeded(true);
        return result;
    }
}
