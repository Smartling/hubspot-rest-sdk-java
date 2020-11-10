package com.smartling.connector.hubspot.sdk.form;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class FormDetail
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
    @SerializedName("tmsId")
    private String tmsId;
}
