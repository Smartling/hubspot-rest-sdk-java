package com.smartling.connector.hubspot.sdk.v3.email;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class EmailDetail
{
    public static final String INCLUDED_PROPERTIES = "includedProperties=state&includedProperties=updatedAte";

    private String id;

    @JsonProperty("isAb")
    private boolean ab;

    private EmailState state;

    private String type;

    private Date updatedAt;
}
