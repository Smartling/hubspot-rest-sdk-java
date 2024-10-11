package com.smartling.connector.hubspot.sdk.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class DomainDetail {
    private String id;
    private String domain;
    private String expectedCname;
    private String redirectTo;

    private Instant createdAt;
    private Instant updatedAt;

    private boolean isPrimaryLandingPage;
    private boolean isPrimaryEmail;
    private boolean isPrimaryBlogPost;
    private boolean isPrimarySitePage;
    private boolean isPrimaryKnowledge;
    private boolean isResolving;
    private boolean isManuallyMarkedAsResolving;
    private boolean isHttpsEnabled;
    private boolean isHttpsOnly;
    private boolean isUsedForBlogPost;
    private boolean isUsedForSitePage;
    private boolean isUsedForEmail;
    private boolean isUsedForKnowledge;
}
