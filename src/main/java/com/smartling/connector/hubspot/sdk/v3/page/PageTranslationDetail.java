package com.smartling.connector.hubspot.sdk.v3.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageTranslationDetail
{
    private String id;
    private String name;
    private TranslationState state;
    private Date createdAt;
    private Date updatedAt;
    private String slug;
}
