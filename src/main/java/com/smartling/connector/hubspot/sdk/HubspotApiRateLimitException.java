package com.smartling.connector.hubspot.sdk;

import feign.FeignException;

/**
 * Hubspot has rejected the call because an API rate limit (HTTP 429) is exceeded.
 */
public class HubspotApiRateLimitException extends HubspotApiException
{
    public HubspotApiRateLimitException(String message, FeignException ex)
    {
        super(message, ex);
    }
}
