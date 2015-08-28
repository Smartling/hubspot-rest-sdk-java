package com.smartling.connector.hubspot.sdk.rest.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface FormsRawApi
{
    @RequestLine("GET /forms/v2/forms/{form_guid}?access_token={access_token}")
    String form(@Param("form_guid") String guid, @Param("access_token") String accessToken);

    @RequestLine("POST /forms/v2/forms/?access_token={access_token}")
    @Headers("Content-Type: application/json")
    String create(String formAsJson, @Param("access_token") String accessToken);

    @RequestLine("POST /forms/v2/forms/{form_guid}?access_token={access_token}")
    @Headers("Content-Type: application/json")
    String update(@Param("form_guid") String guid, String formAsJson, @Param("access_token") String accessToken);
}
