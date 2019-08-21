package com.smartling.connector.hubspot.sdk;

import feign.FeignException;

public class HubspotApiNotFoundException extends HubspotApiException
{
    public HubspotApiNotFoundException(String message, FeignException ex)
    {
        super(message, ex);
    }
}
