package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.domain.DomainDetails;

public interface HubspotDomainClient extends HubspotClient
{
   DomainDetails getDomainDetails(int limit, String after, String sort) throws HubspotApiException;
}
