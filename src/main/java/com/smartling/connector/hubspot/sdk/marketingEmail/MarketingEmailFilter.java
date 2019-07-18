package com.smartling.connector.hubspot.sdk.marketingEmail;

import lombok.Data;

@Data
public class MarketingEmailFilter {
    private String contentGroupId;
    private String emailName;
    private Boolean archived;
    private String campaign;
}
