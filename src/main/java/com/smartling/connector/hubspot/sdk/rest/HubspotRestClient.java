package com.smartling.connector.hubspot.sdk.rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.rest.api.AuthorizationApi;
import com.smartling.connector.hubspot.sdk.rest.api.PagesApi;
import com.smartling.connector.hubspot.sdk.rest.api.RefreshData;
import feign.Feign;
import feign.gson.GsonDecoder;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

public class HubspotRestClient implements HubspotClient
{
    protected static final String   API_HOST  = "https://api.hubapi.com";
    protected static final PagesApi PAGES_API = Feign.builder().target(PagesApi.class, API_HOST);

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
        checkAccessToken();

        return PAGES_API.page(pageId, accessToken);
    }

    @Override
    public String clonePage(final long originalPageId)
    {
        checkAccessToken();

        return PAGES_API.clone(originalPageId, accessToken);
    }

    @Override
    public String updatePage(final String page)
    {
        long pageId = readPageId(page);
        checkAccessToken();

        return PAGES_API.update(pageId, accessToken, page);
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
        System.out.println(accessToken);
    }

}