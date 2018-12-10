package com.smartling.connector.hubspot.sdk.blog;

import lombok.Data;

@Data
public class BlogPostFilter
{
    private String blogId;
    private String postName;
    private Boolean archived;
    private String campaign;
    private String slug;
    private State state;

    public enum State {
        DRAFT, PUBLISHED, SCHEDULED
    }
}
