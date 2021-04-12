package com.smartling.connector.hubspot.sdk.common;

import com.google.gson.annotations.SerializedName;

public enum PublishAction
{
    @SerializedName("push-buffer-live")
    PUSH_BUFFER_LIVE,

    @SerializedName("schedule-publish")
    SCHEDULE_PUBLISH,

    @SerializedName("cancel-publish")
    CANCEL_PUBLISH
}
