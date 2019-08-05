package com.smartling.connector.hubspot.sdk.form;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.smartling.connector.hubspot.sdk.NameAware;

public class FormDetail implements NameAware
{
    private String guid;
    private String name;
    @SerializedName("submitText")
    private String submitText;
    @SerializedName("tmsId")
    private String tmsId;
    @SerializedName("updatedAt")
    private Date   updated;
    @SerializedName("formType")
    private FormType formType;
    @SerializedName("isPublished")
    private boolean isPublished;

    public String getGuid()
    {
        return guid;
    }
    public void setGuid(String guid)
    {
        this.guid = guid;
    }
    @Override
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getSubmitText()
    {
        return submitText;
    }
    public void setSubmitText(String submitText)
    {
        this.submitText = submitText;
    }
    public Date getUpdated()
    {
        return updated;
    }
    public void setUpdated(Date updated)
    {
        this.updated = updated;
    }
    public String getTmsId()
    {
        return tmsId;
    }
    public void setTmsId(String tmsId)
    {
        this.tmsId = tmsId;
    }
    public FormType getFormType() { return formType; }
    public void setFormType(FormType formType) { this.formType = formType; }
    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }
}
