package com.smartling.connector.hubspot.sdk.v3.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.HttpMockUtils;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import com.smartling.connector.hubspot.sdk.v3.HubspotPagesV3Client;
import com.smartling.connector.hubspot.sdk.v3.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.v3.page.ListWrapper;
import com.smartling.connector.hubspot.sdk.v3.page.PageDetail;
import com.smartling.connector.hubspot.sdk.v3.page.PageType;
import com.smartling.connector.hubspot.sdk.v3.page.SchedulePublishRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.fest.assertions.api.Assertions.assertThat;

public class HubspotRestPagesV3ClientTest
{
    private static final int PORT = 10000 + new Random().nextInt(9999);

    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final String PAGE_ID = "127";

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TokenProvider tokenProvider;
    private String originalToken;
    private HubspotPagesV3Client hubspotClient;

    @Before
    public void setUpMocks() throws Exception
    {
        this.originalToken = RandomStringUtils.randomAlphanumeric(36);

        final Configuration configuration = Configuration.build(BASE_URL, null, null, null, null);
        final RefreshTokenData refreshTokenData = new RefreshTokenData();
        refreshTokenData.setAccessToken(originalToken);
        tokenProvider = () -> refreshTokenData;
        this.hubspotClient = new HubspotRestPagesV3Client(PageType.SITE_PAGE, configuration, tokenProvider);
    }

    @Test
    public void shouldCallGetPageByIdUrl() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        hubspotClient.getPageById(PAGE_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages/" + PAGE_ID)));
    }

    @Test
    public void shouldCallGetPageDraftByIdUrl() throws HubspotApiException
    {

        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages/" + PAGE_ID + "/draft")).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        hubspotClient.getPageDraftById(PAGE_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages/" + PAGE_ID + "/draft")));

    }

    @Test
    public void shouldCallGetPageDetailByIdUrl() throws HubspotApiException
    {

        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse(pageDetail())));

        PageDetail page = hubspotClient.getPageDetailById(PAGE_ID);

        assertThat(page.getTranslations()).isNull();
        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages/" + PAGE_ID)));

    }

    @Test
    public void shouldTakeTranslations() throws HubspotApiException
    {

        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse(pageDetailWithTranslations())));

        PageDetail page = hubspotClient.getPageDetailById(PAGE_ID);

        assertThat(page.getTranslations()).isNotNull();
        assertThat(page.getTranslations()).isNotEmpty();
        assertThat(page.getTranslations().get("de-de").getId()).isEqualTo("177329249297");
        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages/" + PAGE_ID)));

    }

    @Test
    public void shouldCallGetPageDetailDraftByIdUrl() throws HubspotApiException
    {

        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages/" + PAGE_ID+ "/draft")).willReturn(HttpMockUtils.aJsonResponse(pageDetail())));

        hubspotClient.getPageDetailDraftById(PAGE_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages/" + PAGE_ID+ "/draft")));

    }

    @Test
    public void shouldThrowNativeExceptionForBadResponse() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse("any").withStatus(400)));
        expectedException.expect(HubspotApiException.class);

        hubspotClient.getPageDetailById(PAGE_ID);
    }

    @Test
    public void shouldThrowNativeExceptionForBrokenJson() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse("not JSON")));
        expectedException.expect(HubspotApiException.class);

        hubspotClient.getPageDetailById(PAGE_ID);
    }

    @Test
    public void shouldCallCreateLanguageVariationUrl() throws HubspotApiException
    {
        givenThat(post(HttpMockUtils.path("/cms/v3/pages/site-pages/multi-language/create-language-variation")).willReturn(HttpMockUtils.aJsonResponse(pageDetail())));

        hubspotClient.createLanguageVariation(new CreateLanguageVariationRequest());

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages/multi-language/create-language-variation")));
    }

    @Test
    public void shouldCallUpdatePageUrl() throws HubspotApiException
    {
        givenThat(patch(HttpMockUtils.path("/cms/v3/pages/site-pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        hubspotClient.updatePage(pageSnippet(), PAGE_ID);

        verify(patchRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages/" + PAGE_ID))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo(pageSnippet()))
        );
    }

    @Test
    public void shouldCallUpdatePageDraftUrl() throws HubspotApiException
    {
        givenThat(patch(HttpMockUtils.path("/cms/v3/pages/site-pages/" + PAGE_ID + "/draft")).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        hubspotClient.updatePageDraft(pageSnippet(), PAGE_ID);

        verify(patchRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages/" + PAGE_ID + "/draft"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo(pageSnippet()))
        );
    }

    @Test
    public void shouldCallListPagesUrl() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(5, 15, null, null);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages"))
                .withQueryParam("limit", equalTo("15"))
                .withQueryParam("offset", equalTo("5"))
        );
    }

    @Test
    public void shouldCallListPagesUrlWithRightParams() throws HubspotApiException
    {
        int offset = 5;
        int limit = 15;
        String campaign = "some-hash-id";
        String name = "Page_name";
        String domain = "test_domain";
        Boolean archived = FALSE;
        Boolean isDraft = TRUE;
        Map<String, Object> filter = createSearchFilter(campaign, name, archived, isDraft, domain);
        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(offset, limit, null, filter);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages"))
                .withQueryParam("limit", equalTo(Integer.toString(limit)))
                .withQueryParam("offset", equalTo(Integer.toString(offset)))
                .withQueryParam("campaign", equalTo(campaign))
                .withQueryParam("name__icontains", equalTo(name))
                .withQueryParam("archived", equalTo(archived.toString()))
                .withQueryParam("isDraft", equalTo("true"))
                .withQueryParam("domain", equalTo("test_domain"))
        );
    }

    @Test
    public void shouldCallRefreshTokenFirst() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(0, 10, null, null);
    }

    @Test
    public void shouldNotUseSameTokenForMultipleCalls() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(0, 10, null, null);
        hubspotClient.listPages(0, 10, null, null);
    }

    @Test
    public void shouldDeserializeFields() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        ListWrapper<PageDetail> pageDetails = hubspotClient.listPages(5, 15, null, null);

        assertThat(pageDetails).isNotNull();
        assertThat(pageDetails.getTotalCount()).isEqualTo(6);

        List<PageDetail> detailList = pageDetails.getDetailList();
        assertThat(detailList).isNotEmpty();

        assertPageDetail(detailList.get(0));
    }

    @Test
    public void shouldCallScheduleUrl() throws HubspotApiException
    {
        givenThat(post(HttpMockUtils.path("/cms/v3/pages/site-pages/schedule")).willReturn(aResponse().withStatus(204)));

        hubspotClient.publish(new SchedulePublishRequest());

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages/schedule")));
    }

    @Test
    public void shouldCallDeletePageUrl() throws HubspotApiException
    {
        givenThat(delete(HttpMockUtils.path("/cms/v3/pages/site-pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse(deleteInfo())));

        hubspotClient.delete(PAGE_ID);

        verify(deleteRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages/" + PAGE_ID)));
    }

    private Map<String, Object> createSearchFilter(String campaign, String name, Boolean archived, Boolean isDraft, String domain) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("campaign", campaign);
        filter.put("name__icontains", name);
        filter.put("archived", archived);
        filter.put("isDraft", isDraft);
        filter.put("domain", domain);
        return filter;
    }

    private String pageSnippet()
    {
        // language=JSON
        return "{"
                + "  \"flexAreas\": {},\n"
                + "  \"pageExpiryRedirectUrl\": \"Some symbols % (\",\n"
                + "  \"authorUserId\": 1027715,\n"
                + "  \"performableGuid\": \"\",\n"
                + "  \"includeDefaultCustomCss\": false,\n"
                + "  \"id\": \"127\""
                + "}";
    }

    private String pageDetail()
    {
        // language=JSON
        return "{\n"
                + "  \"id\": \"127\",\n"
                + "  \"htmlTitle\": \"Page 1 title\",\n"
                + "  \"name\": \"page1\",\n"
                + "  \"updatedAt\": \"2024-04-15T16:10:54.590Z\",\n"
                + "  \"archivedAt\": \"1970-01-01T00:00:00Z\"\n"
                + "}";
    }

    private String pageDetailWithTranslations()
    {
        // language=JSON
        return "{\n"
                + "  \"id\": \"127\",\n"
                + "  \"htmlTitle\": \"Page 1 title\",\n"
                + "  \"name\": \"page1\",\n"
                + "  \"updatedAt\": \"2024-04-15T16:10:54.590Z\",\n"
                + "  \"archivedAt\": \"1970-01-01T00:00:00Z\",\n" +
                "  \"translations\": {\n" +
                "    \"de-de\": {\n" +
                "      \"archivedInDashboard\": false,\n" +
                "      \"createdAt\": \"2024-09-02T10:51:15.656Z\",\n" +
                "      \"id\": 177329249297,\n" +
                "      \"name\": \"Creator Terms - DE-DE\",\n" +
                "      \"publicAccessRules\": [],\n" +
                "      \"publicAccessRulesEnabled\": false,\n" +
                "      \"publishDate\": \"2024-09-02T11:21:10Z\",\n" +
                "      \"slug\": \"de-de/policies/creator-terms\",\n" +
                "      \"state\": \"PUBLISHED_OR_SCHEDULED\",\n" +
                "      \"updatedAt\": \"2024-10-10T02:37:11.097Z\"\n" +
                "    },\n" +
                "    \"es-es\": {\n" +
                "      \"archivedInDashboard\": false,\n" +
                "      \"createdAt\": \"2024-09-03T05:10:04.704Z\",\n" +
                "      \"id\": 177388492985,\n" +
                "      \"name\": \"Creator Terms - ES-ES\",\n" +
                "      \"publicAccessRules\": [],\n" +
                "      \"publicAccessRulesEnabled\": false,\n" +
                "      \"publishDate\": \"2024-09-03T05:23:16Z\",\n" +
                "      \"slug\": \"es-es/policies/creator-terms\",\n" +
                "      \"state\": \"PUBLISHED_OR_SCHEDULED\",\n" +
                "      \"updatedAt\": \"2024-10-08T22:38:19.062Z\"\n" +
                "    },\n" +
                "    \"es-mx\": {\n" +
                "      \"archivedInDashboard\": false,\n" +
                "      \"createdAt\": \"2024-09-03T11:13:17.294Z\",\n" +
                "      \"id\": 177409465752,\n" +
                "      \"name\": \"Creator Terms - ES-MX\",\n" +
                "      \"publicAccessRules\": [],\n" +
                "      \"publicAccessRulesEnabled\": false,\n" +
                "      \"publishDate\": \"2024-09-03T11:31:01Z\",\n" +
                "      \"slug\": \"es-mx/policies/creator-terms\",\n" +
                "      \"state\": \"PUBLISHED_OR_SCHEDULED\",\n" +
                "      \"updatedAt\": \"2024-09-27T20:11:41.480Z\"\n" +
                "    },\n" +
                "    \"fr-fr\": {\n" +
                "      \"archivedInDashboard\": false,\n" +
                "      \"createdAt\": \"2024-09-04T06:40:33.940Z\",\n" +
                "      \"id\": 177491585358,\n" +
                "      \"name\": \"Creator Terms - FR-FR\",\n" +
                "      \"publicAccessRules\": [],\n" +
                "      \"publicAccessRulesEnabled\": false,\n" +
                "      \"publishDate\": \"2024-09-04T06:54:34Z\",\n" +
                "      \"slug\": \"fr-fr/policies/creator-terms\",\n" +
                "      \"state\": \"PUBLISHED_OR_SCHEDULED\",\n" +
                "      \"updatedAt\": \"2024-10-08T22:37:22.640Z\"\n" +
                "    },\n" +
                "    \"it-it\": {\n" +
                "      \"archivedInDashboard\": false,\n" +
                "      \"createdAt\": \"2024-09-03T10:41:56.614Z\",\n" +
                "      \"id\": 177409196932,\n" +
                "      \"name\": \"Creator Terms - IT-IT\",\n" +
                "      \"publicAccessRules\": [],\n" +
                "      \"publicAccessRulesEnabled\": false,\n" +
                "      \"publishDate\": \"2024-09-03T11:31:17Z\",\n" +
                "      \"slug\": \"it-it/policies/creator-terms\",\n" +
                "      \"state\": \"PUBLISHED_OR_SCHEDULED\",\n" +
                "      \"updatedAt\": \"2024-10-10T21:10:23.691Z\"\n" +
                "    },\n" +
                "    \"nl-nl\": {\n" +
                "      \"archivedInDashboard\": false,\n" +
                "      \"createdAt\": \"2024-09-05T11:01:49.931Z\",\n" +
                "      \"id\": 177596753746,\n" +
                "      \"name\": \"Creator Terms - NL-NL\",\n" +
                "      \"publicAccessRules\": [],\n" +
                "      \"publicAccessRulesEnabled\": false,\n" +
                "      \"publishDate\": \"2024-09-05T11:02:46Z\",\n" +
                "      \"slug\": \"nl-nl/policies/creator-terms\",\n" +
                "      \"state\": \"PUBLISHED_OR_SCHEDULED\",\n" +
                "      \"updatedAt\": \"2024-09-27T20:10:27.076Z\"\n" +
                "    },\n" +
                "    \"pt-br\": {\n" +
                "      \"archivedInDashboard\": false,\n" +
                "      \"createdAt\": \"2024-09-05T11:45:11.280Z\",\n" +
                "      \"id\": 177597299725,\n" +
                "      \"name\": \"Creator Terms - PT-BR\",\n" +
                "      \"publicAccessRules\": [],\n" +
                "      \"publicAccessRulesEnabled\": false,\n" +
                "      \"publishDate\": \"2024-09-05T11:45:58Z\",\n" +
                "      \"slug\": \"pt-br/policies/creator-terms\",\n" +
                "      \"state\": \"PUBLISHED_OR_SCHEDULED\",\n" +
                "      \"updatedAt\": \"2024-10-09T23:48:26.907Z\"\n" +
                "    },\n" +
                "    \"pt-pt\": {\n" +
                "      \"archivedInDashboard\": false,\n" +
                "      \"createdAt\": \"2024-09-04T07:23:54.405Z\",\n" +
                "      \"id\": 177492719495,\n" +
                "      \"name\": \"Creator Terms - PT-PT\",\n" +
                "      \"publicAccessRules\": [],\n" +
                "      \"publicAccessRulesEnabled\": false,\n" +
                "      \"publishDate\": \"2024-09-04T07:24:50Z\",\n" +
                "      \"slug\": \"pt-pt/policies/creator-terms\",\n" +
                "      \"state\": \"PUBLISHED_OR_SCHEDULED\",\n" +
                "      \"updatedAt\": \"2024-10-10T00:20:08.345Z\"\n" +
                "    },\n" +
                "    \"sv-se\": {\n" +
                "      \"archivedInDashboard\": false,\n" +
                "      \"createdAt\": \"2024-09-04T10:35:19.985Z\",\n" +
                "      \"id\": 177503286110,\n" +
                "      \"name\": \"Creator Terms - SV-SE\",\n" +
                "      \"publicAccessRules\": [],\n" +
                "      \"publicAccessRulesEnabled\": false,\n" +
                "      \"publishDate\": \"2024-09-04T10:58:45Z\",\n" +
                "      \"slug\": \"sv-se/policies/creator-terms\",\n" +
                "      \"state\": \"PUBLISHED_OR_SCHEDULED\",\n" +
                "      \"updatedAt\": \"2024-09-27T20:12:04.675Z\"\n" +
                "    },\n" +
                "    \"tl\": {\n" +
                "      \"archivedInDashboard\": false,\n" +
                "      \"createdAt\": \"2024-10-09T09:47:35.027Z\",\n" +
                "      \"id\": 180583641348,\n" +
                "      \"name\": \"Creator Terms - FIL-PH\",\n" +
                "      \"publicAccessRules\": [],\n" +
                "      \"publicAccessRulesEnabled\": false,\n" +
                "      \"publishDate\": \"1970-01-01T00:00:00Z\",\n" +
                "      \"slug\": \"tl/policies/creator-terms\",\n" +
                "      \"state\": \"DRAFT\",\n" +
                "      \"updatedAt\": \"2024-10-23T22:50:54.223Z\"\n" +
                "    }\n" +
                "  }\n"
                + "}";
    }

    private String pageDetails()
    {
        // language=JSON
        return "{\n"
                + "  \"total\": 6,\n"
                + "  \"results\": [\n"
                + "    {\n"
                + "      \"id\": \"127\",\n"
                + "      \"htmlTitle\": \"Page 1 title\",\n"
                + "      \"name\": \"page1\",\n"
                + "      \"updatedAt\": \"2024-04-15T16:10:54.590Z\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"id\": \"129\",\n"
                + "      \"htmlTitle\": \"Page 2 title\",\n"
                + "      \"name\": \"page2\",\n"
                + "      \"updatedAt\": \"2024-04-15T16:10:54.590Z\"\n"
                + "    }\n"
                + "  ]\n"
                + "}";
    }

    private String deleteInfo()
    {
        // language=JSON
        return "{\n"
                + "  \"succeeded\": true,\n"
                + "  \"message\": \"Action succeeded\"\n"
                + "}";
    }

    private void assertPageDetail(PageDetail pageDetail)
    {
        assertThat(pageDetail.getHtmlTitle()).isEqualTo("Page 1 title");
        assertThat(pageDetail.getName()).isEqualTo("page1");
        assertThat(pageDetail.getId()).isEqualTo("127");
        assertThat(pageDetail.getUpdatedAt()).hasTime(1713197454590L);
    }
}
