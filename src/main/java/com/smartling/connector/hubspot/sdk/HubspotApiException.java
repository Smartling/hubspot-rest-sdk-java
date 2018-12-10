package com.smartling.connector.hubspot.sdk;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class HubspotApiException extends Exception
{
    @Getter
    private Response response;

    public HubspotApiException(final String message, final FeignException ex)
    {
        super(message, ex);
        response = new Response(ex.status(), ex.content() != null ? ex.contentUTF8() : null);
    }

    public HubspotApiException(final String message, final Exception cause)
    {
        super(message, cause);
    }

    public HubspotApiException(String message)
    {
        super(message);
    }

    @Data @AllArgsConstructor
    public static class Response {
        private int responseCode;
        private String responseBody;
    }
}
