package com.smartling.connector.hubspot.sdk.rest.api;

import java.util.List;

import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.form.FormDetail;

import feign.Param;
import feign.RequestLine;

public interface FormsEntityApi
{
    @RequestLine("GET /forms/v2/forms?access_token={access_token}")
    List<FormDetail> forms(@Param("access_token") String accessToken);

    @RequestLine("GET /forms/v2/forms?access_token={access_token}&tms_id={tms_id}")
    List<FormDetail> findByTmsId(@Param("tms_id") String tmsId, @Param("access_token") String accessToken);

    @RequestLine("GET /forms/v2/forms/{form_guid}?access_token={access_token}")
    FormDetail formDetail(@Param("form_guid") String form_guid, @Param("access_token") String accessToken);

    @RequestLine("DELETE /forms/v2/forms/{form_guid}?access_token={access_token}")
    ResultInfo delete(@Param("form_guid") String guid, @Param("access_token") String accessToken);
}
