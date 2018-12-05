package com.smartling.connector.hubspot.sdk.blog;

import com.smartling.connector.hubspot.sdk.NameAware;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class BlogPostDetail implements NameAware
{
    Long contentGroupId;
    String id;
    String metaDescription;
    String name;
    String postBody;
    String postSummary;
    boolean publishImmediately = true;
    String slug;
    Map<String, Map> widgets;
}
