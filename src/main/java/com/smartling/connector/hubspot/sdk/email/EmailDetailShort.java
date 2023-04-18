package com.smartling.connector.hubspot.sdk.email;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class EmailDetailShort
{
    public static final String FIELDS = "emailType,id,name,updated,created";
    private String emailType;
    private String id;
    private String name;
    private Date updated;
    private Date created;
}
