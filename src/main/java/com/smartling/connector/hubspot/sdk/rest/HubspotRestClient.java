package com.smartling.connector.hubspot.sdk.rest;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Function;

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
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.api.AuthorizationApi;
import com.smartling.connector.hubspot.sdk.rest.api.PagesEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.PagesRawApi;

import feign.Feign;
import feign.FeignException;
import feign.Request.Options;
import feign.gson.GsonDecoder;

import static java.time.LocalDateTime.now;

public class HubspotRestClient implements HubspotClient
{
    private static final String API_HOST = "https://api.hubapi.com";

    private final PagesRawApi      pagesRawApi;
    private final PagesEntityApi   pagesEntityApi;
    private final AuthorizationApi authorizationApi;
    private final String           refreshToken;
    private final String           clientId;

    public HubspotRestClient(final Configuration configuration)
    {       
        this.clientId = configuration.getClientId();
        this.refreshToken = configuration.getRefreshToken();

        Options connectionConfig = new Options(
                configuration.getConnectTimeoutMillis(), configuration.getReadTimeoutMillis());
        
        pagesRawApi = Feign.builder()
                           .options(connectionConfig)
                           .target(PagesRawApi.class, configuration.getApiUrl());

        pagesEntityApi = Feign.builder()
                              .options(connectionConfig)
                              .decoder(new GsonDecoder(configuredGson()))
                              .target(PagesEntityApi.class, configuration.getApiUrl());

        authorizationApi = Feign.builder()
                                .options(connectionConfig)
                                .decoder(new GsonDecoder())
                                .target(AuthorizationApi.class, configuration.getApiUrl());
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
        return executeWithToken(token -> pagesEntityApi.pages(limit, offset, token));
    }
    
    @Override
    public PageDetails listPages(final int offset, final int limit, PageSearchFilter filter) throws HubspotApiException
    {
        return executeWithToken(token -> pagesEntityApi.pages(filter.getArchived(), filter.getDraft(), filter.getName(),
                                                              filter.getCampaign(), limit, offset, token));
    }

    @Override
    public PageDetails listPagesByTmsId(final String tmsId) throws HubspotApiException
    {
        return executeWithToken(token -> pagesEntityApi.findByTmsId(tmsId, token));
    }
    
    private <T> T executeWithToken(Function<String, T> apiCall) throws HubspotApiException
    {
        AccessToken accessToken = refreshAccessToken();

        try
        {
            return apiCall.apply(accessToken.getToken());
        }
        catch (FeignException e)
        {
            throw new HubspotApiException("Call to Hubspot API failed!", e);
        }
    }

    private AccessToken refreshAccessToken() throws HubspotApiException
    {
        return new AccessToken(refreshToken());
    }

    @Override
    public RefreshTokenData refreshToken() throws HubspotApiException
    {
        try
        {
            return authorizationApi.newToken(clientId, refreshToken);

        }
        catch (FeignException e)
        {
            throw new HubspotApiException("Authorization to Hubspot failed!", e);
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

    private static class AccessToken
    {
        private String        accessToken;
        private Duration      duration;
        private LocalDateTime refreshTokenDateTime;

        public AccessToken(RefreshTokenData refreshTokenData)
        {
            refreshTokenDateTime = LocalDateTime.now();
            int expiresIn = refreshTokenData.getExpiresIn();
            duration = Duration.ofSeconds(expiresIn > 5 ? expiresIn - 5 : expiresIn);
            accessToken = refreshTokenData.getAccessToken();
        }

        public boolean tokenExpired()
        {
            return refreshTokenDateTime.plus(duration).isBefore(now());
        }

        public String getToken()
        {
            return accessToken;
        }
    }
    
    public static class Configuration
    {
        private String apiUrl;
        private String clientId;
        private String refreshToken;
        private int    connectTimeoutMillis = 10_000;
        private int    readTimeoutMillis    = 60_000;

        private Configuration(String clientId, String refreshToken)
        {
            this(API_HOST, clientId, refreshToken);
        }

        private Configuration(String apiUrl, String clientId, String refreshToken)
        {
            this.apiUrl = apiUrl;
            this.clientId = clientId;
            this.refreshToken = refreshToken;
        }

        public static Configuration build(String clientId, String refreshToken)
        {
            return new Configuration(clientId, refreshToken);
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

    }

}