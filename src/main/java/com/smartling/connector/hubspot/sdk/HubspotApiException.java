package com.smartling.connector.hubspot.sdk;

import feign.FeignException;

public class HubspotApiException extends Exception
{
    private int responseCode;

    public HubspotApiException(final String message, final FeignException ex)
    {
        super(String.format("%s, \n responseCode=%s, responseBody=\"%s\"", message, ex.status(), ex.content() != null ? ex.contentUTF8() : null), ex);
        this.responseCode = ex.status();
    }

    public HubspotApiException(final String message, final Exception cause)
    {
        super(message, cause);
    }

    public HubspotApiException(String message)
    {
        super(message);
    }

    public int getResponseCode()
    {
        return responseCode;
    }
}
