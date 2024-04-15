package com.smartling.connector.hubspot.sdk.v3.page;

import com.smartling.connector.hubspot.sdk.common.CurrentState;
import com.smartling.connector.hubspot.sdk.common.Subcategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDetail
{
    public static final String FIELDS = "id,name,currentState,archivedAt,createdAt,updatedAt,slug,language,htmlTitle,subcategory,campaign,campaignName,url,folderId,translations,translatedFromId";

    private String id;
    private String name;
    private CurrentState currentState;
    private Date archivedAt;
    private Date createdAt;
    private Date updatedAt;
    private String slug;
    private String language;
    private String htmlTitle;
    private Subcategory subcategory;
    private String campaign;
    private String campaignName;
    private String url;
    private String folderId;
    private String translatedFromId;
    private Map<String, PageTranslationDetail> translations; // "uk-ua" -> "<shortened page json>"
}
