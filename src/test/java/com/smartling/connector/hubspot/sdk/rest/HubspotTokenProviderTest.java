package com.smartling.connector.hubspot.sdk.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.token.HubspotTokenProvider;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;

public class HubspotTokenProviderTest
{
    private static final int PORT = 10000 + new Random().nextInt(9999);

    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final String REFRESH_TOKEN = "3333-4444-5555";
    private static final String CLIENT_ID = "0000-1111-2222";
    private static final String CLIENT_SECRET = "0000-1111-2222";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REDIRECT_URI = "http://example.com";

    private static final int EXPIRES_IN_TOKEN = 28799;

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TokenProvider tokenProvider = new HubspotTokenProvider(Configuration.build(BASE_URL, CLIENT_ID, CLIENT_SECRET, REFRESH_TOKEN, REDIRECT_URI));

    @Before
    public void setUp() throws Exception
    {
        stubFor(post(HttpMockUtils.urlStartingWith("/oauth/")).willReturn(HttpMockUtils.aJsonResponse(getTokenData())));
    }

    @Test
    public void testGetTokenData() throws Exception
    {
        RefreshTokenData token = this.tokenProvider.getTokenData();
        assertEquals(ACCESS_TOKEN, token.getAccessToken());
        assertEquals(EXPIRES_IN_TOKEN, token.getExpiresIn());

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/oauth"))
                .withRequestBody(HttpMockUtils.withFormParam("client_id", CLIENT_ID))
                .withRequestBody(HttpMockUtils.withFormParam("client_secret", CLIENT_SECRET))
                .withRequestBody(HttpMockUtils.withFormParam("redirect_uri", REDIRECT_URI))
                .withRequestBody(HttpMockUtils.withFormParam("refresh_token", REFRESH_TOKEN))
                .withRequestBody(HttpMockUtils.withFormParam("grant_type", "refresh_token")) );
    }

    private String getTokenData()
    {
        return "{\n"
                + "  \"portal_id\": 584677,\n"
                + "  \"expires_in\": " + EXPIRES_IN_TOKEN + ",\n"
                + "  \"refresh_token\": \"684f2944-b474-4440-8b2a-207d4c26a959\",\n"
                + "  \"access_token\": \"" + ACCESS_TOKEN + "\"\n"
                + "}";
    }
}
