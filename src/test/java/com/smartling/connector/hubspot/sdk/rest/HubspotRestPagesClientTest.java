package com.smartling.connector.hubspot.sdk.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotPagesClient;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.page.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.common.Language;
import com.smartling.connector.hubspot.sdk.page.PageDetail;
import com.smartling.connector.hubspot.sdk.common.PublishActionRequest;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
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
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.fest.assertions.api.Assertions.assertThat;

public class HubspotRestPagesClientTest
{
    private static final int PORT = 10000 + new Random().nextInt(9999);

    private static final String BASE_URL      = "http://localhost:" + PORT;
    private static final long   PAGE_ID       = 127L;

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TokenProvider tokenProvider;
    private String originalToken;
    private HubspotPagesClient hubspotClient;

    @Before
    public void setUpMocks() throws Exception
    {
        this.originalToken = RandomStringUtils.randomAlphanumeric(36);

        final Configuration configuration = Configuration.build(BASE_URL, null, null, null, null);
        final RefreshTokenData refreshTokenData = new RefreshTokenData();
        refreshTokenData.setAccessToken(originalToken);
        tokenProvider = () -> refreshTokenData;
        this.hubspotClient = new HubspotRestPagesClient(configuration, tokenProvider);
    }

    @Test
    public void shouldCallGetPageUrl() throws HubspotApiException
    {

        givenThat(get(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        hubspotClient.getPageById(PAGE_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID)));

    }

    @Test
    public void shouldCallGetPageBufferUrl() throws HubspotApiException
    {

        givenThat(get(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID + "/buffer")).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        hubspotClient.getPageBufferById(PAGE_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID + "/buffer")));

    }

    @Test
    public void shouldCallGetPageUrlForPageDetail() throws HubspotApiException
    {

        givenThat(get(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse(pageDetail())));

        hubspotClient.getPageDetailById(PAGE_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID)));

    }

    @Test
    public void shouldCallGetPageUrlForPageDetailBuffer() throws HubspotApiException
    {

        givenThat(get(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID+ "/buffer")).willReturn(HttpMockUtils.aJsonResponse(pageDetail())));

        hubspotClient.getPageDetailBufferById(PAGE_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID+ "/buffer")));

    }

    @Test
    public void shouldCallDeletePageUrl() throws HubspotApiException
    {

        givenThat(delete(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse(deleteInfo())));

        hubspotClient.delete(PAGE_ID);

        verify(deleteRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID)));

    }

    @Test
    public void shouldCallClonePageUrl() throws HubspotApiException
    {
        givenThat(post(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID + "/clone")).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        hubspotClient.clonePage(PAGE_ID);

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID + "/clone")));
    }

    @Test
    public void shouldThrowNativeExceptionForBadResponse() throws HubspotApiException
    {
        givenThat(post(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID + "/clone")).willReturn(HttpMockUtils.aJsonResponse("any").withStatus(400)));
        expectedException.expect(HubspotApiException.class);

        hubspotClient.clonePageAsDetail(PAGE_ID);
    }

    @Test
    public void shouldThrowNativeExceptionForBrokenJson() throws HubspotApiException
    {
        givenThat(post(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID + "/clone")).willReturn(HttpMockUtils.aJsonResponse("not JSON")));
        expectedException.expect(HubspotApiException.class);

        hubspotClient.clonePageAsDetail(PAGE_ID);
    }

    @Test
    public void shouldCallClonePageUrlForEntityApi() throws HubspotApiException
    {
        givenThat(post(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID + "/clone")).willReturn(HttpMockUtils.aJsonResponse(pageDetail())));

        hubspotClient.clonePageAsDetail(PAGE_ID);

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID + "/clone")));
    }

    @Test
    public void shouldCallCreateLanguageVariationUrlForEntityApi() throws HubspotApiException
    {
        givenThat(post(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID + "/create-language-variation")).willReturn(HttpMockUtils.aJsonResponse(pageDetail())));

        hubspotClient.createLanguageVariation(PAGE_ID, new CreateLanguageVariationRequest());

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID + "/create-language-variation")));
    }

    @Test
    public void shouldCallUpdatePageUrl() throws HubspotApiException
    {
        givenThat(put(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        hubspotClient.updatePage(pageSnippet(), PAGE_ID);

        verify(putRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalTo(pageSnippet()))
        );
    }

    @Test
    public void shouldCallUpdatePageBufferUrl() throws HubspotApiException
    {
        givenThat(put(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID + "/buffer")).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        hubspotClient.updatePageBuffer(pageSnippet(), PAGE_ID);

        verify(putRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID + "/buffer"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo(pageSnippet()))
        );
    }

    @Test
    public void shouldCallListPagesUrl() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(5, 15, null, null);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages"))
                        .withQueryParam("limit", equalTo("15"))
                        .withQueryParam("offset", equalTo("5"))
        );
    }

    @Test
    public void shouldCallListPagesUrlWithRightParams() throws HubspotApiException
    {
        final int offset = 5;
        final int limit = 15;
        final String campaign = "some-hash-id";
        final String name = "Page_name";
        final Boolean archived = FALSE;
        final Boolean isDraft = TRUE;
        Map<String, Object> filter = createSearchFilter(campaign, name, archived, isDraft);
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(offset, limit, null, filter);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages"))
                        .withQueryParam("limit", equalTo(Integer.toString(limit)))
                        .withQueryParam("offset", equalTo(Integer.toString(offset)))
                        .withQueryParam("campaign", equalTo(campaign))
                        .withQueryParam("name__icontains", equalTo(name))
                        .withQueryParam("archived", equalTo(archived.toString()))
                        .withQueryParam("is_draft", equalTo("true"))
        );
    }

    @Test
    public void shouldCallGetSupportedLanguagesUrl() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages/supported-languages")).willReturn(HttpMockUtils.aJsonResponse(supportedLanguages())));

        ListWrapper<Language> languages = hubspotClient.getSupportedLanguages();

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/supported-languages")));

        assertThat(languages.getDetailList()).hasSize(2);
        assertThat(languages.getDetailList().get(0).getTag()).isEqualTo("af");
        assertThat(languages.getDetailList().get(0).getDisplayName()).isEqualTo("Afrikaans");
        assertThat(languages.getDetailList().get(1).getTag()).isEqualTo("zh-hant");
        assertThat(languages.getDetailList().get(1).getDisplayName()).isEqualTo("Chinese (Traditional Han)");
    }

    @Test
    public void shouldCallRefreshTokenFirst() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(0, 10, null, null);
    }

    @Test
    public void shouldNotUseSameTokenForMultipleCalls() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(0, 10, null, null);
        hubspotClient.listPages(0, 10, null, null);
    }

    @Test
    public void shouldDeserializeFields() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        ListWrapper<PageDetail> pageDetails = hubspotClient.listPages(5, 15, null, null);

        assertThat(pageDetails).isNotNull();
        assertThat(pageDetails.getTotalCount()).isEqualTo(6);

        List<PageDetail> detailList = pageDetails.getDetailList();
        assertThat(detailList).isNotEmpty();

        assertPageDetail(detailList.get(0));
    }

    @Test
    public void shouldCallPublishForEntityApi() throws HubspotApiException
    {
        givenThat(post(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID + "/publish-action")).willReturn(aResponse().withStatus(204)));

        hubspotClient.publish(PAGE_ID, new PublishActionRequest());

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID + "/publish-action")));
    }

    private Map<String, Object> createSearchFilter(String campaign, String name, Boolean archived, Boolean isDraft) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("campaign", campaign);
        filter.put("name__icontains", name);
        filter.put("archived", archived);
        filter.put("is_draft", isDraft);
        return filter;
    }

    private String pageSnippet()
    {
        // language=JSON
        return "{"
                + "  \"flex_areas\": {},\n"
                + "  \"page_expiry_redirect_url\": \"Some symbols % (\",\n"
                + "  \"author_user_id\": 1027715,\n"
                + "  \"performable_guid\": \"\",\n"
                + "  \"include_default_custom_css\": false,\n"
                + "  \"id\": 127"
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

    private String pageDetail()
    {
        // language=JSON
        return "{\n"
                + "  \"id\": 127,\n"
                + "  \"html_title\": \"Page 1 title\",\n"
                + "  \"name\": \"page1\",\n"
                + "  \"updated\": 1434452129000\n"
                + "}";
    }

    private String pageDetails()
    {
        // language=JSON
        return "{\n"
                + "  \"total\": 6,\n"
                + "  \"objects\": [\n"
                + "    {\n"
                + "      \"id\": 127,\n"
                + "      \"html_title\": \"Page 1 title\",\n"
                + "      \"name\": \"page1\",\n"
                + "      \"updated\": 1434452129000\n"
                + "    },\n"
                + "    {\n"
                + "      \"id\": 129,\n"
                + "      \"html_title\": \"Page 2 title\",\n"
                + "      \"name\": \"page2\",\n"
                + "      \"updated\": 1434452129990\n"
                + "    }\n"
                + "  ]\n"
                + "}";
    }

    private String supportedLanguages()
    {
        // language=JSON
        return "{\n" +
                "  \"limit\": 0,\n" +
                "  \"objects\": [\n" +
                "    {\n" +
                "      \"displayName\": \"Afrikaans\",\n" +
                "      \"tag\": \"af\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"displayName\": \"Chinese (Traditional Han)\",\n" +
                "      \"tag\": \"zh-hant\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"offset\": 0,\n" +
                "  \"total\": 0,\n" +
                "  \"totalCount\": 0\n" +
                "}";
    }

    private void assertPageDetail(final PageDetail pageDetail)
    {
        assertThat(pageDetail.getHtmlTitle()).isEqualTo("Page 1 title");
        assertThat(pageDetail.getName()).isEqualTo("page1");
        assertThat(pageDetail.getId()).isEqualTo(127);
        assertThat(pageDetail.getUpdated()).hasTime(1434452129000L);
    }
}
