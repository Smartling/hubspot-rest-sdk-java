package com.smartling.connector.hubspot.sdk.rest.api;

import java.util.List;
import java.util.Map;

import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.form.CloneFormRequest;
import com.smartling.connector.hubspot.sdk.form.FormDetail;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

public interface FormsEntityApi
{
    @RequestLine("GET /forms/v2/forms?formTypes={formType}&order={order}&offset={offset}&limit={limit}")
    List<FormDetail> forms(@Param("formType") String formType,
                           @Param("offset") int offset, @Param("limit") int limit,
                           @Param("order") String orderBy,
                           @QueryMap Map<String, Object> queryMap);

    @RequestLine("GET /forms/v2/forms/{form_guid}")
    @Headers("Content-Type: application/json")
    FormDetail formDetail(@Param("form_guid") String form_guid);

    @RequestLine("DELETE /forms/v2/forms/{form_guid}")
    ResultInfo delete(@Param("form_guid") String guid);

    @RequestLine("POST /forms/v2/forms/{form_guid}/clone")
    @Headers("Content-Type: application/json")
    FormDetail clone(@Param("form_guid") String form_guid, CloneFormRequest cloneFormRequest);
}
