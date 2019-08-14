package com.smartling.connector.hubspot.sdk.rest;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotApiNotFoundException;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import feign.FeignException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Supplier;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractHubspotRestClientTest
{
    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private Supplier supplier;

    private AbstractHubspotRestClient abstractHubspotRestClient;

    @Before
    public void setup() throws Exception
    {
        abstractHubspotRestClient = new AbstractHubspotRestClient(tokenProvider){};
        when(tokenProvider.getTokenData()).thenReturn(new RefreshTokenData());
    }

    @Test(expected = HubspotApiException.class)
    public void shouldThrowGenericApiException() throws Exception
    {
        when(supplier.get()).thenThrow(FeignException.class);

        abstractHubspotRestClient.execute(supplier);
    }

    @Test(expected = HubspotApiNotFoundException.class)
    public void shouldThrowNotFoundException() throws Exception
    {
        when(supplier.get()).thenThrow(FeignException.NotFound.class);

        abstractHubspotRestClient.execute(supplier);
    }
}