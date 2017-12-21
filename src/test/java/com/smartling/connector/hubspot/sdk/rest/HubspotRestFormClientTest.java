package com.smartling.connector.hubspot.sdk.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.JsonParser;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotFormClient;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.form.FormDetail;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
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

public class HubspotRestFormClientTest
{
    private static final int PORT = 10000 + new Random().nextInt(9999);

    private static final String BASE_URL      = "http://localhost:" + PORT;
    private static final String FORM_ID       = "5d913ab1-1670-470f-91c8-962eefd8f2ec";

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TokenProvider tokenProvider;
    private HubspotFormClient hubspotClient;
    private String originalToken;

    @Before
    public void setUpMocks() throws Exception
    {
        this.originalToken = RandomStringUtils.randomAlphanumeric(36);

        final Configuration configuration = Configuration.build(BASE_URL, null, null, null, null);
        final RefreshTokenData refreshTokenData = new RefreshTokenData();
        refreshTokenData.setAccessToken(originalToken);
        tokenProvider = () -> refreshTokenData;
        this.hubspotClient = new HubspotRestFormClient(configuration, tokenProvider);
    }

    @Test
    public void shouldCallGetFormUrl() throws HubspotApiException
    {

        givenThat(get(HttpMockUtils.path("/forms/v2/forms/" + FORM_ID)).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        hubspotClient.getFormContentById(FORM_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms/" + FORM_ID)));
    }

    @Test
    public void shouldCallGetFormUrlForFormDetail() throws HubspotApiException
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
    public void shouldCallCloneFormUrlForEntityApi() throws HubspotApiException
    {
        String body = formDetail();
        String original = new JsonParser().parse(body).toString();
        String newGuid = UUID.randomUUID().toString();
        body = body.replaceFirst("My New Form", "My New Form - Clone -");
        body = body.replaceFirst("6364429e-9c68-4c38-a71c-e1edb98825fc", newGuid);

        givenThat(get(HttpMockUtils.path("/forms/v2/forms/" + FORM_ID)).willReturn(HttpMockUtils.aJsonResponse(original)));
        givenThat(post(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(body)));
        givenThat(get(HttpMockUtils.path("/forms/v2/forms/" + newGuid)).willReturn(HttpMockUtils.aJsonResponse(body)));

        hubspotClient.cloneFormAsDetail(FORM_ID);

        int i = original.indexOf("\",\"action\"");
        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(containing(original.substring(0, i)))
                .withRequestBody(containing(original.substring(i)))
        );
        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms/" + newGuid)));
    }

    @Test
    public void cloneShouldResetDeletableToTrue() throws HubspotApiException
    {
        final String guid = "6364429e-9c68-4c38-a71c-e1edb98825fc";
        String body = formDetail();
        body = body.replaceFirst("\"deletable\": true", "\"deletable\": false");

        givenThat(get(HttpMockUtils.path("/forms/v2/forms/" + FORM_ID))
                .willReturn(HttpMockUtils.aJsonResponse(body)));

        givenThat(post(HttpMockUtils.path("/forms/v2/forms"))
                .willReturn(HttpMockUtils.aJsonResponse(body)));

        givenThat(get(HttpMockUtils.path("/forms/v2/forms/" + guid))
                .willReturn(HttpMockUtils.aJsonResponse(body)));

        hubspotClient.cloneFormAsDetail(FORM_ID);

        verify(
                postRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms"))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(containing("\"deletable\":true"))
        );
    }

    @Test
    public void shouldCallUpdateFormUrl() throws HubspotApiException
    {
        givenThat(post(HttpMockUtils.path("/forms/v2/forms/" + FORM_ID)).willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

        String form = formSnippet();
        form = form.replaceFirst("\"deletable\": true", "\"deletable\": false");

        hubspotClient.updateFormContent(FORM_ID, form);

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms/" + FORM_ID))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(formSnippet()))
        );
    }

    @Test
    public void shouldCallListFormsUrl() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        hubspotClient.listForms();

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms")));
    }

    @Test
    public void shouldCallListFormsByTmsIdUrl() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        hubspotClient.listFormsByTmsId("someId");

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/forms/v2/forms")));
    }

    @Test
    public void shouldCallRefreshTokenFirst() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        hubspotClient.listFormsByTmsId("someId");
    }

    @Test
    public void shouldNotUseSameTokenForMultipleCalls() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        hubspotClient.listFormsByTmsId("someId");
        hubspotClient.listFormsByTmsId("someId");
    }

    @Test
    public void shouldDeserializeFields() throws HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/forms/v2/forms")).willReturn(HttpMockUtils.aJsonResponse(formDetails())));

        List<FormDetail> formDetails = hubspotClient.listForms();

        assertThat(formDetails).isNotEmpty();

        assertThat(formDetails.size()).isEqualTo(2);
        assertFormDetail(formDetails.get(0));
    }

    private void assertFormDetail(FormDetail formDetail)
    {
        assertThat(formDetail.getGuid()).isEqualTo("0033cf74de6a48c4ac5d805d72d69822");
        assertThat(formDetail.getName()).isEqualTo("Dummy UnitTest Form [6bd6532d-2a9b-49] (Leads API)");
        assertThat(formDetail.getSubmitText()).isEqualTo("Submit");
        assertThat(formDetail.getUpdated()).isEqualTo(new Date(1433967918640L));
    }

    private String formDetail()
    {
        return   "{"
                +" \"portalId\": 123,"
                +" \"guid\": \"6364429e-9c68-4c38-a71c-e1edb98825fc\","
                +" \"name\": \"My New Form\","
                +" \"action\": \"\","
                +" \"method\": \"POST\","
                +" \"cssClass\": \"hs-form stacked\","
                +" \"redirect\": \"\","
                +" \"submitText\": \"Submit\","
                +" \"followUpId\": \"\","
                +" \"notifyRecipients\": \"\","
                +" \"leadNurturingCampaignId\": \"\","
                +" \"formFieldGroups\": ["
                +" {"
                +"    \"fields\": ["
                +"    {"
                +"       \"name\": \"firstname\","
                +"       \"label\": \"First Name\","
                +"       \"type\": \"string\","
                +"       \"fieldType\": \"text\","
                +"       \"description\": \"\","
                +"       \"groupName\": \"contactinformation\","
                +"       \"displayOrder\": -1,"
                +"       \"required\": false,"
                +"       \"selectedOptions\": [],"
                +"       \"options\": [],"
                +"       \"validation\": "
                +"       {"
                +"          \"name\": \"\","
                +"          \"message\": \"\","
                +"          \"data\": \"\","
                +"          \"useDefaultBlockList\": false"
                +"       },"
                +"       \"enabled\": true,"
                +"       \"hidden\": false,"
                +"       \"defaultValue\": \"\","
                +"       \"isSmartField\": false,"
                +"       \"unselectedLabel\": \"\","
                +"       \"placeholder\": \"\""
                +"    },"
                +"    {"
                +"       \"name\": \"lastname\","
                +"       \"label\": \"Last Name\","
                +"       \"type\": \"string\","
                +"       \"fieldType\": \"text\","
                +"       \"description\": \"\","
                +"       \"groupName\": \"contactinformation\","
                +"       \"displayOrder\": -1,"
                +"       \"required\": false,"
                +"       \"selectedOptions\": [],"
                +"       \"options\": [],"
                +"       \"validation\": "
                +"       {"
                +"          \"name\": \"\","
                +"          \"message\": \"\","
                +"          \"data\": \"\","
                +"          \"useDefaultBlockList\": false"
                +"       },"
                +"       \"enabled\": true,"
                +"       \"hidden\": false,"
                +"       \"defaultValue\": \"\","
                +"       \"isSmartField\": false,"
                +"       \"unselectedLabel\": \"\","
                +"       \"placeholder\": \"\""
                +"    } ],"
                +"    \"default\": true,"
                +"    \"isSmartGroup\": false"
                +" },"
                +" {"
                +"    \"fields\": ["
                +"    {"
                +"       \"name\": \"city\","
                +"       \"label\": \"City\","
                +"       \"type\": \"string\","
                +"       \"fieldType\": \"text\","
                +"       \"description\": \"\","
                +"       \"groupName\": \"\","
                +"       \"displayOrder\": -1,"
                +"       \"required\": false,"
                +"       \"selectedOptions\": [],"
                +"       \"options\": [],"
                +"       \"validation\": {"
                +"          \"name\": \"\","
                +"          \"message\": \"\","
                +"          \"data\": \"\","
                +"          \"useDefaultBlockList\": false"
                +"       },"
                +"       \"enabled\": true,"
                +"       \"hidden\": false,"
                +"       \"defaultValue\": \"\","
                +"       \"isSmartField\": false,"
                +"       \"unselectedLabel\": \"\","
                +"       \"placeholder\": \"\""
                +"    } ],"
                +"    \"default\": true,"
                +"    \"isSmartGroup\": false"
                +" },"
                +" {"
                +"    \"fields\": ["
                +"    {"
                +"       \"name\": \"email\","
                +"       \"label\": \"Email\","
                +"       \"type\": \"string\","
                +"       \"fieldType\": \"text\","
                +"       \"description\": \"\","
                +"       \"groupName\": \"contactinformation\","
                +"       \"displayOrder\": -1,"
                +"       \"required\": true,"
                +"       \"selectedOptions\": [],"
                +"       \"options\": [],"
                +"       \"validation\": {"
                +"          \"name\": \"email\","
                +"          \"message\": \"Please enter a valid email address\","
                +"          \"data\": \"\","
                +"          \"useDefaultBlockList\": false"
                +"       },"
                +"       \"enabled\": true,"
                +"       \"hidden\": false,"
                +"       \"defaultValue\": \"\","
                +"       \"isSmartField\": false,"
                +"       \"unselectedLabel\": \"\","
                +"       \"placeholder\": \"\""
                +"    } ],"
                +"    \"default\": true,"
                +"    \"isSmartGroup\": false"
                +" } ],"
                +" \"createdAt\": 1430010213580,"
                +" \"updatedAt\": 1430010886071,"
                +" \"metaData\": [],"
                +" \"deletable\": true"
                +"}";
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

    private String formDetails()
    {
        return   "[ {"
                +"     \"guid\":\"0033cf74de6a48c4ac5d805d72d69822\","
                +"     \"name\":\"Dummy UnitTest Form [6bd6532d-2a9b-49] (Leads API)\","
                +"     \"action\":\"\","
                +"     \"method\":\"PUT\","
                +"     \"cssClass\":\"\","
                +"     \"redirect\":\"\","
                +"     \"fields\":["
                +"     {"
                +"        \"name\":\"firstname\","
                +"        \"label\":\"First Name\","
                +"        \"type\":\"string\","
                +"        \"fieldType\":\"text\","
                +"        \"description\":\"\","
                +"        \"groupName\":\"\","
                +"        \"displayOrder\":0,"
                +"        \"required\":false,"
                +"        \"selectedOptions\":[ ],"
                +"        \"options\":[ ],"
                +"        \"validation\":"
                +"        {"
                +"           \"name\":\"\","
                +"           \"message\":\"\","
                +"           \"data\":\"\","
                +"           \"useDefaultBlockList\":false"
                +"        },"
                +"        \"enabled\":true,"
                +"        \"hidden\":false,"
                +"        \"defaultValue\":\"\","
                +"        \"isSmartField\":false,"
                +"        \"unselectedLabel\":\"\","
                +"        \"placeholder\":\"\","
                +"        \"dependentFieldFilters\":[ ]"
                +"     },"
                +"     {"
                +"        \"name\":\"lastname\","
                +"        \"label\":\"Last Name\","
                +"        \"type\":\"string\","
                +"        \"fieldType\":\"text\","
                +"        \"description\":\"\","
                +"        \"groupName\":\"\","
                +"        \"displayOrder\":1,"
                +"        \"required\":false,"
                +"        \"selectedOptions\":[ ],"
                +"        \"options\":[ ],"
                +"        \"validation\":{"
                +"           \"name\":\"\","
                +"           \"message\":\"\","
                +"           \"data\":\"\","
                +"           \"useDefaultBlockList\":false"
                +"        },"
                +"        \"enabled\":true,"
                +"        \"hidden\":false,"
                +"        \"defaultValue\":\"\","
                +"        \"isSmartField\":false,"
                +"        \"unselectedLabel\":\"\","
                +"        \"placeholder\":\"\","
                +"        \"dependentFieldFilters\":[ ]"
                +"     },"
                +"     {"
                +"        \"name\":\"adress_1\","
                +"        \"label\":\"Adress 1\","
                +"        \"type\":\"string\","
                +"        \"fieldType\":\"text\","
                +"        \"description\":\"\","
                +"        \"groupName\":\"\","
                +"        \"displayOrder\":2,"
                +"        \"required\":false,"
                +"        \"selectedOptions\":[ ],"
                +"        \"options\":[ ],"
                +"        \"validation\":"
                +"        {"
                +"           \"name\":\"\","
                +"           \"message\":\"\","
                +"           \"data\":\"\","
                +"           \"useDefaultBlockList\":false"
                +"        },"
                +"        \"enabled\":true,"
                +"        \"hidden\":false,"
                +"        \"defaultValue\":\"\","
                +"        \"isSmartField\":false,"
                +"        \"unselectedLabel\":\"\","
                +"        \"placeholder\":\"\","
                +"        \"dependentFieldFilters\":[ ]"
                +"     } ],"
                +"     \"submitText\":\"Submit\","
                +"     \"followUpId\":\"\","
                +"     \"notifyRecipients\":\"\","
                +"     \"leadNurturingCampaignId\":\"\","
                +"     \"formFieldGroups\":["
                +"     {"
                +"        \"name\":\"group-0\","
                +"        \"fieldNames\":[ \"firstname\" ],"
                +"        \"default\":true"
                +"     },"
                +"        {"
                +"        \"name\":\"group-1\","
                +"        \"fieldNames\":[ \"lastname\" ],"
                +"        \"default\":true"
                +"     },"
                +"     {"
                +"        \"name\":\"group-2\","
                +"        \"fieldNames\":[ \"adress_1\" ],"
                +"        \"default\":true"
                +"     } ],"
                +"     \"createdAt\":1318534279910,"
                +"     \"updatedAt\":1433967918640,"
                +"     \"performableHtml\":\"\","
                +"     \"migratedFrom\":\"ld\","
                +"     \"ignoreCurrentValues\":false,"
                +"     \"metaData\":[ ],"
                +"     \"deletable\":true,"
                +"     \"inlineMessage\":\"\","
                +"     \"socialLoginEnabled\":false,"
                +"     \"socialLoginTypes\":[ ],"
                +"     \"tmsId\":\"\","
                +"     \"captchaEnabled\":false,"
                +"     \"campaignGuid\":\"\","
                +"     \"embeddedCode\":\"<script charset=\\\"utf-8\\\" src=\\\"http://js.hubspot.com/forms/current.js\\\"></script>\n<script>\n hbspt.forms.create({\n portalId: '62515',\n formId: '0033cf74de6a48c4ac5d805d72d69822'\n });\n </script>\""
                +"  },"
                +"  {"
                +"     \"guid\":\"012d1150-2f7a-4b36-83e3-a0b33a463ed4\","
                +"     \"name\":\"UC: 1, Lead Gen - Education\","
                +"     \"action\":\"\","
                +"     \"method\":\"POST\","
                +"     \"cssClass\":\"hs-form stacked\","
                +"     \"redirect\":\"\","
                +"     \"fields\":["
                +"     {"
                +"        \"name\":\"firstname\","
                +"        \"label\":\"First Name\","
                +"        \"type\":\"string\","
                +"        \"fieldType\":\"text\","
                +"        \"description\":\"\","
                +"        \"groupName\":\"contactinformation\","
                +"        \"displayOrder\":-1,"
                +"        \"required\":false,"
                +"        \"selectedOptions\":[ ],"
                +"        \"options\":[ ],"
                +"        \"validation\":"
                +"        {"
                +"           \"name\":\"\","
                +"           \"message\":\"\","
                +"           \"data\":\"\","
                +"           \"useDefaultBlockList\":false"
                +"        },"
                +"        \"enabled\":true,"
                +"        \"hidden\":false,"
                +"        \"defaultValue\":\"\","
                +"        \"isSmartField\":false,"
                +"        \"unselectedLabel\":\"\","
                +"        \"placeholder\":\"\","
                +"        \"dependentFieldFilters\":[ ]"
                +"     },"
                +"     {"
                +"        \"name\":\"lastname\","
                +"        \"label\":\"Last Name\","
                +"        \"type\":\"string\","
                +"        \"fieldType\":\"text\","
                +"        \"description\":\"\","
                +"        \"groupName\":\"contactinformation\","
                +"        \"displayOrder\":-1,"
                +"        \"required\":false,"
                +"        \"selectedOptions\":[ ],"
                +"        \"options\":[ ],"
                +"        \"validation\":{"
                +"           \"name\":\"\","
                +"           \"message\":\"\","
                +"           \"data\":\"\","
                +"           \"useDefaultBlockList\":false"
                +"        },"
                +"        \"enabled\":true,"
                +"        \"hidden\":false,"
                +"        \"defaultValue\":\"\","
                +"        \"isSmartField\":false,"
                +"        \"unselectedLabel\":\"\","
                +"        \"placeholder\":\"\","
                +"        \"dependentFieldFilters\":[ ]"
                +"     },"
                +"     {"
                +"        \"name\":\"email\","
                +"        \"label\":\"Email\","
                +"        \"type\":\"string\","
                +"        \"fieldType\":\"text\","
                +"        \"description\":\"\","
                +"        \"groupName\":\"contactinformation\","
                +"        \"displayOrder\":-1,"
                +"        \"required\":true,"
                +"        \"selectedOptions\":[ ],"
                +"        \"options\":[ ],"
                +"        \"validation\":{"
                +"           \"name\":\"email\","
                +"           \"message\":\"Please enter a valid email address\","
                +"           \"data\":\"\","
                +"           \"useDefaultBlockList\":false"
                +"        },"
                +"        \"enabled\":true,"
                +"        \"hidden\":false,"
                +"        \"defaultValue\":\"\","
                +"        \"isSmartField\":false,"
                +"        \"unselectedLabel\":\"\","
                +"        \"placeholder\":\"\","
                +"        \"dependentFieldFilters\":[ ]"
                +"     },"
                +"     {"
                +"        \"name\":\"book2meet_company_phone\","
                +"        \"label\":\"Phone number\","
                +"        \"type\":\"string\","
                +"        \"fieldType\":\"text\","
                +"        \"description\":\"\","
                +"        \"groupName\":\"\","
                +"        \"displayOrder\":-1,"
                +"        \"required\":false,"
                +"        \"selectedOptions\":[ ],"
                +"        \"options\":[ ],"
                +"        \"validation\":{"
                +"           \"name\":\"\","
                +"           \"message\":\"\","
                +"           \"data\":\"\","
                +"           \"useDefaultBlockList\":false"
                +"        },"
                +"        \"enabled\":true,"
                +"        \"hidden\":false,"
                +"        \"defaultValue\":\"\","
                +"        \"isSmartField\":false,"
                +"        \"unselectedLabel\":\"\","
                +"        \"placeholder\":\"\","
                +"        \"dependentFieldFilters\":[ ]"
                +"     },"
                +"     {"
                +"        \"name\":\"current_education_level\","
                +"        \"label\":\"Current Education Level\","
                +"        \"type\":\"string\","
                +"        \"fieldType\":\"text\","
                +"        \"description\":\"\","
                +"        \"groupName\":\"\","
                +"        \"displayOrder\":-1,"
                +"        \"required\":false,"
                +"        \"selectedOptions\":[ ],"
                +"        \"options\":[ ],"
                +"        \"validation\":{"
                +"           \"name\":\"\","
                +"           \"message\":\"\","
                +"           \"data\":\"\","
                +"           \"useDefaultBlockList\":false"
                +"        },"
                +"        \"enabled\":true,"
                +"        \"hidden\":false,"
                +"        \"defaultValue\":\"\","
                +"        \"isSmartField\":false,"
                +"        \"unselectedLabel\":\"\","
                +"        \"placeholder\":\"\","
                +"        \"dependentFieldFilters\":[ ]"
                +"     } ],"
                +"  \"submitText\":\"Submit\","
                +"  \"followUpId\":\"\","
                +"  \"notifyRecipients\":\"\","
                +"  \"leadNurturingCampaignId\":\"\","
                +"  \"formFieldGroups\":["
                +"  {"
                +"     \"name\":\"group-0\","
                +"     \"fieldNames\":[ \"firstname\" ],"
                +"     \"default\":true"
                +"  },"
                +"  {"
                +"     \"name\":\"group-1\","
                +"     \"fieldNames\":[ \"lastname\" ],"
                +"     \"default\":true"
                +"  },"
                +"  {"
                +"     \"name\":\"group-2\","
                +"     \"fieldNames\":[ \"email\" ],"
                +"     \"default\":true"
                +"  },"
                +"  {"
                +"     \"name\":\"group-3\","
                +"     \"fieldNames\":[ \"book2meet_company_phone\" ],"
                +"     \"default\":true"
                +"  },"
                +"  {"
                +"     \"name\":\"group-4\","
                +"     \"fieldNames\":[ \"current_education_level\" ],"
                +"     \"default\":true"
                +"  } ],"
                +"  \"createdAt\":1438642147748,"
                +"  \"updatedAt\":1438642234188,"
                +"  \"performableHtml\":\"\","
                +"  \"migratedFrom\":\"\","
                +"  \"ignoreCurrentValues\":false,"
                +"  \"metaData\":[ ],"
                +"  \"deletable\":true,"
                +"  \"inlineMessage\":\"\","
                +"  \"socialLoginEnabled\":false,"
                +"  \"socialLoginTypes\":[ \"FACEBOOK\", \"LINKEDIN\", \"GOOGLEPLUS\" ],"
                +"  \"tmsId\":\"\","
                +"  \"captchaEnabled\":false,"
                +"  \"campaignGuid\":\"\","
                +"  \"embeddedCode\":\"<script charset=\\\"utf-8\\\" src=\\\"http://js.hubspot.com/forms/current.js\\\"></script>\n<script>\n hbspt.forms.create({\n portalId: '62515',\n formId: '012d1150-2f7a-4b36-83e3-a0b33a463ed4'\n });\n </script>\""
                +"} ]";
    }
}
