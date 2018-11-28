package com.smartling.connector.hubspot.sdk.blog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartling.connector.hubspot.sdk.NameAware;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class BlogPostDetail implements NameAware
{
    String htmlTitle;
    String label;
    String metaDescription;
    String pageTitle;
    String postBody;
    String postBodyRss;
    String postSummary;
    String postSummaryRss;
    String postEmailContent;
    String postListContent;
    String postRssContent;
    String id;
    String name;
    Long contentGroupId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Date updated;
}
