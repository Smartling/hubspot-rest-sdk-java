package com.smartling.connector.hubspot.sdk.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.rest.api.AuthorizationApi;
import com.smartling.connector.hubspot.sdk.rest.api.PageDetails;
import com.smartling.connector.hubspot.sdk.rest.api.PagesEntityApi;
import com.smartling.connector.hubspot.sdk.rest.api.PagesRawApi;
import com.smartling.connector.hubspot.sdk.rest.api.RefreshData;
import feign.Feign;
import feign.gson.GsonDecoder;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.BiFunction;

import static java.time.LocalDateTime.now;

public class HubspotRestClient implements HubspotClient
{
    protected static final String         API_HOST         = "https://api.hubapi.com";
    protected static final PagesRawApi    PAGES_RAW_API    = Feign.builder()
                                                                  .target(PagesRawApi.class, API_HOST);
    protected static final PagesEntityApi PAGES_ENTITY_API = Feign.builder()
                                                                  .decoder(new GsonDecoder(configuredGson()))
                                                                  .target(PagesEntityApi.class, API_HOST);

    private static Gson configuredGson()
    {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateSerializer())
                .create();
    }

    private final String        refreshToken;
    private final String        clientId;
    private       String        accessToken;
    private       Duration      duration;
    private       LocalDateTime refreshTokenDateTime;

    public HubspotRestClient(final String refreshToken, final String clientId)
    {
        this.refreshToken = refreshToken;
        this.clientId = clientId;
    }

    @Override
    public String getPageById(long pageId)
    {
        return executeWithToken(PAGES_RAW_API::page, pageId);
    }

    @Override
    public String clonePage(final long originalPageId)
    {
        return executeWithToken(PAGES_RAW_API::clone, originalPageId);
    }

    @Override
    public String updatePage(final String page)
    {
        long pageId = readPageId(page);

        return executeWithToken(PAGES_RAW_API::update, pageId, page);
    }

    @Override
    public PageDetails listPages(final int limit, final int offset)
    {
        return executeWithToken(PAGES_ENTITY_API::pages, limit, offset);
    }

    @Override
    public PageDetails listPagesByTmsId(final String tmsId)
    {
        return executeWithToken(PAGES_ENTITY_API::findByTmsId, tmsId);
    }

    private <T, R> R executeWithToken(BiFunction<T, String, R> apiCall, T firstArgument)
    {
        checkAccessToken();

        return apiCall.apply(firstArgument, accessToken);
    }

    private <T, U, R> R executeWithToken(TripleFunction<T, U, String, R> apiCall, T firstArgument, U secondArgument)
    {
        checkAccessToken();

        return apiCall.apply(firstArgument, secondArgument, accessToken);
    }

    private long readPageId(final String page)
    {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(page).getAsJsonObject();
        return obj.get("id").getAsLong();
    }

    private void checkAccessToken()
    {
        if (accessToken == null || tokenExpired())
        {
            authorize();
        }
    }

    private boolean tokenExpired()
    {
        return refreshTokenDateTime.plus(duration).isBefore(now());
    }

    public RefreshData authorize()
    {
        AuthorizationApi authorizationApi = Feign.builder().decoder(new GsonDecoder()).target(AuthorizationApi.class, API_HOST);
        RefreshData target = authorizationApi.newToken(clientId, refreshToken);
        getTokenInfo(target);

        return target;
    }

    private void getTokenInfo(final RefreshData target)
    {
        refreshTokenDateTime = LocalDateTime.now();
        duration = Duration.ofSeconds(target.getExpiresIn());
        accessToken = target.getAccessToken();
    }

    private static class DateSerializer implements JsonDeserializer<Date>
    {
        @Override
        public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException
        {
            return new Date(json.getAsLong());
        }
    }

    @FunctionalInterface
    public interface TripleFunction<T, U, V, R>
    {
        R apply(T t, U u, V v);
    }

}