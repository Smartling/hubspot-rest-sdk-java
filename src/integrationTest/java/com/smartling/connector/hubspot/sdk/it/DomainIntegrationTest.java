package com.smartling.connector.hubspot.sdk.it;

import com.smartling.connector.hubspot.sdk.HubspotDomainClient;
import com.smartling.connector.hubspot.sdk.domain.DomainDetails;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager;
import org.junit.Before;
import org.junit.Test;

import static com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager.createTokenProvider;
import static org.fest.assertions.api.Assertions.assertThat;

public class DomainIntegrationTest extends BaseIntegrationTest
{
    private HubspotDomainClient hubspotDomainClient;

    @Before
    public void init()
    {
        final Configuration configuration = Configuration.build(clientId, clientSecret, redirectUri, refreshToken);
        hubspotDomainClient = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getDomainClient();
    }

    @Test
    public void shouldReturnAllTags() throws Exception
    {
        DomainDetails domainDetails = hubspotDomainClient.getDomainDetails(0, null, null);
        assertThat(domainDetails).isNotNull();
        assertThat(domainDetails.getDetailList()).isNotEmpty();
    }

}
