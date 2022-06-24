package com.smartling.connector.hubspot.sdk.email;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@NoArgsConstructor
@SuperBuilder
public class EmailDetailShort
{
    public static final String FIELDS = "emailType,id,name,updated";
    private String emailType;
    private String id;
    private String name;
    private Date updated;
}
