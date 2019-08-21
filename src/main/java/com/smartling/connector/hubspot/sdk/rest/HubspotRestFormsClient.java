package com.smartling.connector.hubspot.sdk.rest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotFormsClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.form.CloneFormRequest;
import com.smartling.connector.hubspot.sdk.form.FormDetail;
import com.smartling.connector.hubspot.sdk.form.FormFilter;
import com.smartling.connector.hubspot.sdk.rest.api.FormsEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.FormsRawApi;

import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.Feign;
import feign.Request.Options;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

public class HubspotRestFormsClient extends AbstractHubspotRestClient implements HubspotFormsClient
{
    public static final String DEFAULT_FORM_TYPE_FILTER = "HUBSPOT";
    public static final int DEFAULT_LIMIT_FILTER = 50;
    public static final String DEFAULT_ORDER_BY = "-updatedAt";
    public static final String NAME_SEARCH_QUERY_PARAMETER_NAME = "name__icontains";

    private final FormsRawApi formsRawApi;
    private final FormsEntityApi formsEntityApi;

    public HubspotRestFormsClient(final Configuration configuration, final TokenProvider tokenProvider)
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
                              .encoder(new GsonEncoder(configuredGson()))
                              .target(FormsEntityApi.class, configuration.getApiUrl());
    }


    @Override
    public List<FormDetail> listForms(int offset, int limit, @NonNull FormFilter filter, String orderBy) throws HubspotApiException
    {
        Map<String, Object> queryMap = StringUtils.isBlank(filter.getName()) ?
                Collections.emptyMap() :
                Collections.singletonMap(NAME_SEARCH_QUERY_PARAMETER_NAME, filter.getName());

        return execute(() -> formsEntityApi.forms(
                filter.getFormType() == null ? DEFAULT_FORM_TYPE_FILTER : filter.getFormType(),
                offset,
                limit == 0 ? DEFAULT_LIMIT_FILTER : limit,
                orderBy == null ? DEFAULT_ORDER_BY : orderBy,
                queryMap
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
    public FormDetail cloneForm(String guid, String name) throws HubspotApiException
    {
        return execute(() -> formsEntityApi.clone(guid, new CloneFormRequest(name)));
    }

    @Override
    public String updateFormContent(String guid, String content) throws HubspotApiException
    {
        return execute(() -> formsRawApi.update(guid, content));
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
