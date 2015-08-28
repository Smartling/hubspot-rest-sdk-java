package com.smartling.connector.hubspot.sdk.it;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotFormClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.form.FormDetail;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager;

public class FormsIntegrationTest
{
    private static final String BASIC_FORM_ID          = "79c9090c-1587-4171-a6d1-54b54b7eec7c";
    private static final String BASIC_FORM_NAME        = "Default Form (Sample)";
    private static final String UPDATE_FORM_NAME       = "TEST UPDATE SUPER FORM";
    private static final String UPDATE_FORM_METHOD     = "PUT";
    private static final String UPDATE_FORM_SUBMITTEXT = "Apply";

    private static final String NAME                   = "name";
    private static final String METHOD                 = "method";
    private static final String SUBMITTEXT             = "submitText";
    private static final String TMS_ID                 = "tmsId";
    private static final String ROOT_PATH              = "$.";
    private static final String NAME_PATH              = ROOT_PATH + NAME;
    private static final String METHOD_PATH            = ROOT_PATH + METHOD;
    private static final String SUBMITTEXT_PATH        = ROOT_PATH + SUBMITTEXT;
    private static final String ID_PATH                = "$.guid";

    private HubspotFormClient hubspotClient;
    private String        tmsId                  = "Tms Id ";
    private List<String>  formsToDelete          = Lists.newArrayList();

    @Before
    public void checkRequiredProperties()
    {
        final String refreshToken = System.getProperty("hubspot.refreshToken");
        final String clientId = System.getProperty("hubspot.clientId");

        assertThat(refreshToken).overridingErrorMessage("Access token for Hubspot API is missing!").isNotEmpty();
        assertThat(clientId).overridingErrorMessage("Client id for Hubspot application is missing!").isNotEmpty();

        hubspotClient = new HubspotRestClientManager(Configuration.build(clientId, refreshToken)).getFormClient();
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
        String formAsJson = hubspotClient.getFormContentById(BASIC_FORM_ID);
        with(formAsJson)
                .assertThat(NAME_PATH, equalTo(BASIC_FORM_NAME), "Name should have particular text")
                .assertThat(METHOD_PATH, not(isEmptyOrNullString()), "Method should have particular text")
                .assertThat(SUBMITTEXT_PATH, equalTo("Submit"), "Submit should have particular text")
                .assertThat(ID_PATH, equalTo(BASIC_FORM_ID), "Form id should have particular value");
    }

    @Test
    public void shouldReturnFormDetail() throws Exception
    {
        FormDetail formDetail = hubspotClient.getFormDetailById(BASIC_FORM_ID);

        assertThat(formDetail.getGuid()).isEqualTo(BASIC_FORM_ID);
        assertThat(formDetail.getName()).isEqualTo(BASIC_FORM_NAME);
        assertFormDetailIsNotEmpty(formDetail);
    }

    @Test
    public void shouldListForms() throws Exception
    {
        List<FormDetail> formDetails = hubspotClient.listForms();
        assertThat(formDetails).overridingErrorMessage("Form details list should not be null").isNotNull();
        assertThat(formDetails.size()).overridingErrorMessage("Form details count should not be positive").isPositive();

        formDetails.stream().forEach(d -> assertFormDetailIsNotEmpty(d));
    }

    private void assertFormDetailIsNotEmpty(FormDetail formDetail)
    {
        assertThat(formDetail.getGuid()).isNotEmpty();
        assertThat(formDetail.getName()).isNotEmpty();
        assertThat(formDetail.getUpdated()).isNotNull();
    }

    @Test
    public void shouldListFormsByTmsId() throws Exception
    {
        Pair<String, String> clonedForm = getClone();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(clonedForm.getValue()).getAsJsonObject();
        obj.addProperty(TMS_ID, tmsId);
        hubspotClient.updateFormContent(clonedForm.getKey(), obj.toString());

        List<FormDetail> formDetails = hubspotClient.listFormsByTmsId(tmsId);

        assertThat(formDetails).overridingErrorMessage("Form details should not be empty").isNotEmpty();
        assertFormDetailIsNotEmpty(formDetails.get(0));
    }

    private Pair<String, String> getClone() throws HubspotApiException
    {
        FormDetail clonedForm = hubspotClient.cloneFormAsDetail(BASIC_FORM_ID);
        String guid = clonedForm.getGuid();
        formsToDelete.add(guid);
        String content = hubspotClient.getFormContentById(guid);
        return new ImmutablePair<String, String>(guid, content);
    }

    @Test(expected = HubspotApiException.class)
    public void shouldThrowExceptionIfAuthorizationFailed() throws HubspotApiException
    {
        HubspotFormClient client = new HubspotRestClientManager(Configuration.build("wrong-client-id", "wrong-token")).getFormClient();
        client.listForms();
    }

    @Test
    public void shouldCloneForm() throws Exception
    {
        FormDetail clonedForm = hubspotClient.cloneFormAsDetail(BASIC_FORM_ID);
        formsToDelete.add(clonedForm.getGuid());

        assertThat(clonedForm.getGuid()).isNotEqualTo(BASIC_FORM_ID);
        assertThat(clonedForm.getName()).isNotEqualTo(BASIC_FORM_NAME);
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
