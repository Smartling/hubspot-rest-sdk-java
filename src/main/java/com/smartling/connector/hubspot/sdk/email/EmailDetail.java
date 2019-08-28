package com.smartling.connector.hubspot.sdk.email;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailDetail extends EmailDetailShort
{
    public static final String FIELDS = EmailDetailShort.FIELDS + ",authorName,fromName,htmlTitle,language," +
            "publishedByName,rssEmailByText,rssEmailClickThroughText,rssEmailCommentText,state,subject,subscriptionName";

    private String authorName;
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
