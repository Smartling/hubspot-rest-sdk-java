package com.smartling.connector.hubspot.sdk.rest;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

public class HttpMockUtils
{
    public static StringValuePattern withFormParam(String key, String value)
    {
        return containing(key + "=" + value);
    }

    public static ResponseDefinitionBuilder aJsonResponse(String json)
    {
        return aResponse().withHeader("Content-Type", "application/json").withBody(json);
    }

    public static UrlPattern urlStartingWith(String path)
    {
        return urlMatching(path + ".*");
    }

    public static UrlPattern path(String path)
    {
        return urlMatching(path + "(\\?.+)?");
    }
}
