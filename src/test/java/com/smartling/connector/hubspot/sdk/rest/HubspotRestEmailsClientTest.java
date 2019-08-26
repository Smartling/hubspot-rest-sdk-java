package com.smartling.connector.hubspot.sdk.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotEmailsClient;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.email.EmailDetail;
import com.smartling.connector.hubspot.sdk.email.EmailDetails;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Random;

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

public class HubspotRestEmailsClientTest
{

    private static final int PORT = 10000 + new Random().nextInt(9999);

    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final String EMAIL_ID = "11358385328";

    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TokenProvider tokenProvider;
    private String originalToken;
    private HubspotEmailsClient emailClient;

    @Before
    public void init()
    {
        this.originalToken = RandomStringUtils.randomAlphanumeric(36);

        final Configuration configuration = Configuration.build(BASE_URL, null, null, null, null);
        final RefreshTokenData refreshTokenData = new RefreshTokenData();
        refreshTokenData.setAccessToken(originalToken);
        tokenProvider = () -> refreshTokenData;
        this.emailClient = new HubspotRestEmailsClient(configuration, tokenProvider);
    }

    @Test
    public void shouldCallListEmails() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/marketing-emails/v1/emails")).willReturn(HttpMockUtils.aJsonResponse(emailsContent())));

        emailClient.listEmails(5, 15, "updated", null);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/marketing-emails/v1/emails"))
                .withQueryParam("limit", equalTo("15"))
                .withQueryParam("offset", equalTo("5"))
                .withQueryParam("order_by", equalTo("updated")));
    }

    @Test
    public void shouldCallGetEmailById() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/marketing-emails/v1/emails/" + EMAIL_ID)).willReturn(HttpMockUtils.aJsonResponse(emailContent())));

        emailClient.getDetail(EMAIL_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/marketing-emails/v1/emails/" + EMAIL_ID)));
    }

    @Test
    public void shouldCallGetEmailContent() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/marketing-emails/v1/emails/" + EMAIL_ID)).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        emailClient.getContent(EMAIL_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/marketing-emails/v1/emails/" + EMAIL_ID)));
    }

    @Test
    public void shouldCallUpdateEmail() throws Exception
    {
        givenThat(put(HttpMockUtils.path("/marketing-emails/v1/emails/" + EMAIL_ID)).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        emailClient.updateContent(EMAIL_ID, emailContent());

        verify(putRequestedFor(HttpMockUtils.path("/marketing-emails/v1/emails/" + EMAIL_ID))
                .withHeader("Content-Type", equalTo("application/json")));
    }

    @Test
    public void shouldCallCloneEmail() throws Exception
    {
        givenThat(post(HttpMockUtils.path("/marketing-emails/v1/emails/" + EMAIL_ID + "/clone")).willReturn(HttpMockUtils.aJsonResponse(emailContent())));

        emailClient.clone(EMAIL_ID, "new name");

        verify(postRequestedFor(HttpMockUtils.path("/marketing-emails/v1/emails/" + EMAIL_ID+ "/clone"))
                .withHeader("Content-Type", equalTo("application/json")));
    }

    @Test
    public void shouldThrowNativeExceptionForBadResponse() throws Exception
    {
        givenThat(post(HttpMockUtils.path("/marketing-emails/v1/emails/" + EMAIL_ID + "/clone")).willReturn(HttpMockUtils.aJsonResponse("any").withStatus(400)));
        expectedException.expect(HubspotApiException.class);

        emailClient.clone(EMAIL_ID, emailContent());
    }

    @Test
    public void shouldThrowNativeExceptionForBrokenJson() throws Exception
    {
        givenThat(post(HttpMockUtils.path("/marketing-emails/v1/emails/" + EMAIL_ID + "/clone")).willReturn(HttpMockUtils.aJsonResponse("not JSON")));
        expectedException.expect(HubspotApiException.class);

        emailClient.clone(EMAIL_ID, emailContent());
    }

    @Test
    public void shouldDeserializeFields() throws Exception
    {
        givenThat(get(HttpMockUtils.urlStartingWith("/marketing-emails/v1/emails")).willReturn(HttpMockUtils.aJsonResponse(emailsContent())));

        EmailDetails emailDetails = emailClient.listEmails(5, 15, null, null);

        assertThat(emailDetails).isNotNull();
        assertThat(emailDetails.getTotalCount()).isEqualTo(6);

        List<EmailDetail> detailList = emailDetails.getDetailList();
        assertThat(detailList).isNotEmpty();

        assertEmailDetail(detailList.get(0));
    }

    private String minifyJson(final String json) throws Exception {
        JsonNode jsonNode = MAPPER.readValue(json, JsonNode.class);
        return jsonNode.toString();
    }

    private void assertEmailDetail(final EmailDetail emailDetail)
    {
        assertThat(emailDetail.getAuthorName()).isEqualTo("HubSpot Test");
        assertThat(emailDetail.getCurrentState()).isEqualTo("DRAFT");
        assertThat(emailDetail.getEmailType()).isEqualTo("BATCH_EMAIL");
        assertThat(emailDetail.getFromName()).isEqualTo("HubSpot Test");
        assertThat(emailDetail.getId()).isEqualTo(EMAIL_ID);
        assertThat(emailDetail.getName()).isEqualTo("sl-mm-test-email");
        assertThat(emailDetail.getPublishedByName()).isEqualTo("HubSpot Test");
        assertThat(emailDetail.getRssEmailByText()).isEqualTo("By HubSpot Test");
        assertThat(emailDetail.getRssEmailClickThroughText()).isEqualTo("Read more &raquo;");
        assertThat(emailDetail.getSubject()).isEqualTo("Subject");
        assertThat(emailDetail.getSubscriptionName()).isEqualTo("Marketing Information");
    }

    private String emailContent() throws Exception {
        return minifyJson(emailDetail());
    }

    private String emailsContent() throws Exception {
        return minifyJson(emailDetails());
    }

    private String emailDetail() {
        return "{\n" +
                "  \"authorName\": \"HubSpot Test\",\n" +
                "  \"currentState\": \"DRAFT\",\n" +
                "  \"emailType\": \"BATCH_EMAIL\",\n" +
                "  \"fromName\": \"HubSpot Test\",\n" +
                "  \"htmlTitle\": \"\",\n" +
                "  \"id\": 11358385328,\n" +
                "  \"name\": \"sl-mm-test-email\",\n" +
                "  \"publishedByName\": \"HubSpot Test\",\n" +
                "  \"rssEmailByText\": \"By HubSpot Test\",\n" +
                "  \"rssEmailClickThroughText\": \"Read more &raquo;\",\n" +
                "  \"rssEmailCommentText\": \"Comment &raquo;\",\n" +
                "  \"subject\": \"Subject\",\n" +
                "  \"subscriptionName\": \"Marketing Information\"\n" +
                "}";
    }

    private String emailDetails() {
        return "{\n" +
                "  \"limit\": 10,\n" +
                "  \"objects\": [\n" +
                "    {\n" +
                "      \"authorName\": \"HubSpot Test\",\n" +
                "      \"currentState\": \"DRAFT\",\n" +
                "      \"emailType\": \"BATCH_EMAIL\",\n" +
                "      \"fromName\": \"HubSpot Test\",\n" +
                "      \"htmlTitle\": \"\",\n" +
                "      \"id\": 11358385328,\n" +
                "      \"name\": \"sl-mm-test-email\",\n" +
                "      \"publishedByName\": \"HubSpot Test\",\n" +
                "      \"rssEmailByText\": \"By HubSpot Test\",\n" +
                "      \"rssEmailClickThroughText\": \"Read more &raquo;\",\n" +
                "      \"rssEmailCommentText\": \"Comment &raquo;\",\n" +
                "      \"subject\": \"Subject\",\n" +
                "      \"subscriptionName\": \"Marketing Information\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"offset\": 0,\n" +
                "  \"total\": 6,\n" +
                "  \"totalCount\": 6\n" +
                "}";
    }
}
