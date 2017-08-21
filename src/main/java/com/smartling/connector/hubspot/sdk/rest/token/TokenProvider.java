package com.smartling.connector.hubspot.sdk.rest.token;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;

public interface TokenProvider
{
    String TOKEN_PROVIDER_DECORATOR_CLASS = "smartling.hubspot.tokenprovider.decorator";

    RefreshTokenData getTokenData() throws HubspotApiException;
}
