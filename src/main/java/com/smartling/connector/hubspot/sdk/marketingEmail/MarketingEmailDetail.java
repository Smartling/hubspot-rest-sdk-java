package com.smartling.connector.hubspot.sdk.marketingEmail;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.gson.annotations.JsonAdapter;
import com.smartling.connector.hubspot.sdk.NameAware;
import com.smartling.connector.hubspot.sdk.serialization.RawJsonAdapter;
import com.smartling.connector.hubspot.sdk.serialization.RawJsonDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MarketingEmailDetail implements NameAware
{
    String id;

    @JsonAdapter(RawJsonAdapter.class)
    @JsonDeserialize(using = RawJsonDeserializer.class)
    String flexAreas;

    String fromName;
    List<Integer> mailingListsIncluded;
    String name;
    String metaDescription;
    boolean publishImmediately = true;
    String replyTo;
    String slug;
    String subject;
    String templatePath;

    @JsonAdapter(RawJsonAdapter.class)
    @JsonDeserialize(using = RawJsonDeserializer.class)
    String widgets;
}
