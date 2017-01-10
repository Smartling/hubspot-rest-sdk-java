package com.smartling.connector.hubspot.sdk.it;

import org.junit.Before;

import static org.fest.assertions.api.Assertions.assertThat;

public abstract class BaseIntegrationTest
{
    protected String refreshToken;
    protected String clientId;
    protected String clientSecret;
    protected String redirectUri;

    @Before
    public void checkRequiredProperties()
    {
        refreshToken = System.getProperty("hubspot.refreshToken");
        clientId = System.getProperty("hubspot.clientId");
        clientSecret = System.getProperty("hubspot.clientSecret");
        redirectUri = System.getProperty("hubspot.redirectUri");

        assertThat(refreshToken).overridingErrorMessage("Access token for Hubspot API is missing!").isNotEmpty();
        assertThat(clientId).overridingErrorMessage("Client id for Hubspot application is missing!").isNotEmpty();
    }
}
