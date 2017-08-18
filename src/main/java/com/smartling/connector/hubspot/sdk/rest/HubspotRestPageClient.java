package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotPageClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.page.PageDetail;
import com.smartling.connector.hubspot.sdk.page.PageDetails;
import com.smartling.connector.hubspot.sdk.page.PageSearchFilter;
import com.smartling.connector.hubspot.sdk.rest.api.PagesEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.PagesRawApi;

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

    public HubspotRestPageClient(final Configuration configuration, final RestExecutor executor)
    {
        super(executor);

        Options connectionConfig = new Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());

        pagesRawApi = Feign.builder()
                           .options(connectionConfig)
                           .target(PagesRawApi.class, configuration.getApiUrl());

        pagesEntityApi = Feign.builder()
                              .options(connectionConfig)
                              .decoder(new GsonDecoder(configuredGson()))
                              .target(PagesEntityApi.class, configuration.getApiUrl());

        // ApacheHttpClient has advanced options for request/response processing
        pagesEntityApiApache = Feign.builder()
                                    .options(connectionConfig)
                                    .client(new ApacheHttpClient())
                                    .decoder(new GsonDecoder(configuredGson()))
                                    .target(PagesEntityApi.class, configuration.getApiUrl());

    }

    @Override
    public String getPageById(long pageId) throws HubspotApiException
    {
        return execute(token -> pagesRawApi.page(pageId, token));
    }

    @Override
    public PageDetail getPageDetailById(final long pageId) throws HubspotApiException
    {
        return execute(token -> pagesEntityApi.pageDetail(pageId, token));
    }

    @Override
    public String clonePage(final long originalPageId) throws HubspotApiException
    {
        return execute(token -> pagesRawApi.clone(originalPageId, token, EMPTY_JSON));
    }

    @Override
    public PageDetail clonePageAsDetail(final long originalPageId) throws HubspotApiException
    {
        return execute(token -> pagesEntityApi.clone(originalPageId, token, EMPTY_JSON));
    }

    @Override
    public String updatePage(final String page, final long updatePageId) throws HubspotApiException
    {
        return execute(token -> pagesRawApi.update(updatePageId, page, token));
    }

    @Override
    public PageDetails listPages(final int offset, final int limit) throws HubspotApiException
    {
        return execute(token -> pagesEntityApiApache.pages(limit, offset, token));
    }

    @Override
    public PageDetails listPages(final int offset, final int limit, PageSearchFilter filter) throws HubspotApiException
    {
        return execute(token -> pagesEntityApiApache.pages(filter.getArchived(), filter.getDraft(), filter.getName(),
                                                              filter.getCampaign(), limit, offset, token));
    }

    @Override
    public PageDetails listPagesByTmsId(final String tmsId) throws HubspotApiException
    {
        return execute(token -> pagesEntityApi.findByTmsId(tmsId, token));
    }

    @Override
    public ResultInfo delete(final long pageId) throws HubspotApiException
    {
        return execute(token -> pagesEntityApi.delete(pageId, token));
    }
}
