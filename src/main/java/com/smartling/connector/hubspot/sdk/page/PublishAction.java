package com.smartling.connector.hubspot.sdk.page;

import com.google.gson.annotations.SerializedName;

public enum PublishAction
{
    @SerializedName(value = "push-buffer-live")
    PUSH_BUFFER_LIVE,

    @SerializedName(value = "schedule-publish")
    SCHEDULE_PUBLISH,

    @SerializedName(value = "cancel-publish")
    CANCEL_PUBLISH
}
