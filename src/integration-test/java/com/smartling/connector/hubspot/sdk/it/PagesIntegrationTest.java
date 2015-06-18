package com.smartling.connector.hubspot.sdk.it;

import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClient;
import com.smartling.connector.hubspot.sdk.rest.api.Page;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.fest.assertions.api.Assertions.assertThat;

public class PagesIntegrationTest
{
    protected static final long PAGE_ID = 2976789349L;

    private HubspotClient hubspotClient;
    private LocalDateTime now             = LocalDateTime.now();
    private String        testHtml        = "<h1>From integration test at " + now + "</h1>";
    private String        title           = "Title created at " + now;
    private String        metaDescription = "Meta description, created at " + now;

    @Before
    public void checkRequiredProperties()
    {
        final String refreshToken = System.getProperty("hubspot.refreshToken");
        final String clientId = System.getProperty("hubspot.clientId");

        assertThat(refreshToken).overridingErrorMessage("Access token for Hubspot API is missing!").isNotEmpty();
        assertThat(clientId).overridingErrorMessage("Client id for Hubspot application is missing!").isNotEmpty();

        hubspotClient = new HubspotRestClient(refreshToken, clientId);
    }

    @Test
    public void shouldReturnPage() throws Exception
    {
        Page page = hubspotClient.getPageById(PAGE_ID);
        assertThat(page.getHtmlTitle()).overridingErrorMessage("HTML title should has particular text").isEqualTo("Page title for translation");
        assertThat(page.getId()).overridingErrorMessage("Page id should have particular value").isEqualTo(PAGE_ID);
        assertThat(page.getMetaDescription()).overridingErrorMessage("Meta description should have particular text").isEqualTo("meta description");
    }

    @Test
    public void shouldClonePage() throws Exception
    {
        Page cloneFromResponse = hubspotClient.clonePage(PAGE_ID);

        assertClonedPage(cloneFromResponse);
    }

    @Test
    public void shouldUpdatePage() throws Exception
    {
        // prepare clone for update
        Page cloneFromResponse = hubspotClient.clonePage(PAGE_ID);
        long pageId = cloneFromResponse.getId();

        Page updatedPage = hubspotClient.updatePage(page(pageId));

        assertUpdatedMessage(updatedPage, pageId);
    }

    private void assertUpdatedMessage(final Page updatedPage, final long pageId)
    {
        assertThat(updatedPage).overridingErrorMessage("Updated page should not be null").isNotNull();
        assertThat(updatedPage.getHeadHtml()).overridingErrorMessage("Updated page should have new head HTML").isEqualTo(testHtml);
        assertThat(updatedPage.getFooterHtml()).overridingErrorMessage("Updated page should have new footer HTML").isEqualTo(testHtml);
        assertThat(updatedPage.getMetaDescription()).overridingErrorMessage("Updated page should have new meta description").isEqualTo(metaDescription);
        assertThat(updatedPage.getHtmlTitle()).overridingErrorMessage("Updated page should have new title").isEqualTo(title);
        assertThat(updatedPage.getId()).overridingErrorMessage("Id for updated page should be the same").isEqualTo(pageId);
    }

    private Page page(final long pageId)
    {
        Page page = new Page();
        page.setId(pageId);
        page.setFooterHtml(testHtml);
        page.setHeadHtml(testHtml);
        page.setHtmlTitle(title);
        page.setMetaDescription(metaDescription);

        return page;
    }

    private void assertClonedPage(final Page clone)
    {
        assertThat(clone).overridingErrorMessage("Cloned page should not be null").isNotNull();
        assertThat(clone.getHtmlTitle()).overridingErrorMessage("Cloned page should be as original one").isEqualTo("Page title for translation");
        assertThat(clone.getId()).overridingErrorMessage("Cloned page should have another id").isNotEqualTo(PAGE_ID);
    }
}