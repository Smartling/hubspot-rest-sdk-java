package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotPagesClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.page.PageDetail;
import com.smartling.connector.hubspot.sdk.page.PageSearchFilter;
import com.smartling.connector.hubspot.sdk.page.PageState;
import com.smartling.connector.hubspot.sdk.rest.api.PagesEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.PagesRawApi;

import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.Feign;
import feign.Request.Options;
import feign.gson.GsonDecoder;
import feign.httpclient.ApacheHttpClient;

import java.util.HashMap;
import java.util.Map;

import static com.smartling.connector.hubspot.sdk.page.PageState.DRAFT;
import static com.smartling.connector.hubspot.sdk.page.PageState.PUBLISHED;
import static com.smartling.connector.hubspot.sdk.page.PageState.SCHEDULED;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
    public String createLanguageVariation(long pageId, CreateLanguageVariationRequest createLanguageVariationRequest) throws HubspotApiException
    {
        return execute(() -> pagesRawApi.createLanguageVariation(pageId, createLanguageVariationRequest));
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
    public ListWrapper<PageDetail> listPages(final int offset, final int limit, final String orderBy, final PageSearchFilter filter) throws HubspotApiException
    {
        Map<String, Object> queryMap = new HashMap<>();
        if (filter != null)
        {
            if(filter.getId() != null)
            {
                queryMap.put("id", filter.getId());
            }
            if(isNotBlank(filter.getName()))
            {
                queryMap.put("name__icontains", filter.getName());
            }
            if (filter.getArchived() != null)
            {
                queryMap.put("archived", filter.getArchived());
            }
            if(filter.getPageState() != null)
            {
                PageState pageState = filter.getPageState();
                if (pageState == DRAFT)
                {
                    queryMap.put("is_draft", "true");
                }
                if (pageState == PUBLISHED)
                {
                    queryMap.put("is_published", "true");
                }
                if (pageState == SCHEDULED)
                {
                    queryMap.put("scheduled", "true");
                }
            }
            if(isNotBlank(filter.getCampaign()))
            {
                queryMap.put("campaign", filter.getCampaign());
            }
            if(filter.getSubcategory() != null)
            {
                queryMap.put("subcategory", filter.getSubcategory());
            }
        }

        return execute(() -> pagesEntityApiApache.listPages(limit, offset, orderBy, queryMap));
    }

    @Override
    public ResultInfo delete(final long pageId) throws HubspotApiException
    {
        return execute(() -> pagesEntityApi.delete(pageId));
    }
}
