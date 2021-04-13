package com.smartling.connector.hubspot.sdk.blog;

import lombok.Data;
import lombok.NoArgsConstructor;

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
}
