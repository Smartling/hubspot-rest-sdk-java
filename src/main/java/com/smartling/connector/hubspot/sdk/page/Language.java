package com.smartling.connector.hubspot.sdk.page;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Language
{
    private String tag;
    @SerializedName("displayName")
    private String displayName;
}
