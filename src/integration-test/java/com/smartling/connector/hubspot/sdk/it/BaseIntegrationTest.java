package com.smartling.connector.hubspot.sdk.it;

import org.junit.Before;

import static org.fest.assertions.api.Assertions.assertThat;

public abstract class BaseIntegrationTest
{
    protected String refreshToken;
    protected String redirectUri;
    protected String clientId;
    protected String clientSecret;

    @Before
    public void checkRequiredProperties()
    {
        refreshToken = System.getProperty("hubspot.refreshToken");
        redirectUri = System.getProperty("hubspot.redirectUri");
        clientId = System.getProperty("hubspot.clientId");
        clientSecret = System.getProperty("hubspot.clientSecret");

        assertThat(refreshToken).overridingErrorMessage("Access token for Hubspot API is missing!").isNotEmpty();
        assertThat(redirectUri).overridingErrorMessage("redirect URI for Hubspot API is missing!").isNotEmpty();
        assertThat(clientId).overridingErrorMessage("Client id for Hubspot application is missing!").isNotEmpty();
        assertThat(clientSecret).overridingErrorMessage("Client Secret for Hubspot application is missing!").isNotEmpty();
    }
}
