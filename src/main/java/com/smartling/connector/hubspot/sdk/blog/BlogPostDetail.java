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
    String id;
    Long blogAuthorId;
    Long contentGroupId;
    String name;
    String postBody;
    String postSummary;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Date updated;
}
