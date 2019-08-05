package com.smartling.connector.hubspot.sdk.form;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.smartling.connector.hubspot.sdk.NameAware;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FormDetail implements NameAware
{
    private String guid;
    private String name;
    @SerializedName("submitText")
    private String submitText;
    @SerializedName("updatedAt")
    private Date   updated;
    @SerializedName("formType")
    private String formType;
    @SerializedName("isPublished")
    private boolean isPublished;
}
