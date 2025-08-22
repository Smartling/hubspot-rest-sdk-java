package com.smartling.connector.hubspot.sdk.v3.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.HttpMockUtils;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import com.smartling.connector.hubspot.sdk.v3.HubspotEmailsV3Client;
import com.smartling.connector.hubspot.sdk.v3.email.CloneEmailRequest;
import com.smartling.connector.hubspot.sdk.v3.email.EmailDetail;
import com.smartling.connector.hubspot.sdk.v3.email.EmailState;
import com.smartling.connector.hubspot.sdk.v3.email.ListWrapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.fest.assertions.api.Assertions.assertThat;

public class HubspotRestEmailsV3ClientTest
{
    private static final int PORT = 10000 + new Random().nextInt(9999);
    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final String EMAIL_ID = "11358385328";
    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HubspotEmailsV3Client emailClient;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Before
    public void init()
    {
        String originalToken = RandomStringUtils.randomAlphanumeric(36);

        final Configuration configuration = Configuration.build(BASE_URL, null, null, null, null);
        final RefreshTokenData refreshTokenData = new RefreshTokenData();
        refreshTokenData.setAccessToken(originalToken);
        TokenProvider tokenProvider = () -> refreshTokenData;
        this.emailClient = new HubspotRestEmailsV3Client(configuration, tokenProvider);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void shouldCallListEmails() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/marketing/v3/emails")).willReturn(HttpMockUtils.aJsonResponse(emailsContent())));

        Map<String, Object> queryMap = new HashMap<>();
       ListWrapper<EmailDetail> result =
                emailClient.listEmails(30, 15, "updated", queryMap);

        // Verify the API call
        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/marketing/v3/emails"))
                .withQueryParam("offset", equalTo("30"))
                .withQueryParam("limit", equalTo("15"))
                .withQueryParam("sort", equalTo("updated")));

        // Verify the returned results
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1175);
        assertThat(result.getResults()).hasSize(1);
        EmailDetail emailDetail = result.getResults().get(0);
        assertEmailDetail(emailDetail);

        // Verify pagination information
        assertThat(result.getPaging()).isNotNull();
        assertThat(result.getPaging().getNext()).isNotNull();
        assertThat(result.getPaging().getNext().getAfter()).isEqualTo("Mg%3D%3D");
        assertThat(result.getPaging().getNext().getLink()).contains("after=Mg%3D%3D");
    }

    private void assertEmailDetail(EmailDetail emailDetail)
    {
        assertThat(emailDetail.getId()).isEqualTo("187563852197");
        assertThat(emailDetail.getState()).isEqualTo(EmailState.DRAFT);
        assertThat(emailDetail.getType()).isEqualTo("BATCH_EMAIL");
        assertThat(sdf.format(emailDetail.getUpdatedAt())).isEqualTo("2025-03-19T15:53:07.096Z");
    }

    @Test
    public void shouldCallGetEmailById() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/marketing/v3/emails/" + EMAIL_ID)).willReturn(HttpMockUtils.aJsonResponse(emailContent())));

        EmailDetail emailDetail = emailClient.getDetail(EMAIL_ID);

        assertEmailDetail(emailDetail);
        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/marketing/v3/emails/" + EMAIL_ID)));
    }

    @Test
    public void shouldCallGetDraftDetail() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/marketing/v3/emails/" + EMAIL_ID + "/draft")).willReturn(HttpMockUtils.aJsonResponse(emailContent())));

        EmailDetail emailDetail = emailClient.getDraftDetail(EMAIL_ID);

        assertEmailDetail(emailDetail);
        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/marketing/v3/emails/" + EMAIL_ID + "/draft")));
    }

    @Test
    public void shouldCallGetAbTestVariation() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/marketing/v3/emails/" + EMAIL_ID + "/ab-test/get-variation")).willReturn(HttpMockUtils.aJsonResponse(emailContent())));

        EmailDetail emailDetail = emailClient.getAbTestVariation(EMAIL_ID);

        assertEmailDetail(emailDetail);
        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/marketing/v3/emails/" + EMAIL_ID + "/ab-test/get-variation")));
    }

    @Test
    public void shouldCallGetContent() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/marketing/v3/emails/" + EMAIL_ID)).willReturn(HttpMockUtils.aJsonResponse(emailContent())));

        emailClient.getContent(EMAIL_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/marketing/v3/emails/" + EMAIL_ID)));
    }

    @Test
    public void shouldCallUpdateContent() throws Exception
    {
        String content = "Updated content";
        givenThat(patch(HttpMockUtils.path("/marketing/v3/emails/" + EMAIL_ID)).willReturn(HttpMockUtils.aJsonResponse("{\"status\": \"updated\"}")));

        emailClient.updateContent(EMAIL_ID, content);

        verify(patchRequestedFor(HttpMockUtils.urlStartingWith("/marketing/v3/emails/" + EMAIL_ID))
                .withRequestBody(equalTo(content)));
    }

    @Test
    public void shouldCallCloneWithRequest() throws Exception
    {
        CloneEmailRequest request = new CloneEmailRequest();
        request.setId(EMAIL_ID);
        request.setCloneName("Clone Name");
        request.setLanguage("fr");

        String requestJson = MAPPER.writeValueAsString(request);

        givenThat(post(HttpMockUtils.path("/marketing/v3/emails/clone")).willReturn(HttpMockUtils.aJsonResponse(emailContent())));

        emailClient.clone(request);

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/marketing/v3/emails/clone"))
                .withRequestBody(equalToJson(requestJson)));
    }

    @Test
    public void shouldThrowExceptionOnApiError() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/marketing/v3/emails/" + EMAIL_ID))
                .willReturn(HttpMockUtils.aJsonResponse("{\"status\":\"error\",\"message\":\"Not found\"}")
                        .withStatus(404)));

        expectedException.expect(HubspotApiException.class);
        emailClient.getDetail(EMAIL_ID);
    }

    private String emailsContent()
    {
        return "{" +
                "\"total\": 1175," +
                "\"results\": [" +
                emailContent() +
                "]," +
                "\"paging\": {" +
                "    \"next\": {" +
                "        \"after\": \"Mg%3D%3D\"," +
                "        \"link\": \"https://api.hubapi.com/marketing/v3/emails?limit=1&sort=-updated&after=Mg%3D%3D&includedProperties=state&includedProperties=updatedAt\"" +
                "    }" +
                "}" +
                "}";
    }

    private String emailContent()
    {
        return "{" +
                "\"id\": \"187563852197\"," +
                "\"isAb\": false," +
                "\"isPublished\": false," +
                "\"state\": \"DRAFT\"," +
                "\"type\": \"BATCH_EMAIL\"," +
                "\"updatedAt\": \"2025-03-19T15:53:07.096Z\"" +
                "}";
    }
}
