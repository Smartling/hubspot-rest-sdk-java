package com.smartling.connector.hubspot.sdk.rest;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotClient;
import com.smartling.connector.hubspot.sdk.PageDetails;
import com.smartling.connector.hubspot.sdk.rest.api.PageDetail;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.fest.assertions.api.Assertions.assertThat;

public class HubspotRestClientTest
{
    private static final int PORT = 10000 + new Random().nextInt(9999);

    private static final String BASE_URL      = "http://localhost:" + PORT;
    private static final String REFRESH_TOKEN = "3333-4444-5555";
    private static final String CLIENT_ID     = "0000-1111-2222";
    private static final long   PAGE_ID       = 127L;

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HubspotClient hubspotClient = new HubspotRestClient(BASE_URL, CLIENT_ID, REFRESH_TOKEN);

    @Before
    public void setUpMocks()
    {
        stubFor(post(urlStartingWith("/auth")).willReturn(aJsonResponse(getTokenData())));
    }

    @Test
    public void shouldCallGetPageUrl() throws HubspotApiException
    {

        givenThat(get(path("/content/api/v2/pages/" + PAGE_ID)).willReturn(aJsonResponse("anyResponse")));

        hubspotClient.getPageById(PAGE_ID);

        verify(getRequestedFor(urlStartingWith("/content/api/v2/pages/" + PAGE_ID))
                        .withQueryParam("access_token", equalTo("access-token"))
        );

    }

    @Test
    public void shouldCallClonePageUrl() throws HubspotApiException
    {
        givenThat(post(path("/content/api/v2/pages/" + PAGE_ID + "/clone")).willReturn(aJsonResponse("anyResponse")));

        hubspotClient.clonePage(PAGE_ID);

        verify(postRequestedFor(urlStartingWith("/content/api/v2/pages/" + PAGE_ID + "/clone"))
                        .withQueryParam("access_token", equalTo("access-token"))
        );
    }

    @Test
    public void shouldCallUpdatePageUrl() throws HubspotApiException
    {
        givenThat(put(path("/content/api/v2/pages/" + PAGE_ID)).willReturn(aJsonResponse("anyResponse")));

        hubspotClient.updatePage(pageSnippet());

        verify(putRequestedFor(urlStartingWith("/content/api/v2/pages/" + PAGE_ID))
                        .withQueryParam("access_token", equalTo("access-token"))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalTo(pageSnippet()))
        );
    }

    @Test
    public void shouldCallListPagesUrl() throws HubspotApiException
    {
        givenThat(get(path("/content/api/v2/pages")).willReturn(aJsonResponse(pageDetails())));

        hubspotClient.listPages(5, 15);

        verify(getRequestedFor(urlStartingWith("/content/api/v2/pages"))
                        .withQueryParam("access_token", equalTo("access-token"))
                        .withQueryParam("limit", equalTo("15"))
                        .withQueryParam("offset", equalTo("5"))
        );
    }

    @Test
    public void shouldCallListPagesByTmsIdUrl() throws HubspotApiException
    {
        givenThat(get(path("/content/api/v2/pages")).willReturn(aJsonResponse(pageDetails())));

        hubspotClient.listPagesByTmsId("someId");

        verify(getRequestedFor(urlStartingWith("/content/api/v2/pages"))
                        .withQueryParam("access_token", equalTo("access-token"))
                        .withQueryParam("tms_id", equalTo("someId"))
        );
    }

    @Test
    public void shouldCallRefreshTokenFirst() throws HubspotApiException
    {
        givenThat(get(path("/content/api/v2/pages")).willReturn(aJsonResponse(pageDetails())));

        hubspotClient = new HubspotRestClient(BASE_URL, CLIENT_ID, REFRESH_TOKEN);
        hubspotClient.listPagesByTmsId("someId");

        verify(postRequestedFor(urlStartingWith("/auth"))
                        .withRequestBody(withFormParam("client_id", CLIENT_ID))
                        .withRequestBody(withFormParam("refresh_token", REFRESH_TOKEN))
                        .withRequestBody(withFormParam("grant_type", "refresh_token"))
        );
    }

    @Test
    public void shouldRefreshTokenIfItIsExpired() throws HubspotApiException
    {
        givenThat(get(path("/content/api/v2/pages")).willReturn(aJsonResponse(pageDetails())));

        stubFor(post(urlStartingWith("/auth")).willReturn(aJsonResponse(getExpiredTokenData())));

        hubspotClient.listPages(0, 1);
        hubspotClient.listPages(0, 1);

        verify(2, postRequestedFor(urlStartingWith("/auth")));
    }

    @Test
    public void shouldUseSameTokenForMultipleCalls() throws HubspotApiException
    {
        givenThat(get(path("/content/api/v2/pages")).willReturn(aJsonResponse(pageDetails())));

        hubspotClient = new HubspotRestClient(BASE_URL, CLIENT_ID, REFRESH_TOKEN);
        hubspotClient.listPagesByTmsId("someId");
        hubspotClient.listPagesByTmsId("someId");

        verify(1, postRequestedFor(urlStartingWith("/auth")));
    }

    @Test
    public void shouldDeserializeFields() throws HubspotApiException
    {
        givenThat(get(path("/content/api/v2/pages")).willReturn(aJsonResponse(pageDetails())));

        PageDetails pageDetails = hubspotClient.listPages(5, 15);

        assertThat(pageDetails).isNotNull();
        assertThat(pageDetails.getTotalCount()).isEqualTo(6);

        List<PageDetail> detailList = pageDetails.getDetailList();
        assertThat(detailList).isNotEmpty();

        assertPageDetail(detailList.get(0));
    }

    @Test
    public void shouldThrowExceptionIfUpdatedPageIsMalformed() throws Exception
    {

        expectedException.expect(HubspotApiException.class);
        expectedException.expectMessage("JSON syntax of page snippet is wrong!");

        hubspotClient.updatePage("{{}");
    }

    private String getExpiredTokenData()
    {
        return "{\n"
                + "  \"portal_id\": 584677,\n"
                + "  \"expires_in\": 0,\n"
                + "  \"refresh_token\": \"684f2944-b474-4440-8b2a-207d4c26a959\",\n"
                + "  \"access_token\": \"access-token\"\n"
                + "}";
    }

    private String getTokenData()
    {
        return "{\n"
                + "  \"portal_id\": 584677,\n"
                + "  \"expires_in\": 28799,\n"
                + "  \"refresh_token\": \"684f2944-b474-4440-8b2a-207d4c26a959\",\n"
                + "  \"access_token\": \"access-token\"\n"
                + "}";
    }

    private String pageSnippet()
    {
        return "{"
                + "  \"flex_areas\": {},\n"
                + "  \"page_expiry_redirect_url\": \"\",\n"
                + "  \"author_user_id\": 1027715,\n"
                + "  \"performable_guid\": \"\",\n"
                + "  \"include_default_custom_css\": false,\n"
                + "  \"id\": 127"
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

    private static UrlMatchingStrategy path(String path)
    {
        return urlMatching(path + "(\\?.+)?");
    }

    private static ResponseDefinitionBuilder aJsonResponse(String json)
    {
        return aResponse().withHeader("Content-Type", "application/json").withBody(json);
    }

    private static UrlMatchingStrategy urlStartingWith(String path)
    {
        return urlMatching(path + ".*");
    }

    private ValueMatchingStrategy withFormParam(String key, String value)
    {
        return containing(key + "=" + value);
    }

    private void assertPageDetail(final PageDetail pageDetail)
    {
        assertThat(pageDetail.getHtmlTitle()).isEqualTo("Page 1 title");
        assertThat(pageDetail.getName()).isEqualTo("page1");
        assertThat(pageDetail.getId()).isEqualTo(127);
        assertThat(pageDetail.getUpdated()).hasTime(1434452129000L);
    }
}