package com.smartling.connector.hubspot.sdk.it;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotFormsClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.form.FormDetail;
import com.smartling.connector.hubspot.sdk.form.FormFilter;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.jsonassert.JsonAssert.with;
import static com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager.createTokenProvider;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class FormsIntegrationTest extends BaseIntegrationTest
{
    private static final String BASIC_FORM_NAME        = "Default Form (Sample)";
    private static final String UPDATE_FORM_NAME       = "TEST UPDATE SUPER FORM UPDATED";
    private static final String UPDATE_FORM_METHOD     = "PUT";
    private static final String UPDATE_FORM_SUBMITTEXT = "Apply";

    private static final String NAME                   = "name";
    private static final String METHOD                 = "method";
    private static final String SUBMITTEXT             = "submitText";
    private static final String ROOT_PATH              = "$.";
    private static final String NAME_PATH              = ROOT_PATH + NAME;
    private static final String METHOD_PATH            = ROOT_PATH + METHOD;
    private static final String SUBMITTEXT_PATH        = ROOT_PATH + SUBMITTEXT;
    private static final String ID_PATH                = "$.guid";

    private HubspotFormsClient hubspotClient;
    private List<String>  formsToDelete          = new ArrayList<>();

    private String basicFormId;

    @Before
    public void init()
    {
        final Configuration configuration = Configuration.build(clientId, clientSecret, redirectUri, refreshToken);
        hubspotClient = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getFormsClient();
        basicFormId = System.getProperty("hubspot.basicFormId");
    }

    @After
    public void deleteTestForms()
    {
        for (String formId : formsToDelete)
        {
            try
            {
                hubspotClient.delete(formId);
            }
            catch (HubspotApiException e)
            {
                System.err.printf("Fail to clean up form '%1$s', cause '%2$s'", formId, e);
            }
        }
    }

    @Test
    public void shouldReturnForm() throws Exception
    {
        String formAsJson = hubspotClient.getFormContentById(basicFormId);
        with(formAsJson)
                .assertThat(NAME_PATH, equalTo(BASIC_FORM_NAME), "Name should have particular text")
                .assertThat(METHOD_PATH, not(isEmptyOrNullString()), "Method should have particular text")
                .assertThat(SUBMITTEXT_PATH, equalTo("Submit"), "Submit should have particular text")
                .assertThat(ID_PATH, equalTo(basicFormId), "Form id should have particular value");
    }

    @Test
    public void shouldReturnFormDetail() throws Exception
    {
        FormDetail formDetail = hubspotClient.getFormDetailById(basicFormId);

        assertThat(formDetail.getGuid()).isEqualTo(basicFormId);
        assertThat(formDetail.getName()).isEqualTo(BASIC_FORM_NAME);
        assertFormDetailIsNotEmpty(formDetail);
    }

    @Test
    public void shouldListForms() throws Exception
    {
        List<FormDetail> formDetails = hubspotClient.listForms(0, 50, new FormFilter(), null);
        assertThat(formDetails).overridingErrorMessage("Form details list should not be null").isNotNull();
        assertThat(formDetails.size()).overridingErrorMessage("Form details count should not be positive").isPositive();

        formDetails.forEach(this::assertFormDetailIsNotEmpty);
    }

    private void assertFormDetailIsNotEmpty(FormDetail formDetail)
    {
        assertThat(formDetail.getGuid()).isNotEmpty();
        assertThat(formDetail.getName()).isNotEmpty();
        assertThat(formDetail.getUpdated()).isNotNull();
    }

    private Pair<String, String> getClone() throws HubspotApiException
    {
        FormDetail clonedForm = hubspotClient.cloneForm(basicFormId, randomAlphabetic(12));
        String guid = clonedForm.getGuid();
        formsToDelete.add(guid);
        String content = hubspotClient.getFormContentById(guid);
        return new ImmutablePair<>(guid, content);
    }

    @Test(expected = HubspotApiException.class)
    public void shouldThrowExceptionIfAuthorizationFailed() throws HubspotApiException
    {
        final Configuration configuration = Configuration.build("wrong-client-id", "wrong-client-secret", "wrong-redirect-uri" ,"wrong-token");
        HubspotFormsClient client = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getFormsClient();
        client.listForms(0, 50, new FormFilter(), null);
    }

    @Test
    public void shouldCloneForm() throws Exception
    {
        String clonedFormName = randomAlphabetic(12);
        FormDetail clonedForm = hubspotClient.cloneForm(basicFormId, clonedFormName);
        formsToDelete.add(clonedForm.getGuid());

        assertThat(clonedForm.getGuid()).isNotEqualTo(basicFormId);
        assertThat(clonedForm.getName()).isEqualTo(clonedFormName);
        assertFormDetailIsNotEmpty(clonedForm);
    }

    @Test
    public void shouldUpdateForm() throws Exception
    {
        Pair<String, String> clonedForm = getClone();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(clonedForm.getValue()).getAsJsonObject();
        obj.addProperty(NAME, UPDATE_FORM_NAME);
        obj.addProperty(METHOD, UPDATE_FORM_METHOD);
        obj.addProperty(SUBMITTEXT, UPDATE_FORM_SUBMITTEXT);

        String updatedFormContent = hubspotClient.updateFormContent(clonedForm.getKey(), obj.toString());

        with(updatedFormContent)
            .assertThat(NAME_PATH, equalTo(UPDATE_FORM_NAME), "Name should have particular text")
            .assertThat(METHOD_PATH, equalTo(UPDATE_FORM_METHOD), "Method should have particular text")
            .assertThat(SUBMITTEXT_PATH, equalTo(UPDATE_FORM_SUBMITTEXT), "Submit should have particular text")
            .assertThat(ID_PATH, equalTo(clonedForm.getKey()), "Form id should have particular value");
    }

    @Test
    public void shouldDeleteForm() throws Exception
    {
        Pair<String, String> clonedForm = getClone();

        ResultInfo deleteFormInfo = hubspotClient.delete(clonedForm.getKey());
        assertThat(deleteFormInfo.isSucceeded()).isTrue();

        formsToDelete.remove(clonedForm.getKey());
    }
}
