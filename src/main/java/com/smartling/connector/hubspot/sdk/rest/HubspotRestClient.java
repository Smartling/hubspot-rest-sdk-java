package com.smartling.connector.hubspot.sdk.rest;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.smartling.connector.hubspot.sdk.DeletePageInfo;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.PageDetail;
import com.smartling.connector.hubspot.sdk.PageDetails;
import com.smartling.connector.hubspot.sdk.PageSearchFilter;
import com.smartling.connector.hubspot.sdk.rest.api.PagesEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.PagesRawApi;
import com.smartling.connector.hubspot.sdk.rest.token.HubspotTokenProvider;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;

import feign.Feign;
import feign.FeignException;
import feign.Request.Options;
import feign.gson.GsonDecoder;
import feign.httpclient.ApacheHttpClient;

public class HubspotRestClient implements HubspotClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HubspotRestClient.class);
    private static final String API_HOST = "https://api.hubapi.com";

    private final PagesRawApi      pagesRawApi;
    private final PagesEntityApi   pagesEntityApi;
    private final PagesEntityApi   pagesEntityApiApache;
    private final TokenProvider    tokenProvider;

    public HubspotRestClient(final Configuration configuration)
    {
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

        tokenProvider = createTokenProvider(configuration);
    }

    private static Gson configuredGson()
    {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateSerializer())
                .create();
    }

    @Override
    public String getPageById(long pageId) throws HubspotApiException
    {
        return executeWithToken(token -> pagesRawApi.page(pageId, token));
    }

    @Override
    public PageDetail getPageDetailById(final long pageId) throws HubspotApiException
    {
        return executeWithToken(token -> pagesEntityApi.pageDetail(pageId, token));
    }

    @Override
    public String clonePage(final long originalPageId) throws HubspotApiException
    {
        return executeWithToken(token -> pagesRawApi.clone(originalPageId, token));
    }

    @Override
    public PageDetail clonePageAsDetail(final long originalPageId) throws HubspotApiException
    {
        return executeWithToken(token -> pagesEntityApi.clone(originalPageId, token));
    }

    @Override
    public String updatePage(final String page, final long updatePageId) throws HubspotApiException
    {
        return executeWithToken(token -> pagesRawApi.update(updatePageId, page, token));
    }

    @Override
    public PageDetails listPages(final int offset, final int limit) throws HubspotApiException
    {
        return executeWithToken(token -> pagesEntityApiApache.pages(limit, offset, token));
    }

    @Override
    public PageDetails listPages(final int offset, final int limit, PageSearchFilter filter) throws HubspotApiException
    {
        return executeWithToken(token -> pagesEntityApiApache.pages(filter.getArchived(), filter.getDraft(), filter.getName(),
                                                              filter.getCampaign(), limit, offset, token));
    }

    @Override
    public PageDetails listPagesByTmsId(final String tmsId) throws HubspotApiException
    {
        return executeWithToken(token -> pagesEntityApi.findByTmsId(tmsId, token));
    }

    @SuppressWarnings("unchecked")
    protected TokenProvider createTokenProvider(final Configuration configuration)
    {
        TokenProvider provider = new HubspotTokenProvider(configuration);
        String decoratorClassName = configuration.getPropertyValue(TokenProvider.TOKEN_PROVIDER_DECORATOR_CLASS);
        if (StringUtils.isNotBlank(decoratorClassName))
        {
            try
            {
                provider = ConstructorUtils.invokeConstructor((Class<TokenProvider>)ClassUtils.getClass(decoratorClassName), configuration, provider);
                LOGGER.info("tokenProvider is decorated by {} class", decoratorClassName);
            }
            catch (Exception e)
            {
                LOGGER.warn("Cannot decorate tokenProvider by {} decorator", decoratorClassName, e);
            }
        }
        return provider;
    }

    private <T> T executeWithToken(Function<String, T> apiCall) throws HubspotApiException
    {
        try
        {
            return apiCall.apply(tokenProvider.getTokenData().getAccessToken());
        }
        catch (FeignException e)
        {
            throw new HubspotApiException("Call to Hubspot API failed!", e);
        }
    }

    @Override
    public DeletePageInfo delete(final long pageId) throws HubspotApiException
    {
        return executeWithToken(token -> pagesEntityApi.delete(pageId, token));
    }

    private static class DateSerializer implements JsonDeserializer<Date>
    {
        @Override
        public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException
        {
            return new Date(json.getAsLong());
        }
    }

    public static class Configuration
    {
        private String apiUrl;
        private String clientId;
        private String refreshToken;
        private int    connectTimeoutMillis = 10_000;
        private int    readTimeoutMillis    = 60_000;
        private Map<String, String> properties = Collections.emptyMap();

        private Configuration(String apiUrl, String clientId, String refreshToken)
        {
            this.apiUrl = apiUrl;
            this.clientId = clientId;
            this.refreshToken = refreshToken;
        }

        public static Configuration build(String clientId, String refreshToken)
        {
            return new Configuration(API_HOST, clientId, refreshToken);
        }

        public static Configuration build(String apiUrl, String clientId, String refreshToken)
        {
            return new Configuration(apiUrl, clientId, refreshToken);
        }

        public String getApiUrl()
        {
            return apiUrl;
        }

        public String getClientId()
        {
            return clientId;
        }

        public String getRefreshToken()
        {
            return refreshToken;
        }

        public int getConnectTimeoutMillis()
        {
            return connectTimeoutMillis;
        }

        public int getReadTimeoutMillis()
        {
            return readTimeoutMillis;
        }

        public String getPropertyValue(String name)
        {
            return this.properties.get(name);
        }

        public Configuration setConnectTimeoutMillis(int connectTimeoutMillis)
        {
            this.connectTimeoutMillis = connectTimeoutMillis;
            return this;
        }

        public Configuration setReadTimeoutMillis(int readTimeoutMillis)
        {
            this.readTimeoutMillis = readTimeoutMillis;
            return this;
        }

        public Configuration setProperties(Map<String, String> properties)
        {
            this.properties = properties;
            return this;
        }
    }
}
