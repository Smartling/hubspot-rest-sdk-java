package com.smartling.connector.hubspot.sdk.v3.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLanguageVariationRequest
{
    private String id;
    private String language;
    private String primaryLanguage;
}
