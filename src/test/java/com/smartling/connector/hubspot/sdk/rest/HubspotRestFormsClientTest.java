package com.smartling.connector.hubspot.sdk.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotFormsClient;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.form.FormDetail;
import com.smartling.connector.hubspot.sdk.form.FormFilter;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
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
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.fest.assertions.api.Assertions.assertThat;

public class HubspotRestFormsClientTest
{
    private static final int PORT = 10000 + new Random().nextInt(9999);

    private static final String BASE_URL      = "http://localhost:" + PORT;
    private static final String FORM_ID       = "5d913ab1-1670-470f-91c8-962eefd8f2ec";
    private static final String FORM_NAME     = "test form name";

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TokenProvider tokenProvider;
    private HubspotFormsClient hubspotClient;
    private String originalToken;

    @Before
    public void setUpMocks() throws Exception
    {
        this.originalToken = RandomStringUtils.randomAlphanumeric(36);

        final Configuration configuration = Configuration.build(BASE_URL, null, null, null, null);
        final RefreshTokenData refreshTokenData = new RefreshTokenData();
        refreshTokenData.setAccessToken(originalToken);
        tokenProvider = () -> refreshTokenData;
        this.hubspotClient = new HubspotRestFormsClient(configuration, tokenProvider);
    }

    @Test
    public void shouldCallGetFormUrl() throws HubspotApiException
    {

        givenThat(get(HttpMockUtils.path("/forms/v2/forms/" + FORM_ID)).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        hubspotClient.getFormContentById(FORM_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms/" + FORM_ID)));
    }

    @Test
    public void shouldCallGetFormUrlForFormDetail() throws Exception
    {

        givenThat(get(HttpMockUtils.path("/forms/v2/forms/" + FORM_ID)).willReturn(HttpMockUtils.aJsonResponse(formDetail())));

        hubspotClient.getFormDetailById(FORM_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms/" + FORM_ID)));
    }

    @Test
    public void shouldCallDeleteFormUrl() throws HubspotApiException
    {

        givenThat(delete(HttpMockUtils.path("/forms/v2/forms/" + FORM_ID)).willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT)));

        hubspotClient.delete(FORM_ID);

        verify(deleteRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms/" + FORM_ID)));
    }

    @Test
    public void shouldCallCloneFormUrlForEntityApi() throws Exception
    {
        givenThat(post(HttpMockUtils.path("/forms/v2/forms/" + FORM_ID + "/clone"))
                .willReturn(HttpMockUtils.aJsonResponse(formDetail())));

        hubspotClient.cloneForm(FORM_ID, FORM_NAME);

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson("{ \"name\" : \"" + FORM_NAME + "\" }"))
        );
    }

    @Test
    public void shouldCallUpdateFormUrl() throws HubspotApiException
    {
        givenThat(post(HttpMockUtils.path("/forms/v2/forms/" + FORM_ID)).willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

        hubspotClient.updateFormContent(FORM_ID, formSnippet());

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms/" + FORM_ID))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(formSnippet()))
        );
    }

    @Test
    public void shouldCallListFormsUrl() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        hubspotClient.listForms(0, 50, new FormFilter(), null);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms")));
    }

    @Test
    public void shouldCallListFormsWithFilter() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        FormFilter filter = new FormFilter();
        filter.setFormType("FLOW");
        filter.setName("popup");
        hubspotClient.listForms(10, 5, filter, "updatedAt");

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms"))
                .withQueryParam("formTypes", equalTo("FLOW"))
                .withQueryParam(HubspotRestFormsClient.NAME_SEARCH_QUERY_PARAMETER_NAME, equalTo("popup"))
                .withQueryParam("order", equalTo("updatedAt"))
                .withQueryParam("offset", equalTo("10"))
                .withQueryParam("limit", equalTo("5")));
    }

    @Test
    public void shouldCallListAllFormsWithFilter() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        FormFilter filter = new FormFilter();
        filter.setName("popup");
        hubspotClient.listForms(10, 5, filter, "updatedAt");

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms"))
                .withQueryParam("formTypes", equalTo(HubspotRestFormsClient.DEFAULT_FORM_TYPE_FILTER))
                .withQueryParam(HubspotRestFormsClient.NAME_SEARCH_QUERY_PARAMETER_NAME, equalTo("popup"))
                .withQueryParam("order", equalTo("updatedAt"))
                .withQueryParam("offset", equalTo("10"))
                .withQueryParam("limit", equalTo("5")));
    }

    @Test
    public void shouldCallListFormsWithWithDefaultOrder() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        hubspotClient.listForms(10, 5, new FormFilter(), null);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms"))
                .withQueryParam("order", equalTo(HubspotRestFormsClient.DEFAULT_ORDER_BY)));
    }

    @Test
    public void shouldCallListFormsWithWithDefaultLimit() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        hubspotClient.listForms(10, 0, new FormFilter(), null);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms"))
                .withQueryParam("limit", equalTo(Integer.toString(HubspotRestFormsClient.DEFAULT_LIMIT_FILTER))));
    }

    @Test
    public void shouldCallRefreshTokenFirst() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        hubspotClient.listForms(0, 10, new FormFilter(), null);
    }

    @Test
    public void shouldNotUseSameTokenForMultipleCalls() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        hubspotClient.listForms(0, 10, new FormFilter(), null);
        hubspotClient.listForms(0, 10, new FormFilter(), null);
    }

    @Test
    public void shouldDeserializeFields() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        List<FormDetail> formDetails = hubspotClient.listForms(0, 50, new FormFilter(), null);

        assertThat(formDetails).isNotEmpty();

        assertThat(formDetails.size()).isEqualTo(8);
        assertFormDetail(formDetails.get(0));
    }

    private void assertFormDetail(FormDetail formDetail)
    {
        assertThat(formDetail.getGuid()).isEqualTo("0eb6c714-0fe4-4b0b-a6cf-fb69665ecefb");
        assertThat(formDetail.getName()).isEqualTo("Default HubSpot Blog Comment Form 11357778888");
        assertThat(formDetail.getSubmitText()).isEqualTo("Submit Comment");
        assertThat(formDetail.getUpdated()).isEqualTo(new Date(1563405893542L));
        assertThat(formDetail.getFormType()).isEqualTo("BLOG_COMMENT");
        assertThat(formDetail.isPublished()).isEqualTo(true);
    }

    private String formDetail() throws Exception
    {
        return loadResource("form.json");
    }

    private String formSnippet()
    {
        return   "{"
                +" \"name\": \"updated form name\","
                +" \"redirect\": \"http://hubspot.com\","
                +" \"formFieldGroups\": ["
                +" {"
                +"    \"fields\": ["
                +"    {"
                +"       \"name\": \"favoritecolor\","
                +"       \"label\": \"Favorite Color\","
                +"       \"description\": \"\","
                +"       \"groupName\": \"customfields\","
                +"       \"type\": \"string\","
                +"       \"fieldType\": \"text\","
                +"       \"displayOrder\": 0,"
                +"       \"required\": false,"
                +"       \"enabled\": true,"
                +"       \"hidden\": false,"
                +"       \"defaultValue\": \"\","
                +"       \"isSmartField\": false,"
                +"       \"validation\": "
                +"       {"
                +"          \"name\": \"\","
                +"          \"message\": \"\""
                +"       },"
                +"       \"selectedOptions\": [],"
                +"       \"options\": []"
                +"    } ]"
                +" } ],"
                +" \"deletable\":true"
                +"}";
    }

    private String formDetails() throws Exception
    {
        return  loadResource("forms.json");
    }

    private String loadResource(String name) throws IOException, URISyntaxException
    {
        URI uri = HubspotRestFormsClientTest.class.getClassLoader().getResource(name).toURI();
        return new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));
    }
}
