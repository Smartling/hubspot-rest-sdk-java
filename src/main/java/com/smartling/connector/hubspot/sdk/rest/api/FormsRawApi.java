package com.smartling.connector.hubspot.sdk.rest.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface FormsRawApi
{
    @RequestLine("GET /forms/v2/forms/{form_guid}")
    String form(@Param("form_guid") String guid);

    @RequestLine("POST /forms/v2/forms/{form_guid}")
    @Headers("Content-Type: application/json")
    String update(@Param("form_guid") String guid, String formAsJson);
}
