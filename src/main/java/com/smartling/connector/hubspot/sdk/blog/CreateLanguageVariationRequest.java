package com.smartling.connector.hubspot.sdk.blog;


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
    private String contentGroupId;
    private String translatedFromId;
    private String slug;
}
