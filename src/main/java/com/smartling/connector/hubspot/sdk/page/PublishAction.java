package com.smartling.connector.hubspot.sdk.page;

import com.google.gson.annotations.SerializedName;
import com.smartling.connector.hubspot.sdk.rest.IgnoreCaseInsensitiveEnumSerialization;

@IgnoreCaseInsensitiveEnumSerialization
public enum PublishAction
{
    @SerializedName("push-buffer-live")
    PUSH_BUFFER_LIVE,

    @SerializedName("schedule-publish")
    SCHEDULE_PUBLISH,

    @SerializedName("cancel-publish")
    CANCEL_PUBLISH
}
