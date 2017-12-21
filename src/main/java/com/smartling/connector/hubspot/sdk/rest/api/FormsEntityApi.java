package com.smartling.connector.hubspot.sdk.rest.api;

import java.util.List;

import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.form.FormDetail;

import feign.Param;
import feign.RequestLine;

public interface FormsEntityApi
{
    @RequestLine("GET /forms/v2/forms")
    List<FormDetail> forms();

    @RequestLine("GET /forms/v2/forms/{form_guid}")
    FormDetail formDetail(@Param("form_guid") String form_guid);

    @RequestLine("DELETE /forms/v2/forms/{form_guid}")
    ResultInfo delete(@Param("form_guid") String guid);
}
