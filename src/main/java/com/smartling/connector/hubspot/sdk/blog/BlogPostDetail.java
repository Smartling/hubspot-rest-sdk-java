package com.smartling.connector.hubspot.sdk.blog;

import com.smartling.connector.hubspot.sdk.common.CurrentState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class BlogPostDetail
{
    public static final String FIELDS = "contentGroupId,id,metaDescription,name,postBody,postSummary,publishImmediately,slug,htmlTitle,rssSummary,featuredImageAltText,translatedContent,currentState";

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
    CurrentState currentState;
    Map<String, BlogPostDetail> translatedContent = new HashMap<>();
}
