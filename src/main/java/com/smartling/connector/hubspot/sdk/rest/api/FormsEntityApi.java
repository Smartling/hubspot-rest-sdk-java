package com.smartling.connector.hubspot.sdk.rest.api;

import java.util.List;

import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.form.FormDetail;

import feign.Param;
import feign.RequestLine;

public interface FormsEntityApi
{
    @RequestLine("GET /forms/v2/forms?formTypes={formType}&name__icontains={name}&order={order}&offset={offset}&limit={limit}")
    List<FormDetail> forms(@Param("formType") String formType, @Param("name") String name,
                           @Param("offset") int offset, @Param("limit") int limit,
                           @Param("order") String orderBy);

    @RequestLine("GET /forms/v2/forms/{form_guid}")
    FormDetail formDetail(@Param("form_guid") String form_guid);

    @RequestLine("DELETE /forms/v2/forms/{form_guid}")
    ResultInfo delete(@Param("form_guid") String guid);
}
