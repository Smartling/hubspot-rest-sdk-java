package com.smartling.connector.hubspot.sdk;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PageDetail
{
    private long id;

    @SerializedName("html_title")
    private String htmlTitle;

    private String name;
    private Date   updated;

    public String getHtmlTitle()
    {
        return htmlTitle;
    }

    public void setHtmlTitle(final String htmlTitle)
    {
        this.htmlTitle = htmlTitle;
    }

    public long getId()
    {
        return id;
    }

    public void setId(final long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Date getUpdated()
    {
        return updated;
    }

    public void setUpdated(final Date updated)
    {
        this.updated = updated;
    }
}
