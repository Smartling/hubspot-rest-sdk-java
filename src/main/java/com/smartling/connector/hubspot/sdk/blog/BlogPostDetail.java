package com.smartling.connector.hubspot.sdk.blog;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlogPostDetail
{
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
    String featuredImage;
    String featuredImageAltText;
}
