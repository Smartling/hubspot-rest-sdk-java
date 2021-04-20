package com.smartling.connector.hubspot.sdk.it;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotPagesClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.common.Language;
import com.smartling.connector.hubspot.sdk.page.PageDetail;
import com.smartling.connector.hubspot.sdk.common.CurrentState;
import com.smartling.connector.hubspot.sdk.common.PublishAction;
import com.smartling.connector.hubspot.sdk.common.PublishActionRequest;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager;
import org.fest.assertions.core.Condition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static com.jayway.jsonassert.JsonAssert.with;
import static com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager.createTokenProvider;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class PagesIntegrationTest extends BaseIntegrationTest
{
    private static final String BASIC_PAGE_NAME        = "test";
    private static final String META_DESCRIPTION       = "meta_description";
    private static final String META_KEYWORDS          = "meta.keywords";
    private static final String ROOT_PATH              = "$.";
    private static final String META_DESCRIPTION_PATH  = ROOT_PATH + META_DESCRIPTION;
    private static final String META_KEYWORDS_PATH     = ROOT_PATH + META_KEYWORDS;
    private static final String ID_PATH                = "$.id";

    private HubspotPagesClient hubspotClient;
    private LocalDateTime now                    = LocalDateTime.now();
    private String        updatedMetaDescription = "Meta description, created at " + now;
    private String        updatedMetaKeywords    = "Meta keywords, created at " + now + ". Added some metacharacters: % (it is percent).";
    private List<Long>    pagesToDelete          = new ArrayList<>();

    private long basicPageId;
    private long archivedPageId;
    private long notLivePageId;
    private String notLivePageCampaignId;

    @Before
    public void init()
    {
        final Configuration configuration = Configuration.build(clientId, clientSecret, redirectUri, refreshToken);
        hubspotClient = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getPagesClient();
        notLivePageCampaignId = System.getProperty("hubspot.notLivePageCampaignId");
        basicPageId = Long.parseLong(System.getProperty("hubspot.basicPageId"));
        archivedPageId = Long.parseLong(System.getProperty("hubspot.archivedPageId"));
        notLivePageId = Long.parseLong(System.getProperty("hubspot.notLivePageId"));
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
                e.printStackTrace();
            }
        }
    }

    @Test
    public void shouldReturnPage() throws Exception
    {
        String pageAsJson = hubspotClient.getPageById(basicPageId);
        with(pageAsJson)
                .assertThat(META_DESCRIPTION_PATH, equalTo("meta description"), "Meta description should have particular text")
                        //don't know where to set meta keywords
                .assertThat(META_KEYWORDS_PATH, empty(), "Meta keywords should not be filled")
                .assertThat("$.id", equalTo(basicPageId), "Page id should have particular value");
    }

    @Test
    public void shouldReturnPageBuffer() throws Exception
    {
        String pageAsJson = hubspotClient.getPageBufferById(basicPageId);
        with(pageAsJson)
                .assertThat(META_DESCRIPTION_PATH, equalTo("meta description"), "Meta description should have particular text")
                //don't know where to set meta keywords
                .assertThat(META_KEYWORDS_PATH, empty(), "Meta keywords should not be filled")
                .assertThat("$.id", equalTo(basicPageId), "Page id should have particular value");
    }

    @Test
    public void shouldReturnPageDetail() throws Exception
    {
        PageDetail pageDetailById = hubspotClient.getPageDetailById(basicPageId);

        assertThat(pageDetailById.getId()).isEqualTo(basicPageId);
        assertPageDetail(pageDetailById);
    }

    @Test
    public void shouldReturnPageDetailBuffer() throws Exception
    {
        PageDetail pageDetailById = hubspotClient.getPageDetailBufferById(basicPageId);

        assertThat(pageDetailById.getId()).isEqualTo(basicPageId);
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
        ListWrapper<PageDetail> pageDetails = hubspotClient.listPages(0, 1, null, null);
        assertThat(pageDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(pageDetails.getTotalCount()).overridingErrorMessage("Total count should be positive").isPositive();

        List<PageDetail> detailList = pageDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").hasSize(1);

        assertPageDetailIsNotEmpty(detailList.get(0));
    }
    
    @Test
    public void shouldListPagesFilterByName() throws Exception
    {
        ListWrapper<PageDetail> pageDetails = hubspotClient.listPages(0, 1, null, createSearchFilter(null, BASIC_PAGE_NAME, null, null));
        
        assertThat(pageDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(pageDetails.getTotalCount()).overridingErrorMessage("Total count should be positive").isPositive();

        List<PageDetail> detailList = pageDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").hasSize(1);

        assertPageDetailIsNotEmpty(detailList.get(0));
        assertThat(detailList.get(0).getName()).isEqualTo(BASIC_PAGE_NAME);
    }
    
    @Test
    public void shouldListPagesFilterByArchived() throws Exception
    {
        ListWrapper<PageDetail> pageDetails = hubspotClient.listPages(0, 1000, null, createSearchFilter(null, null, true, null));
        
        assertPageDetailsNotEmpty(pageDetails);
        assertHasPageWithId(archivedPageId, pageDetails);
        assertThat(pageDetails.getDetailList()).are(suchThat("Not an archived", item -> item.isArchived()));
    }
    
    @Test
    public void shouldListPagesFilterByNotLive() throws Exception
    {
        ListWrapper<PageDetail> pageDetails = hubspotClient.listPages(0, 1000, null, createSearchFilter(null, null, null, true));
        
        assertPageDetailsNotEmpty(pageDetails);
        assertHasPageWithId(notLivePageId, pageDetails);
        assertThat(pageDetails.getDetailList()).are(suchThat("Not a draft", item -> item.getCurrentState() == CurrentState.DRAFT));
    }
    
    @Test
    public void shouldListPagesFilterByLive() throws Exception
    {
        ListWrapper<PageDetail> pageDetails = hubspotClient.listPages(0, 1000, null, createSearchFilter(null, null, null, false));
        
        assertPageDetailsNotEmpty(pageDetails);
        assertHasPageWithId(basicPageId, pageDetails);
        assertThat(pageDetails.getDetailList()).are(suchThat("Not a live", item -> item.getCurrentState() != CurrentState.DRAFT));
    }
    
    @Test
    public void shouldListPagesFilterByCampaign() throws Exception
    {
        ListWrapper<PageDetail> pageDetails = hubspotClient.listPages(0, 1000, null, createSearchFilter(notLivePageCampaignId, null, false, true));
        
        assertPageDetailsNotEmpty(pageDetails);
        assertHasPageWithId(notLivePageId, pageDetails);
    }
    
    private void assertPageDetailsNotEmpty(ListWrapper<PageDetail> pageDetails)
    {
        assertThat(pageDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(pageDetails.getTotalCount()).overridingErrorMessage("Total count should be positive"+ pageDetails.getTotalCount()).isPositive();
        List<PageDetail> detailList = pageDetails.getDetailList();        
        assertThat(detailList).overridingErrorMessage("Page details should not be empty").isNotEmpty();
    }

    @Test(expected = HubspotApiException.class)
    public void shouldThrowExceptionIfAuthorizationFailed() throws HubspotApiException
    {
        final Configuration configuration = Configuration.build("wrong-client-id", "wrong-client-secret", "wrong-redirect-uri", "wrong-token");
        HubspotPagesClient hubspotClient = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getPagesClient();
        hubspotClient.listPages(0, 1, null, null);
    }

    private String getCloneAndChangeIt() throws HubspotApiException
    {
        String cloneFromResponse = hubspotClient.clonePage(basicPageId);
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
        String cloneFromResponse = hubspotClient.clonePage(basicPageId);
        pagesToDelete.add(getId(cloneFromResponse));

        assertClonedPage(cloneFromResponse);
    }

    @Test
    public void shouldClonePageWithEntityApi() throws Exception
    {
        PageDetail detail = hubspotClient.clonePageAsDetail(basicPageId);
        pagesToDelete.add(detail.getId());

        assertPageDetail(detail);
    }

    @Test
    public void shouldCreateLanguageVariation() throws Exception
    {
        PageDetail detail = hubspotClient.createLanguageVariation(basicPageId, createLanguageVariation());
        pagesToDelete.add(detail.getId());

        assertPageDetail(detail);
    }

    @Test
    public void shouldPublish() throws Exception
    {
        // prepare clone for update
        String changeBeforeUpdate = getCloneAndChangeIt();
        long clonedPageId = getId(changeBeforeUpdate);

        hubspotClient.publish(clonedPageId, publishAction());
        pagesToDelete.add(clonedPageId);

        PageDetail detail = hubspotClient.getPageDetailById(clonedPageId);
        assertThat(detail.getCurrentState()).isEqualTo(CurrentState.PUBLISHED);
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
    public void shouldUpdatePageBuffer() throws Exception
    {
        // prepare clone for update
        String changeBeforeUpdate = getCloneAndChangeIt();
        long clonedPageId = getId(changeBeforeUpdate);

        String updatedPage = hubspotClient.updatePageBuffer(changeBeforeUpdate, clonedPageId);

        assertUpdatedMessage(updatedPage);
    }

    @Test
    public void shouldDeletePage() throws Exception
    {
        // prepare clone for update
        String changeBeforeUpdate = getCloneAndChangeIt();
        long id = getId(changeBeforeUpdate);

        ResultInfo deletePageInfo = hubspotClient.delete(id);

        // Actually endpoint returns 204, can't check it here
        //assertThat(deletePageInfo.isSucceeded()).isTrue();
    }

    @Test
    public void shouldGetSupportedLanguages() throws Exception
    {
        ListWrapper<Language> languages = hubspotClient.getSupportedLanguages();

        assertThat(languages.getDetailList()).isNotEmpty();
    }

    private CreateLanguageVariationRequest createLanguageVariation()
    {
        CreateLanguageVariationRequest request = new CreateLanguageVariationRequest();
        request.setName("language variation (fr-fr)");
        request.setLanguage("fr-fr");
        request.setMasterLanguage("en-us");
        return request;
    }

    private PublishActionRequest publishAction()
    {
        PublishActionRequest request = new PublishActionRequest();
        request.setAction(PublishAction.SCHEDULE_PUBLISH);
        return request;
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
        // Doesn't work now
        // TODO check if it is necessary
        //obj.addProperty(META_KEYWORDS, updatedMetaKeywords);
        return obj.toString();
    }

    private void assertUpdatedMessage(final String updatedPage)
    {
        with(updatedPage)
                .assertThat(META_DESCRIPTION_PATH, equalTo(updatedMetaDescription), "Updated page should have new meta description");
                // Doesn't work now
                // TODO check if it is necessary
                //obj.addProperty(META_KEYWORDS, updatedMetaKeywords);
                //.assertThat(META_KEYWORDS_PATH, equalTo(updatedMetaKeywords), "Updated page should have new keywords description");
    }

    private void assertClonedPage(final String cloneAsJson)
    {
        with(cloneAsJson)
                .assertThat(META_DESCRIPTION_PATH, equalTo("meta description"), "Cloned page should have description as original one")
                .assertThat(META_KEYWORDS_PATH, empty(), "Cloned page should have meta keywords as original one")
                .assertThat(ID_PATH, not(basicPageId), "Cloned page should have another id");
    }
    
    private void assertHasPageWithId(long id, ListWrapper<PageDetail> pageDetails) {
        Optional<PageDetail> result = pageDetails.getDetailList().stream().filter(page -> id == page.getId()).findFirst();
        if (!result.isPresent()) {
            throw new AssertionError(String.format("Page [ID='%s'] not found in:\n%s", id, pageDetails.getDetailList()));
        }
    }
    
    private Map<String, Object> createSearchFilter(String campaign, String name, Boolean archived, Boolean isDraft) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("campaign", campaign);
        filter.put("name__icontains", name);
        filter.put("archived", archived);
        filter.put("is_draft", isDraft);
        return filter;
    }
    
    private static <T> Condition<T> suchThat(String message, Predicate<T> criteria) {
        return new Condition<T>(message) {
            @Override
            public boolean matches(final T value)
            {
                return criteria.test(value);
            }
        };
    }
}