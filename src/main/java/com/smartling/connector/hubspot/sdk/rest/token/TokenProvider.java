package com.smartling.connector.hubspot.sdk.rest.token;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;

public interface TokenProvider
{
    RefreshTokenData getTokenData() throws HubspotApiException;
}
