package com.smartling.connector.hubspot.sdk.email;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper=true)
@Data
@NoArgsConstructor
@SuperBuilder
public class EmailDetail extends EmailDetailShort
{
    public static final String FIELDS = EmailDetailShort.FIELDS +
            ",ab,abHoursToWait,abStatus,abSuccessMetric,abTestId,abTestPercentage,abVariation," +
            "authorName,currentState,fromName,htmlTitle,language," +
            "publishedByName,rssEmailByText,rssEmailClickThroughText,rssEmailCommentText,state,subject,subscriptionName";

    private boolean ab;
    private int abHoursToWait;
    private EmailAbStatus abStatus;
    private String abSuccessMetric;
    private long abTestId;
    private int abTestPercentage;
    private boolean abVariation;

    private String authorName;
    private EmailState currentState;
    private String fromName;
    private String htmlTitle;
    private String language;
    private String publishedByName;
    private String rssEmailByText;
    private String rssEmailClickThroughText;
    private String rssEmailCommentText;
    private EmailState state;
    private String subject;
    private String subscriptionName;

}
