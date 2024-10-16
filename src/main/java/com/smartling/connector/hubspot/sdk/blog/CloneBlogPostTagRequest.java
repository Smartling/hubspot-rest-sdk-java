package com.smartling.connector.hubspot.sdk.blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloneBlogPostTagRequest
{
    private String id;
    private String name;
    private String language;
    private String primaryLanguage;
}
