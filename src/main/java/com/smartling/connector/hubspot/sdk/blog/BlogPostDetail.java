package com.smartling.connector.hubspot.sdk.blog;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class BlogPostDetail
{
    public static final String FIELDS = "contentGroupId,id,metaDescription,name,postBody,postSummary,publishImmediately,slug,htmlTitle,rssSummary,featuredImageAltText";

    Long contentGroupId;
    String id;
    String metaDescription;
    String name;
    String postBody;
    String postSummary;
    boolean publishImmediately = true;
    String slug;
    String htmlTitle;
    String rssSummary;
    String featuredImageAltText;
    Map<String, BlogPostDetail> translatedContent = new HashMap<>();
}
