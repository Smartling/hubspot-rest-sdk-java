package com.smartling.connector.hubspot.sdk.blog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class BlogDetail
{
    private String id;
    @SerializedName("name")
    private String title;
    private String slug;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date updated;
}
