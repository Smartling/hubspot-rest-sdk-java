package com.smartling.connector.hubspot.sdk.it;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartling.connector.hubspot.sdk.DeletePageInfo;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.PageDetail;
import com.smartling.connector.hubspot.sdk.PageDetails;
import com.smartling.connector.hubspot.sdk.PageSearchFilter;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClient;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClient.Configuration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.jayway.jsonassert.JsonAssert.with;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class PagesIntegrationTest
{
    private static final long   BASIC_PAGE_ID          = 2976789349L;
    private static final long   ARCHIVED_PAGE_ID       = 3141172570L;
    private static final long   NOT_LIVE_PAGE_ID       = 3141182000L;
    private static final String BASIC_PAGE_NAME        = "Base Page for intergation tests";
    private static final String NOT_LIVE_PAGE_CAMPAIGN = "5dc2fe94-d221-49ee-bff8-60303974bb27";
    private static final String META_DESCRIPTION       = "meta_description";
    private static final String META_KEYWORDS          = "meta_keywords";
    private static final String TMS_ID                 = "tms_id";
    private static final String ROOT_PATH              = "$.";
    private static final String META_DESCRIPTION_PATH  = ROOT_PATH + META_DESCRIPTION;
    private static final String META_KEYWORDS_PATH     = ROOT_PATH + META_KEYWORDS;
    private static final String ID_PATH                = "$.id";

    private HubspotClient hubspotClient;
    private LocalDateTime now                    = LocalDateTime.now();
    private String        updatedMetaDescription = "Meta description, created at " + now;
    private String        updatedMetaKeywords    = "Meta keywords, created at " + now;
    private String        tmsId                  = "Tms Id ";
    private List<Long>    pagesToDelete          = Lists.newArrayList();

    @Before
    public void checkRequiredProperties()
    {
        final String refreshToken = System.getProperty("hubspot.refreshToken");
        final String clientId = System.getProperty("hubspot.clientId");

        assertThat(refreshToken).overridingErrorMessage("Access token for Hubspot API is missing!").isNotEmpty();
        assertThat(clientId).overridingErrorMessage("Client id for Hubspot application is missing!").isNotEmpty();

        hubspotClient = new HubspotRestClient(Configuration.build(clientId, refreshToken));
    }

    @After
    public void deleteTestPages()
    {
        for (Long pageId : pagesToDelete)
        {
            try
            {
                hubspotClient.delete(pageId);
            }
            catch (HubspotApiException e)
            {
                System.err.printf("Fail to clean up page '%1$d', cause '%2$s'", pageId, e);
            }
        }
    }

    @Test
    public void shouldReturnPage() throws Exception
    {
        String pageAsJson = hubspotClient.getPageById(BASIC_PAGE_ID);
        with(pageAsJson)
                .assertThat(META_DESCRIPTION_PATH, equalTo("meta description"), "Meta description should have particular text")
                        //don't know where to set meta keywords
                .assertThat(META_KEYWORDS_PATH, isEmptyOrNullString(), "Meta keywords should not be filled")
                .assertThat("$.id", equalTo(BASIC_PAGE_ID), "Page id should have particular value");
    }

    @Test
    public void shouldReturnPageDetail() throws Exception
    {
        PageDetail pageDetailById = hubspotClient.getPageDetailById(BASIC_PAGE_ID);

        assertThat(pageDetailById.getId()).isEqualTo(BASIC_PAGE_ID);
        assertPageDetail(pageDetailById);
    }

    private void assertPageDetail(final PageDetail pageDetail)
    {

        assertThat(pageDetail.getHtmlTitle()).isEqualTo("Page title for translation");
        assertThat(pageDetail.getName()).isEqualTo(BASIC_PAGE_NAME);
    }

    @Test
    public void shouldListPages() throws Exception
    {
        PageDetails pageDetails = hubspotClient.listPages(0, 1);
        assertThat(pageDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(pageDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();

        List<PageDetail> detailList = pageDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").hasSize(1);

        assertPageDetailIsNotEmpty(detailList.get(0));
    }
    
    @Test
    public void shouldListPagesFilterByName() throws Exception
    {
        PageDetails pageDetails = hubspotClient.listPages(0, 1, createSearchFilter(null, BASIC_PAGE_NAME, null, null));
        
        assertThat(pageDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(pageDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();

        List<PageDetail> detailList = pageDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").hasSize(1);

        assertPageDetailIsNotEmpty(detailList.get(0));
        assertThat(detailList.get(0).getName()).isEqualTo(BASIC_PAGE_NAME);
    }
    
    @Test
    public void shouldListPagesFilterByArchived() throws Exception
    {
        PageDetails pageDetails = hubspotClient.listPages(0, 1000, createSearchFilter(null, null, true, null));
        
        assertPageDetailsNotEmpty(pageDetails);
        assertHasPageWithId(ARCHIVED_PAGE_ID, pageDetails);
    }
    
    @Test
    public void shouldListPagesFilterByNotLive() throws Exception
    {
        PageDetails pageDetails = hubspotClient.listPages(0, 1000, createSearchFilter(null, null, null, true));
        
        assertPageDetailsNotEmpty(pageDetails);
        assertHasPageWithId(NOT_LIVE_PAGE_ID, pageDetails);
    }
    
    @Test
    public void shouldListPagesFilterByLive() throws Exception
    {
        PageDetails pageDetails = hubspotClient.listPages(0, 1000, createSearchFilter(null, null, null, false));
        
        assertPageDetailsNotEmpty(pageDetails);
        assertHasPageWithId(BASIC_PAGE_ID, pageDetails);
    }
    
    @Test
    public void shouldListPagesFilterByCampaign() throws Exception
    {
        PageDetails pageDetails = hubspotClient.listPages(0, 1000, createSearchFilter(NOT_LIVE_PAGE_CAMPAIGN, null, false, true));
        
        assertPageDetailsNotEmpty(pageDetails);
        assertHasPageWithId(NOT_LIVE_PAGE_ID, pageDetails);
    }
    
    private void assertPageDetailsNotEmpty(PageDetails pageDetails)
    {
        assertThat(pageDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(pageDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();
        List<PageDetail> detailList = pageDetails.getDetailList();        
        assertThat(detailList).overridingErrorMessage("Page details should not be empty").isNotEmpty();
    }

    @Test
    public void shouldListPagesByTmsId() throws Exception
    {
        // create clone with tmsId
        String clonedPage = getCloneAndChangeIt();
        long clonedPageId = getId(clonedPage);
        hubspotClient.updatePage(clonedPage, clonedPageId);

        PageDetails pageDetails = hubspotClient.listPagesByTmsId(tmsId);

        assertThat(pageDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(pageDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();

        List<PageDetail> detailList = pageDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty").isNotEmpty();

        assertPageDetailIsNotEmpty(detailList.get(0));
    }

    @Test(expected = HubspotApiException.class)
    public void shouldThrowExceptionIfAuthorizationFailed() throws HubspotApiException
    {
        HubspotClient hubspotClient = new HubspotRestClient(Configuration.build("wrong-client-id", "wrong-token"));
        hubspotClient.listPages(0, 1);
    }

    private String getCloneAndChangeIt() throws HubspotApiException
    {
        String cloneFromResponse = hubspotClient.clonePage(BASIC_PAGE_ID);
        pagesToDelete.add(getId(cloneFromResponse));
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
        String cloneFromResponse = hubspotClient.clonePage(BASIC_PAGE_ID);
        pagesToDelete.add(getId(cloneFromResponse));

        assertClonedPage(cloneFromResponse);
    }

    @Test
    public void shouldClonePageWithEntityApi() throws Exception
    {
        PageDetail detail = hubspotClient.clonePageAsDetail(BASIC_PAGE_ID);
        pagesToDelete.add(detail.getId());

        assertPageDetail(detail);
    }

    @Test
    public void shouldUpdatePage() throws Exception
    {
        // prepare clone for update
        String changeBeforeUpdate = getCloneAndChangeIt();
        long clonedPageId = getId(changeBeforeUpdate);

        String updatedPage = hubspotClient.updatePage(changeBeforeUpdate, clonedPageId);

        assertUpdatedMessage(updatedPage);
    }

    @Test
    public void shouldDeletePage() throws Exception
    {
        // prepare clone for update
        String changeBeforeUpdate = getCloneAndChangeIt();
        long id = getId(changeBeforeUpdate);

        DeletePageInfo deletePageInfo = hubspotClient.delete(id);

        assertThat(deletePageInfo.isSucceeded()).isTrue();
    }

    private long getId(final String pageAsJson)
    {
        assertThat(pageAsJson).isNotNull().isNotEmpty();

        JsonParser parser = new JsonParser();
        JsonElement pageAsJsonElement = parser.parse(pageAsJson);
        assertThat(pageAsJsonElement.isJsonObject()).isTrue();

        JsonObject pageAsJsonObject = pageAsJsonElement.getAsJsonObject();
        assertThat(pageAsJsonObject.has("id")).isTrue();

        JsonElement idJsonElement = pageAsJsonObject.get("id");
        assertThat(idJsonElement.isJsonPrimitive()).isTrue();
        assertThat(idJsonElement.getAsJsonPrimitive().isNumber()).isTrue();

        return idJsonElement.getAsLong();
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
                .assertThat(ID_PATH, not(BASIC_PAGE_ID), "Cloned page should have another id");
    }
    
    private void assertHasPageWithId(long id, PageDetails pageDetails) {
        Optional<PageDetail> result = pageDetails.getDetailList().stream().filter(page -> id == page.getId()).findFirst();
        if (!result.isPresent()) {
            throw new AssertionError(String.format("Page [ID='%s'] not found in:\n%s", id, pageDetails.getDetailList()));
        }
    }
    
    private PageSearchFilter createSearchFilter(String campaign, String name, Boolean archived, Boolean draft) {
        PageSearchFilter filter = new PageSearchFilter();
        filter.setCampaign(campaign);
        filter.setName(name);
        filter.setArchived(archived);
        filter.setDraft(draft);   
        return filter;
    }
}