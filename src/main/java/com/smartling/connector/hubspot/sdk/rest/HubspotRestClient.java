package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.rest.api.AuthorizationApi;
import com.smartling.connector.hubspot.sdk.rest.api.Page;
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
    protected static final PagesApi PAGES_API = Feign.builder().decoder(new GsonDecoder()).target(PagesApi.class, API_HOST);

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
    public Page getPageById(long pageId)
    {
        checkAccessToken();

        return PAGES_API.page(pageId, accessToken);
    }

    @Override
    public Page clonePage(final long originalPageId)
    {
        checkAccessToken();

        return PAGES_API.clone(originalPageId, accessToken);
    }

    @Override
    public Page updatePage(final Page page)
    {
        checkAccessToken();

        return PAGES_API.update(page.getId(), accessToken, page);
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