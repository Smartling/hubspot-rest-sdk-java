package com.smartling.connector.hubspot.sdk.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLanguageVariationRequest
{
    private String name;
    private String language;
    private String masterLanguage;
}
