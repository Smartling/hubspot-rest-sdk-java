package com.smartling.connector.hubspot.sdk.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClient.Configuration;

public class TokenProviderTest
{
    private static final int PORT = 10000 + new Random().nextInt(9999);

    protected static final String BASE_URL      = "http://localhost:" + PORT;
    protected static final String REFRESH_TOKEN = "3333-4444-5555";
    protected static final String CLIENT_ID     = "0000-1111-2222";
    protected static final String ACCESS_TOKEN = "access-token";
    protected static final int EXPIRES_IN_TOKEN = 28799;

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    protected TokenProvider tokenProvider;

    @Before
    public void setUp() throws Exception
    {
        this.tokenProvider = createTokenProvider();
        stubFor(post(urlStartingWith("/auth")).willReturn(aJsonResponse(getTokenData())));
    }

    @Test
    public void testGetTokenData()
    {
        RefreshTokenData token = this.tokenProvider.getTokenData();
        assertEquals(ACCESS_TOKEN, token.getAccessToken());
        assertEquals(EXPIRES_IN_TOKEN, token.getExpiresIn());

        verify(postRequestedFor(urlStartingWith("/auth"))
                .withRequestBody(withFormParam("client_id", CLIENT_ID))
                .withRequestBody(withFormParam("refresh_token", REFRESH_TOKEN))
                .withRequestBody(withFormParam("grant_type", "refresh_token")) );
    }

    protected TokenProvider createTokenProvider() throws Exception
    {
        return new TokenProvider(Configuration.build(BASE_URL, CLIENT_ID, REFRESH_TOKEN));
    }

    protected static UrlMatchingStrategy urlStartingWith(String path)
    {
        return urlMatching(path + ".*");
    }

    protected ValueMatchingStrategy withFormParam(String key, String value)
    {
        return containing(key + "=" + value);
    }

    private static ResponseDefinitionBuilder aJsonResponse(String json)
    {
        return aResponse().withHeader("Content-Type", "application/json").withBody(json);
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
