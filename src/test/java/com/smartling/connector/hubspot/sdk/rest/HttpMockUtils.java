package com.smartling.connector.hubspot.sdk.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;

public class HttpMockUtils
{
    public static ValueMatchingStrategy withFormParam(String key, String value)
    {
        return containing(key + "=" + value);
    }

    public static ResponseDefinitionBuilder aJsonResponse(String json)
    {
        return aResponse().withHeader("Content-Type", "application/json").withBody(json);
    }

    public static UrlMatchingStrategy urlStartingWith(String path)
    {
        return urlMatching(path + ".*");
    }

    public static UrlMatchingStrategy path(String path)
    {
        return urlMatching(path + "(\\?.+)?");
    }
}
