package com.smartling.connector.hubspot.sdk;

import java.util.List;

import com.smartling.connector.hubspot.sdk.form.FormDetail;

public interface HubspotFormClient extends HubspotClient
{
    List<FormDetail> listForms() throws HubspotApiException;
    String getFormContentById(String guid) throws HubspotApiException;
    FormDetail getFormDetailById(String guid) throws HubspotApiException;
    FormDetail cloneFormAsDetail(String guid) throws HubspotApiException;
    String updateFormContent(String guid, String content) throws HubspotApiException;
    List<FormDetail> listFormsByTmsId(final String tmsId) throws HubspotApiException;
    ResultInfo delete(String guid) throws HubspotApiException;
}
