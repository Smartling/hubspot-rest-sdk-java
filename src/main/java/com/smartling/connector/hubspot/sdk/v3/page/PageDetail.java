package com.smartling.connector.hubspot.sdk.v3.page;

import com.smartling.connector.hubspot.sdk.common.Subcategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDetail extends PageDetailShort
{
    public static final String FIELDS = PageDetailShort.FIELDS + ",htmlTitle,subcategory,campaign,campaignName,url,folderId,translations,translatedFromId";

    private String  htmlTitle;
    private Subcategory subcategory;
    private String  campaign;
    private String  campaignName;
    private String  url;
    private String  folderId;
    private Long translatedFromId;
    private Map<String, PageDetailShort> translations; // "uk-ua" -> "<shortened page json>"
}
