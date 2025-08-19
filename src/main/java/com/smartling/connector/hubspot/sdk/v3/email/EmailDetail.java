package com.smartling.connector.hubspot.sdk.v3.email;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class EmailDetail
{
    public static final String INCLUDED_PROPERTIES = "includedProperties=state&includedProperties=updatedAt&includedProperties=name";

    private String id;

    private EmailState state;

    private String name;

    private String type;

    private Date updatedAt;
}
