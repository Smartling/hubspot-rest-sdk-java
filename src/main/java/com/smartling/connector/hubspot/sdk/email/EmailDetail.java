package com.smartling.connector.hubspot.sdk.email;

import com.google.gson.annotations.SerializedName;
import com.smartling.connector.hubspot.sdk.NameAware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetail implements NameAware
{
    @SerializedName("authorName")
    private String authorName;
    @SerializedName("currentState")
    private String currentState;
    @SerializedName("emailType")
    private String emailType;
    @SerializedName("fromName")
    private String fromName;
    @SerializedName("htmlTitle")
    private String htmlTitle;
    private String id;
    private String language;
    private String name;
    @SerializedName("publishedByName")
    private String publishedByName;
    @SerializedName("rssEmailByText")
    private String rssEmailByText;
    @SerializedName("rssEmailClickThroughText")
    private String rssEmailClickThroughText;
    @SerializedName("rssEmailCommentText")
    private String rssEmailCommentText;
    private String subject;
    @SerializedName("subscriptionName")
    private String subscriptionName;
    private Date updated;
}
