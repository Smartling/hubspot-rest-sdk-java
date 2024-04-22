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

        hubspotClient.getPageDetailById(PAGE_ID);

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
        Boolean archived = FALSE;
        Boolean isDraft = TRUE;
        Map<String, Object> filter = createSearchFilter(campaign, name, archived, isDraft);
        givenThat(get(HttpMockUtils.path("/cms/v3/pages/site-pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(offset, limit, null, filter);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/pages/site-pages"))
                .withQueryParam("limit", equalTo(Integer.toString(limit)))
                .withQueryParam("offset", equalTo(Integer.toString(offset)))
                .withQueryParam("campaign", equalTo(campaign))
                .withQueryParam("name__icontains", equalTo(name))
                .withQueryParam("archived", equalTo(archived.toString()))
                .withQueryParam("isDraft", equalTo("true"))
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

    private Map<String, Object> createSearchFilter(String campaign, String name, Boolean archived, Boolean isDraft) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("campaign", campaign);
        filter.put("name__icontains", name);
        filter.put("archived", archived);
        filter.put("isDraft", isDraft);
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
