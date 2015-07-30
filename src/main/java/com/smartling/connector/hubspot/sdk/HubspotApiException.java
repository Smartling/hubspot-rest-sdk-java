package com.smartling.connector.hubspot.sdk;

public class HubspotApiException extends Exception
{
    public HubspotApiException(final String message, final Exception cause)
    {
        super(message, cause);
    }

    public HubspotApiException(String message)
    {
        super(message);
    }
}
