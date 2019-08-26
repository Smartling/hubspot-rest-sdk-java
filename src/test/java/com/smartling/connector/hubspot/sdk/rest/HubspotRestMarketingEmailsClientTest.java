package com.smartling.connector.hubspot.sdk.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotMarketingEmailsClient;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.marketingEmail.MarketingEmailDetail;
import com.smartling.connector.hubspot.sdk.marketingEmail.MarketingEmailDetails;
import com.smartling.connector.hubspot.sdk.marketingEmail.MarketingEmailFilter;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.fest.assertions.api.Assertions.assertThat;

public class HubspotRestMarketingEmailsClientTest {

    private static final int PORT = 10000 + new Random().nextInt(9999);

    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final String EMAIL_ID = "11358385328";

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TokenProvider tokenProvider;
    private String originalToken;
    private HubspotMarketingEmailsClient emailClient;

    @Before
    public void setUpMocks() throws Exception
    {
        this.originalToken = RandomStringUtils.randomAlphanumeric(36);

        final Configuration configuration = Configuration.build(BASE_URL, null, null, null, null);
        final RefreshTokenData refreshTokenData = new RefreshTokenData();
        refreshTokenData.setAccessToken(originalToken);
        tokenProvider = () -> refreshTokenData;
        this.emailClient = new HubspotRestMarketingEmailsClient(configuration, tokenProvider);
    }

    @Test
    public void shouldCallListEmails() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/marketing-emails/v1/emails")).willReturn(HttpMockUtils.aJsonResponse(loadResource("marketing_emails.json"))));

        emailClient.listEmails(5, 15, new MarketingEmailFilter(), null);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/marketing-emails/v1/emails"))
                .withQueryParam("limit", equalTo("15"))
                .withQueryParam("offset", equalTo("5"))
        );
    }

    @Test
    public void shouldCallGetEmailById() throws HubspotApiException, IOException, URISyntaxException
    {
        givenThat(get(HttpMockUtils.path("/marketing-emails/v1/emails/" + EMAIL_ID)).willReturn(HttpMockUtils.aJsonResponse(loadResource("marketing_email.json"))));

        emailClient.getEmailById(EMAIL_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/marketing-emails/v1/emails/" + EMAIL_ID)));
    }

    @Test
    public void shouldCallCreateEmail() throws Exception
    {
        withPostHttpResponseData("/marketing-emails/v1/emails", loadResource("marketing_email.json"));

        String json = minifyJson(loadResource("translated_marketing_email.json"));
        MarketingEmailDetail emailDetail = getTranslatedEmail(json);

        MarketingEmailDetail email = emailClient.createEmail(emailDetail);

        ValueMatchingStrategy valueMatchingStrategy = new ValueMatchingStrategy();
        valueMatchingStrategy.setEqualToJson(json);
        verify(postRequestedFor(HttpMockUtils.path("/marketing-emails/v1/emails"))
                .withRequestBody(valueMatchingStrategy));

        assertThat(email.getId()).isEqualTo(EMAIL_ID);
    }

    @Test
    public void shouldCallUpdateEmail() throws Exception
    {
        withPutHttpResponseData("/marketing-emails/v1/emails/1", minifyJson(loadResource("marketing_email.json")));

        String json = minifyJson(loadResource("translated_marketing_email.json"));
        MarketingEmailDetail emailDetail = getTranslatedEmail(json);
        emailDetail.setId("1");

        MarketingEmailDetail email = emailClient.updateEmail(emailDetail);

        ValueMatchingStrategy valueMatchingStrategy = new ValueMatchingStrategy();
        valueMatchingStrategy.setEqualToJson(json);
        verify(putRequestedFor(HttpMockUtils.path("/marketing-emails/v1/emails/1"))
                .withRequestBody(valueMatchingStrategy));

        assertThat(email.getId()).isEqualTo(EMAIL_ID);
    }

    @Test
    public void shouldDeserializeFields() throws Exception
    {
        givenThat(get(HttpMockUtils.urlStartingWith("/marketing-emails/v1/emails")).willReturn(HttpMockUtils.aJsonResponse(loadResource("marketing_emails.json"))));

        MarketingEmailDetails emailDetails = emailClient.listEmails(5, 15, new MarketingEmailFilter(), null);

        assertThat(emailDetails).isNotNull();
        assertThat(emailDetails.getTotalCount()).isEqualTo(6);

        List<MarketingEmailDetail> detailList = emailDetails.getDetailList();
        assertThat(detailList).isNotEmpty();

        assertEmailDetail(detailList.get(0));
    }

    private String minifyJson(final String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
        return jsonNode.toString();
    }

    private MarketingEmailDetail getTranslatedEmail(String json)
    {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, MarketingEmailDetail.class);
    }

    private void withPostHttpResponseData(String url, String data)
    {
        stubFor(post(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody(data)));
    }

    private void withPutHttpResponseData(String url, String data)
    {
        stubFor(put(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(data)));
    }

    private String loadResource(String name) throws IOException, URISyntaxException
    {
        URI uri = HubspotRestMarketingEmailsClientTest.class.getClassLoader().getResource(name).toURI();
        return new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));
    }

    private void assertEmailDetail(final MarketingEmailDetail emailDetail) throws Exception
    {
        assertThat(emailDetail.getName()).isEqualTo("Subject");
        assertThat(emailDetail.getWidgets()).startsWith("{" +
                "\"builtin_module_0_0_0\":{" +
                "\"body\":{" +
                "\"alignment\":\"center\"," +
                "\"hs_enable_module_padding\":true," +
                "\"img\":{" +
                "\"alt\":\"HubSpot logo orange\"," +
                "\"height\":72,");
        assertThat(emailDetail.isPublishImmediately()).isTrue();
        assertThat(emailDetail.getId()).isEqualTo(EMAIL_ID);
    }
}
