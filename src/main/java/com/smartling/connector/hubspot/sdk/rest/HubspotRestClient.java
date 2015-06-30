package com.smartling.connector.hubspot.sdk.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.smartling.connector.hubspot.sdk.DeletePageInfo;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.PageDetail;
import com.smartling.connector.hubspot.sdk.PageDetails;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.api.AuthorizationApi;
import com.smartling.connector.hubspot.sdk.rest.api.PagesEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.PagesRawApi;
import feign.Feign;
import feign.FeignException;
import feign.gson.GsonDecoder;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.BiFunction;

import static java.time.LocalDateTime.now;

public class HubspotRestClient implements HubspotClient
{
    private static final String API_HOST = "https://api.hubapi.com";

    private final PagesRawApi      pagesRawApi;
    private final PagesEntityApi   pagesEntityApi;
    private final AuthorizationApi authorizationApi;
    private final String           refreshToken;
    private final String           clientId;

    private AccessToken accessToken;

    public HubspotRestClient(final String clientId, final String refreshToken)
    {
        this(API_HOST, clientId, refreshToken);
    }

    public HubspotRestClient(final String apiUrl, final String clientId, final String refreshToken)
    {
        this.clientId = clientId;
        this.refreshToken = refreshToken;

        pagesRawApi = Feign.builder()
                           .target(PagesRawApi.class, apiUrl);

        pagesEntityApi = Feign.builder()
                              .decoder(new GsonDecoder(configuredGson()))
                              .target(PagesEntityApi.class, apiUrl);

        authorizationApi = Feign.builder()
                                .decoder(new GsonDecoder())
                                .target(AuthorizationApi.class, apiUrl);
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
        return executeWithToken(pagesRawApi::page, pageId);
    }

    @Override
    public PageDetail getPageDetailById(final long pageId) throws HubspotApiException
    {
        return executeWithToken(pagesEntityApi::pageDetail, pageId);
    }

    @Override
    public String clonePage(final long originalPageId) throws HubspotApiException
    {
        return executeWithToken(pagesRawApi::clone, originalPageId);
    }

    @Override
    public PageDetail clonePageAsDetail(final long originalPageId) throws HubspotApiException
    {
        return executeWithToken(pagesEntityApi::clone, originalPageId);
    }

    @Override
    public String updatePage(final String page) throws HubspotApiException
    {
        long pageId = readPageId(page);

        return executeWithToken(pagesRawApi::update, pageId, page);
    }

    @Override
    public PageDetails listPages(final int offset, final int limit) throws HubspotApiException
    {
        return executeWithToken(pagesEntityApi::pages, limit, offset);
    }

    @Override
    public PageDetails listPagesByTmsId(final String tmsId) throws HubspotApiException
    {
        return executeWithToken(pagesEntityApi::findByTmsId, tmsId);
    }

    private <T, R> R executeWithToken(BiFunction<T, String, R> apiCall, T firstArgument) throws HubspotApiException
    {
        refreshAccessToken();

        try
        {
            return apiCall.apply(firstArgument, accessToken.getToken());
        }
        catch (FeignException e)
        {
            throw new HubspotApiException("Call to Hubspot API failed!", e);
        }
    }

    private <T, U, R> R executeWithToken(TripleFunction<T, U, String, R> apiCall, T firstArgument, U secondArgument) throws HubspotApiException
    {
        refreshAccessToken();

        try
        {
            return apiCall.apply(firstArgument, secondArgument, accessToken.getToken());
        }
        catch (FeignException e)
        {
            throw new HubspotApiException("Call to Hubspot API failed!", e);
        }
    }

    private long readPageId(final String page) throws HubspotApiException
    {
        JsonParser parser = new JsonParser();

        try
        {
            JsonObject obj = parser.parse(page).getAsJsonObject();
            return obj.get("id").getAsLong();
        }
        catch (JsonSyntaxException e)
        {
            throw new HubspotApiException("JSON syntax of page snippet is wrong!", e);
        }
    }

    private void refreshAccessToken() throws HubspotApiException
    {
        accessToken = new AccessToken(refreshToken());
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
        return executeWithToken(pagesEntityApi::delete, pageId);
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

    @FunctionalInterface
    public interface TripleFunction<T, U, V, R>
    {
        R apply(T t, U u, V v);
    }

}