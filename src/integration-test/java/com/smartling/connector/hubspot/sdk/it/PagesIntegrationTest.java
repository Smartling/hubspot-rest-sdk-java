package com.smartling.connector.hubspot.sdk.it;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.PageDetails;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClient;
import com.smartling.connector.hubspot.sdk.rest.api.PageDetail;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class PagesIntegrationTest
{
    private static final long   PAGE_ID               = 2976789349L;
    private static final String META_DESCRIPTION      = "meta_description";
    private static final String META_KEYWORDS         = "meta_keywords";
    private static final String TMS_ID                = "tms_id";
    private static final String ROOT_PATH             = "$.";
    private static final String META_DESCRIPTION_PATH = ROOT_PATH + META_DESCRIPTION;
    private static final String META_KEYWORDS_PATH    = ROOT_PATH + META_KEYWORDS;
    private static final String ID_PATH               = "$.id";

    private HubspotClient hubspotClient;
    private LocalDateTime now                    = LocalDateTime.now();
    private String        updatedMetaDescription = "Meta description, created at " + now;
    private String        updatedMetaKeywords    = "Meta keywords, created at " + now;
    private String        tmsId                  = "Tms Id ";

    @Before
    public void checkRequiredProperties()
    {
        final String refreshToken = System.getProperty("hubspot.refreshToken");
        final String clientId = System.getProperty("hubspot.clientId");

        assertThat(refreshToken).overridingErrorMessage("Access token for Hubspot API is missing!").isNotEmpty();
        assertThat(clientId).overridingErrorMessage("Client id for Hubspot application is missing!").isNotEmpty();

        hubspotClient = new HubspotRestClient(clientId, refreshToken);
    }

    @Test
    public void shouldReturnPage() throws Exception
    {
        String pageAsJson = hubspotClient.getPageById(PAGE_ID);
        with(pageAsJson)
                .assertThat(META_DESCRIPTION_PATH, equalTo("meta description"), "Meta description should have particular text")
                        //don't know where to set meta keywords
                .assertThat(META_KEYWORDS_PATH, isEmptyOrNullString(), "Meta keywords should not be filled")
                .assertThat("$.id", equalTo(PAGE_ID), "Page id should have particular value");
    }

    @Test
    public void shouldListPages() throws Exception
    {
        PageDetails pageDetails = hubspotClient.listPages(0, 1);
        assertThat(pageDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(pageDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();

        List<PageDetail> detailList = pageDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").isNotNull().hasSize(1);

        assertPageDetailIsNotEmpty(detailList.get(0));
    }

    @Test
    public void shouldListPagesByTmsId() throws Exception
    {

        // create clone with tmsId
        hubspotClient.updatePage(getCloneAndChangeIt());

        PageDetails pageDetails = hubspotClient.listPagesByTmsId(tmsId);

        assertThat(pageDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(pageDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();

        List<PageDetail> detailList = pageDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty").isNotNull().isNotEmpty();

        assertPageDetailIsNotEmpty(detailList.get(0));
    }

    @Test(expected = HubspotApiException.class)
    public void shouldThrowExceptionIfAuthorizationFailed() throws HubspotApiException
    {
        HubspotClient hubspotClient = new HubspotRestClient("wrong-client-id", "wrong-token");
        hubspotClient.listPages(0, 1);
    }

    private String getCloneAndChangeIt() throws HubspotApiException
    {
        String cloneFromResponse = hubspotClient.clonePage(PAGE_ID);
        return change(cloneFromResponse);
    }

    private void assertPageDetailIsNotEmpty(final PageDetail pageDetail)
    {
        assertThat(pageDetail.getHtmlTitle()).isNotEmpty();
        assertThat(pageDetail.getName()).isNotEmpty();
        assertThat(pageDetail.getId()).isPositive();
        assertThat(pageDetail.getUpdated()).isNotNull();
    }

    @Test
    public void shouldClonePage() throws Exception
    {
        String cloneFromResponse = hubspotClient.clonePage(PAGE_ID);

        assertClonedPage(cloneFromResponse);
    }

    @Test
    public void shouldUpdatePage() throws Exception
    {
        // prepare clone for update
        String changeBeforeUpdate = getCloneAndChangeIt();

        String updatedPage = hubspotClient.updatePage(changeBeforeUpdate);

        assertUpdatedMessage(updatedPage);
    }

    private String change(final String pageToChange)
    {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(pageToChange).getAsJsonObject();
        obj.addProperty(META_DESCRIPTION, updatedMetaDescription);
        obj.addProperty(META_KEYWORDS, updatedMetaKeywords);
        obj.addProperty(TMS_ID, tmsId);
        return obj.toString();
    }

    private void assertUpdatedMessage(final String updatedPage)
    {
        with(updatedPage)
                .assertThat(META_DESCRIPTION_PATH, equalTo(updatedMetaDescription), "Updated page should have new meta description")
                .assertThat(META_KEYWORDS_PATH, equalTo(updatedMetaKeywords), "Updated page should have new keywords description");
    }

    private void assertClonedPage(final String cloneAsJson)
    {
        with(cloneAsJson)
                .assertThat(META_DESCRIPTION_PATH, equalTo("meta description"), "Cloned page should have description as original one")
                .assertThat(META_KEYWORDS_PATH, isEmptyOrNullString(), "Cloned page should have meta keywords as original one")
                .assertThat(ID_PATH, not(PAGE_ID), "Cloned page should have another id");
    }
}