package com.smartling.connector.hubspot.sdk.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotPagesClient;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.page.PageDetail;
import com.smartling.connector.hubspot.sdk.page.PageDetails;
import com.smartling.connector.hubspot.sdk.page.PageSearchFilter;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Random;

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
    public void shouldCallGetPageUrlForPageDetail() throws HubspotApiException
    {

        givenThat(get(HttpMockUtils.path("/content/api/v2/pages/" + PAGE_ID)).willReturn(HttpMockUtils.aJsonResponse(pageDetail())));

        hubspotClient.getPageDetailById(PAGE_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/" + PAGE_ID)));

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
    public void shouldCallListPagesUrl() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(5, 15);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages"))
                        .withQueryParam("limit", equalTo("15"))
                        .withQueryParam("offset", equalTo("5"))
        );
    }

    @Test
    public void shouldCallListPagesUrlWithRightParams() throws HubspotApiException
    {
        final Integer offset = 5;
        final Integer limit = 15;
        final String campaign = "some-hash-id";
        final String name = "Page_name";
        final Boolean archived = Boolean.FALSE;
        final Boolean draft = Boolean.TRUE;
        PageSearchFilter filter = createSearchFilter(campaign, name, archived, draft);
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(offset, limit, filter);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages"))
                        .withQueryParam("limit", equalTo(limit.toString()))
                        .withQueryParam("offset", equalTo(offset.toString()))
                        .withQueryParam("campaign", equalTo(campaign))
                        .withQueryParam("name__icontains", equalTo(name))
                        .withQueryParam("archived", equalTo(archived.toString()))
                        .withQueryParam("is_draft", equalTo(draft.toString()))
        );
    }

    @Test
    public void shouldCallRefreshTokenFirst() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(0, 10);
    }

    @Test
    public void shouldNotUseSameTokenForMultipleCalls() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        hubspotClient.listPages(0, 10);
        hubspotClient.listPages(0, 10);
    }

    @Test
    public void shouldDeserializeFields() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages")).willReturn(HttpMockUtils.aJsonResponse(pageDetails())));

        PageDetails pageDetails = hubspotClient.listPages(5, 15);

        assertThat(pageDetails).isNotNull();
        assertThat(pageDetails.getTotalCount()).isEqualTo(6);

        List<PageDetail> detailList = pageDetails.getDetailList();
        assertThat(detailList).isNotEmpty();

        assertPageDetail(detailList.get(0));
    }

    private PageSearchFilter createSearchFilter(String campaign, String name, Boolean archived, Boolean draft) {
        PageSearchFilter filter = new PageSearchFilter();
        filter.setCampaign(campaign);
        filter.setName(name);
        filter.setArchived(archived);
        filter.setDraft(draft);
        return filter;
    }

    private String pageSnippet()
    {
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
        return "{\n"
                + "  \"succeeded\": true,\n"
                + "  \"message\": \"Action succeeded\"\n"
                + "}";
    }

    private String pageDetail()
    {
        return "{\n"
                + "  \"id\": 127,\n"
                + "  \"html_title\": \"Page 1 title\",\n"
                + "  \"name\": \"page1\",\n"
                + "  \"updated\": 1434452129000\n"
                + "}";
    }

    private String pageDetails()
    {
        return "{\n"
                + "  \"total_count\": 6,\n"
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

    private void assertPageDetail(final PageDetail pageDetail)
    {
        assertThat(pageDetail.getHtmlTitle()).isEqualTo("Page 1 title");
        assertThat(pageDetail.getName()).isEqualTo("page1");
        assertThat(pageDetail.getId()).isEqualTo(127);
        assertThat(pageDetail.getUpdated()).hasTime(1434452129000L);
    }
}
