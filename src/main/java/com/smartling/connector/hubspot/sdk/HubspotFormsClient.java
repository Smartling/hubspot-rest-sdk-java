package com.smartling.connector.hubspot.sdk;

import java.util.List;

import com.smartling.connector.hubspot.sdk.form.FormDetail;
import com.smartling.connector.hubspot.sdk.form.FormFilter;
import lombok.NonNull;

public interface HubspotFormsClient extends HubspotClient
{
    List<FormDetail> listForms(int offset, int limit, @NonNull FormFilter filter, String orderBy) throws HubspotApiException;
    String getFormContentById(String guid) throws HubspotApiException;
    FormDetail getFormDetailById(String guid) throws HubspotApiException;
    FormDetail cloneFormAsDetail(String guid) throws HubspotApiException;
    String updateFormContent(String guid, String content) throws HubspotApiException;
    ResultInfo delete(String guid) throws HubspotApiException;
}
