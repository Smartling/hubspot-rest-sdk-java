package com.smartling.connector.hubspot.sdk.blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogTagDetail {
    private Instant created;
    private Instant deletedAt;
    private String id;
    private String language;
    private String name;
    private String slug;
    private Instant updated;
    private Long translatedFromId;
}
