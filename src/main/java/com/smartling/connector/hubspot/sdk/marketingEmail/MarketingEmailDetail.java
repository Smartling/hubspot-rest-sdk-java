package com.smartling.connector.hubspot.sdk.marketingEmail;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.gson.annotations.JsonAdapter;
import com.smartling.connector.hubspot.sdk.NameAware;
import com.smartling.connector.hubspot.sdk.serialization.RawJsonAdapter;
import com.smartling.connector.hubspot.sdk.serialization.RawJsonDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MarketingEmailDetail implements NameAware
{
    @JsonAdapter(RawJsonAdapter.class)
    @JsonDeserialize(using = RawJsonDeserializer.class)
    @JsonRawValue
    private String flexAreas;

    private String fromName;

    @JsonAdapter(RawJsonAdapter.class)
    @JsonDeserialize(using = RawJsonDeserializer.class)
    @JsonRawValue
    private String id;

    private String name;
    private boolean publishImmediately = true;
    private String subject;
    private String templatePath;

    @JsonAdapter(RawJsonAdapter.class)
    @JsonDeserialize(using = RawJsonDeserializer.class)
    @JsonRawValue
    private String widgets;
}
