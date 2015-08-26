package com.smartling.connector.hubspot.sdk.form;

import com.smartling.connector.hubspot.sdk.NameAware;

public class FormDetail implements NameAware
{
    private String guid;
    private String name;
    private String submitText;

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
}
