package com.smartling.connector.hubspot.sdk.marketingEmail;

import com.google.gson.annotations.JsonAdapter;
import com.smartling.connector.hubspot.sdk.NameAware;
import com.smartling.connector.hubspot.sdk.serialization.RawJsonAdapter;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MarketingEmailDetail implements NameAware
{
    private String fromName;

    @JsonAdapter(RawJsonAdapter.class)
    private String id;

    private String name;
    private boolean publishImmediately = true;
    private String subject;

    @JsonAdapter(RawJsonAdapter.class)
    private String widgets;
}
