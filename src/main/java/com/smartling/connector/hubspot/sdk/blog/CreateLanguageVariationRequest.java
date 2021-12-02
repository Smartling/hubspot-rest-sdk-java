package com.smartling.connector.hubspot.sdk.blog;


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
    private String contentGroupId;
}
