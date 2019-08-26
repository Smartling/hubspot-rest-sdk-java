package com.smartling.connector.hubspot.sdk.email;

import com.smartling.connector.hubspot.sdk.NameAware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetail implements NameAware
{
    private String fromName;
    private String htmlTitle;
    private String id;
    private String name;
    private boolean publishImmediately;
    private String subject;
}
