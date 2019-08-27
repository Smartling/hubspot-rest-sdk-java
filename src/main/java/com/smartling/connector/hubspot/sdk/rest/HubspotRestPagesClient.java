package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotPagesClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.page.Language;
import com.smartling.connector.hubspot.sdk.page.PageDetail;
import com.smartling.connector.hubspot.sdk.rest.api.PagesEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.PagesRawApi;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.Feign;
import feign.Request.Options;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.httpclient.ApacheHttpClient;

import java.util.Collections;
import java.util.Map;

public class HubspotRestPagesClient extends AbstractHubspotRestClient implements HubspotPagesClient
{
    private static final String EMPTY_JSON = "{}";

    private final PagesRawApi pagesRawApi;
    private final PagesEntityApi pagesEntityApi;
    private final PagesEntityApi pagesEntityApiApache;

    public HubspotRestPagesClient(final Configuration configuration, final TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Options connectionConfig = new Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        pagesRawApi = Feign.builder()
                           .requestInterceptor(getAuthenticationInterceptor())
                           .options(connectionConfig)
                           .target(PagesRawApi.class, configuration.getApiUrl());

        pagesEntityApi = Feign.builder()
                              .requestInterceptor(getAuthenticationInterceptor())
                              .options(connectionConfig)
                              .decoder(new GsonDecoder(configuredGson()))
                              .encoder(new GsonEncoder(configuredGson()))
                              .target(PagesEntityApi.class, configuration.getApiUrl());

        // ApacheHttpClient has advanced options for request/response processing
        pagesEntityApiApache = Feign.builder()
                                    .requestInterceptor(getAuthenticationInterceptor())
                                    .options(connectionConfig)
                                    .client(new ApacheHttpClient())
                                    .decoder(new GsonDecoder(configuredGson()))
                                    .target(PagesEntityApi.class, configuration.getApiUrl());
    }

    @Override
    public String getPageById(long pageId) throws HubspotApiException
    {
        return execute(() -> pagesRawApi.page(pageId));
    }

    @Override
    public PageDetail getPageDetailById(long pageId) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.pageDetail(pageId));
    }

    @Override
    public String clonePage(long originalPageId) throws HubspotApiException
    {
        return execute(() -> pagesRawApi.clone(originalPageId, EMPTY_JSON));
    }

    @Override
    public PageDetail createLanguageVariation(long pageId, CreateLanguageVariationRequest createLanguageVariationRequest) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.createLanguageVariation(pageId, createLanguageVariationRequest));
    }

    @Override
    public PageDetail clonePageAsDetail(long originalPageId) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.clone(originalPageId, EMPTY_JSON));
    }

    @Override
    public String updatePage(String page, long updatePageId) throws HubspotApiException
    {
        return execute(() -> pagesRawApi.update(updatePageId, page));
    }

    @Override
    public ListWrapper<PageDetail> listPages(int offset, int limit, String orderBy, Map<String, Object> queryMap) throws HubspotApiException
    {
        Map<String, Object> safeQueryMap = queryMap != null ? queryMap : Collections.emptyMap();

        return execute(() -> pagesEntityApiApache.listPages(limit, offset, orderBy, safeQueryMap));
    }

    @Override
    public ResultInfo delete(long pageId) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.delete(pageId));
    }

    @Override
    public ListWrapper<Language> getSupportedLanguages() throws HubspotApiException
    {
        return execute(pagesEntityApi::getSupportedLanguages);
    }
}
