package com.smartling.connector.hubspot.sdk;

import com.smartling.connector.hubspot.sdk.marketingEmail.MarketingEmailDetail;
import com.smartling.connector.hubspot.sdk.marketingEmail.MarketingEmailDetails;
import com.smartling.connector.hubspot.sdk.marketingEmail.MarketingEmailFilter;
import lombok.NonNull;

public interface HubspotMarketingEmailsClient extends HubspotClient
{
    MarketingEmailDetails listEmails(int offset, int limit, @NonNull MarketingEmailFilter filter, String orderBy) throws HubspotApiException;

    MarketingEmailDetail getEmailById(String id) throws HubspotApiException;

    MarketingEmailDetail createEmail(MarketingEmailDetail emailDetail) throws HubspotApiException;

    MarketingEmailDetail updateEmail(MarketingEmailDetail emailDetail) throws HubspotApiException;
}
