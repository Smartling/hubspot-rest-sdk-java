package com.smartling.connector.hubspot.sdk.v3.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloneEmailRequest
{
    private String cloneName;
    private String language;
    private String id;
}
