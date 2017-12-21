package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotPageClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.page.PageDetail;
import com.smartling.connector.hubspot.sdk.page.PageDetails;
import com.smartling.connector.hubspot.sdk.page.PageSearchFilter;
import com.smartling.connector.hubspot.sdk.rest.api.PagesEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.PagesRawApi;

import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.Feign;
import feign.Request.Options;
import feign.gson.GsonDecoder;
import feign.httpclient.ApacheHttpClient;

public class HubspotRestPageClient extends AbstractHubspotRestClient implements HubspotPageClient
{
    private static final String EMPTY_JSON = "{}";

    private final PagesRawApi pagesRawApi;
    private final PagesEntityApi pagesEntityApi;
    private final PagesEntityApi pagesEntityApiApache;

    public HubspotRestPageClient(final Configuration configuration, final TokenProvider tokenProvider)
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
    public PageDetail getPageDetailById(final long pageId) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.pageDetail(pageId));
    }

    @Override
    public String clonePage(final long originalPageId) throws HubspotApiException
    {
        return execute(() -> pagesRawApi.clone(originalPageId, EMPTY_JSON));
    }

    @Override
    public PageDetail clonePageAsDetail(final long originalPageId) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.clone(originalPageId, EMPTY_JSON));
    }

    @Override
    public String updatePage(final String page, final long updatePageId) throws HubspotApiException
    {
        return execute(() -> pagesRawApi.update(updatePageId, page));
    }

    @Override
    public PageDetails listPages(final int offset, final int limit) throws HubspotApiException
    {
        return execute(() -> pagesEntityApiApache.pages(limit, offset));
    }

    @Override
    public PageDetails listPages(final int offset, final int limit, PageSearchFilter filter) throws HubspotApiException
    {
        return execute(() -> pagesEntityApiApache.pages(filter.getArchived(), filter.getDraft(), filter.getName(),
                                                              filter.getCampaign(), limit, offset));
    }

    @Override
    public PageDetails listPagesByTmsId(final String tmsId) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.findByTmsId(tmsId));
    }

    @Override
    public ResultInfo delete(final long pageId) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.delete(pageId));
    }
}
