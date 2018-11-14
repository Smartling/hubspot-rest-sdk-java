package com.smartling.connector.hubspot.sdk.blog;

import com.smartling.connector.hubspot.sdk.NameAware;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class BlogPostDetail implements NameAware
{
    Long id;
    Long blogAuthorId;
    Long contentGroupId;
    String featuredImage;
    List<String> keywords;
    String name;
    String postBody;
    String postSummary;
    Date updated;
}
