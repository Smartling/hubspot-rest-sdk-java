package com.smartling.connector.hubspot.sdk.page;

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
    public static final String FIELDS = PageDetailShort.FIELDS + ",html_title,subcategory,campaign,campaign_name,url,folder_id,portal_id,translated_content";

    private String  htmlTitle;
    private Subcategory  subcategory; // This is set to empty or to "landing_page" for landing pages, or to "site_page" for site pages
    private String  campaign; // The guid of the marketing campaign this page is associated with
    private String  campaignName; // The name of the marketing campaign this page is associated with
    private String  url; // The full URL with domain and scheme to the page. Will return a 404 if the page is not yet published.
    private String  folderId; // ?????
    private String  portalId; // ?????
    private Map<String, PageDetailShort> translatedContent; // "uk-ua" -> "<shortened page json>"
}
