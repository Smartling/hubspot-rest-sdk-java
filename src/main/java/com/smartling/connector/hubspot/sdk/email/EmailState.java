package com.smartling.connector.hubspot.sdk.email;

public enum EmailState
{
    // AB master states
    DRAFT_AB,
    PUBLISHED_AB,
    SCHEDULED_AB,

    // AB variant states
    DRAFT_AB_VARIANT,
    PUBLISHED_AB_VARIANT,
    SCHEDULED_AB_VARIANT,

    // Automated states
    AUTOMATED_DRAFT,
    AUTOMATED,

    // Batch states
    DRAFT,
    PUBLISHED,
    SCHEDULED,

    // Follow up states
    AUTOMATED_FOR_FORM_BUFFER,
    AUTOMATED_FOR_FORM_DRAFT,
    AUTOMATED_FOR_FORM,
    AUTOMATED_FOR_FORM_LEGACY,

    // RSS states
    RSS_TO_EMAIL_DRAFT,
    RSS_TO_EMAIL_PUBLISHED,

    // Other states
    PRE_PROCESSING,
    PROCESSING,
    ERROR
}
