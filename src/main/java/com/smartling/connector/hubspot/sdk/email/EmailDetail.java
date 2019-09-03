package com.smartling.connector.hubspot.sdk.email;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailDetail extends EmailDetailShort
{
    public static final String FIELDS = EmailDetailShort.FIELDS +
            ",ab,abHoursToWait,abStatus,abTestId,abTestPercentage," +
            "authorName,currentState,fromName,htmlTitle,language," +
            "publishedByName,rssEmailByText,rssEmailClickThroughText,rssEmailCommentText,state,subject,subscriptionName";

    private boolean ab;
    private int abHoursToWait;
    private EmailAbStatus abStatus;
    private long abTestId;
    private int abTestPercentage;

    private String authorName;
    private String currentState;
    private String fromName;
    private String htmlTitle;
    private String language;
    private String publishedByName;
    private String rssEmailByText;
    private String rssEmailClickThroughText;
    private String rssEmailCommentText;
    private String state;
    private String subject;
    private String subscriptionName;

}
