package com.smartling.connector.hubspot.sdk.it;

import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager;
import com.smartling.connector.hubspot.sdk.v3.HubspotPagesV3Client;
import com.smartling.connector.hubspot.sdk.v3.page.ListWrapper;
import com.smartling.connector.hubspot.sdk.v3.page.PageDetail;
import com.smartling.connector.hubspot.sdk.v3.page.PageType;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager.createTokenProvider;
import static org.fest.assertions.api.Assertions.assertThat;

public class PagesIntegrationV3Test extends BaseIntegrationTest
{
    private HubspotPagesV3Client hubspotPagesV3Client;

    @Before
    public void init()
    {
        final Configuration configuration = Configuration.build(clientId, clientSecret, redirectUri, refreshToken);
        hubspotPagesV3Client = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getPagesV3Client(PageType.SITE_PAGE);
    }

    @Test
    public void shouldListPages() throws Exception
    {
        Map<String, Object> filter = new HashMap<>();
        filter.put("domain", "content.smartling-domain.com");

        ListWrapper<PageDetail> pageDetailListWrapper = hubspotPagesV3Client.listPages(0, 0, null, filter);
        assertThat(pageDetailListWrapper.getDetailList()).isNotEmpty();
    }
}
