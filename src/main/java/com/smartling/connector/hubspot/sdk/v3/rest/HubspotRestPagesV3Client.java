package com.smartling.connector.hubspot.sdk.v3.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.rest.AbstractHubspotRestClient;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import com.smartling.connector.hubspot.sdk.v3.HubspotPagesV3Client;
import com.smartling.connector.hubspot.sdk.v3.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.v3.page.PageDetail;
import com.smartling.connector.hubspot.sdk.v3.page.PageType;
import com.smartling.connector.hubspot.sdk.v3.page.SchedulePublishRequest;
import com.smartling.connector.hubspot.sdk.v3.rest.api.PagesEntityApi;
import com.smartling.connector.hubspot.sdk.v3.rest.api.PagesRawApi;
import feign.Feign;
import feign.Request.Options;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.httpclient.ApacheHttpClient;

import java.util.Collections;
import java.util.Map;

public class HubspotRestPagesV3Client extends AbstractHubspotRestClient implements HubspotPagesV3Client
{
    private final PageType pageType;

    private final PagesRawApi pagesRawApi;
    private final PagesEntityApi pagesEntityApi;

    public HubspotRestPagesV3Client(PageType pageType, Configuration configuration, TokenProvider tokenProvider)
    {
        super(tokenProvider);

        Options connectionConfig = new Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        this.pageType = pageType;

        pagesRawApi = Feign.builder()
                           .requestInterceptor(getAuthenticationInterceptor())
                           .options(connectionConfig)
                           .client(new ApacheHttpClient())
                           .target(PagesRawApi.class, configuration.getApiUrl());

        pagesEntityApi = Feign.builder()
                              .requestInterceptor(getAuthenticationInterceptor())
                              .options(connectionConfig)
                              .client(new ApacheHttpClient())
                              .decoder(new GsonDecoder(camelCaseGson()))
                              .encoder(new GsonEncoder(camelCaseGson()))
                              .target(PagesEntityApi.class, configuration.getApiUrl());
    }

    @Override
    public String getPageById(long pageId) throws HubspotApiException
    {
        return execute(() -> pagesRawApi.page(pageType.getPathParam(), pageId));
    }

    @Override
    public String getPageDraftById(long pageId) throws HubspotApiException
    {
        return execute(() -> pagesRawApi.pageDraft(pageType.getPathParam(), pageId));
    }

    @Override
    public PageDetail getPageDetailById(long pageId) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.pageDetail(pageType.getPathParam(), pageId));
    }

    @Override
    public PageDetail getPageDetailDraftById(long pageId) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.pageDetailDraft(pageType.getPathParam(), pageId));
    }

    @Override
    public PageDetail createLanguageVariation(CreateLanguageVariationRequest createLanguageVariationRequest) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.createLanguageVariation(pageType.getPathParam(), createLanguageVariationRequest));
    }

    @Override
    public String updatePage(String page, long updatePageId) throws HubspotApiException
    {
        return execute(() -> pagesRawApi.update(pageType.getPathParam(), updatePageId, page));
    }

    @Override
    public String updatePageDraft(String page, long updatePageId) throws HubspotApiException
    {
        return execute(() -> pagesRawApi.updateDraft(pageType.getPathParam(), updatePageId, page));
    }

    @Override
    public ListWrapper<PageDetail> listPages(int offset, int limit, String orderBy, Map<String, Object> queryMap) throws HubspotApiException
    {
        Map<String, Object> safeQueryMap = queryMap != null ? queryMap : Collections.emptyMap();

        return execute(() -> pagesEntityApi.listPages(pageType.getPathParam(), limit, offset, orderBy, safeQueryMap));
    }

    @Override
    public void publish(SchedulePublishRequest publishActionRequest) throws HubspotApiException
    {
        execute(() -> pagesEntityApi.publish(pageType.getPathParam(), publishActionRequest));
    }

    @Override
    public ResultInfo delete(long pageId) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.delete(pageType.getPathParam(), pageId));
    }
}
