package com.smartling.connector.hubspot.sdk.email;

import lombok.Data;

@Data
public class EmailFilter {
    private String emailName;
    private Boolean archived;
    private String campaign;
}
