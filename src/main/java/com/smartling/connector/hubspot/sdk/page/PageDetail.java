package com.smartling.connector.hubspot.sdk.page;

import com.smartling.connector.hubspot.sdk.NameAware;

import java.util.Date;

public class PageDetail implements NameAware
{
    private long    id;
    private String  name;
    private String  htmlTitle;
    private String  abStatus;
    private boolean draft;
    private boolean archived;
    private Date    updated;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
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

    public String getHtmlTitle()
    {
        return htmlTitle;
    }

    public void setHtmlTitle(String htmlTitle)
    {
        this.htmlTitle = htmlTitle;
    }

    public String getAbStatus()
    {
        return abStatus;
    }

    public void setAbStatus(String abStatus)
    {
        this.abStatus = abStatus;
    }
    
    public boolean isAbTest() {
        return abStatus != null;
    }

    public boolean isDraft()
    {
        return draft;
    }

    public void setDraft(boolean draft)
    {
        this.draft = draft;
    }

    public boolean isArchived()
    {
        return archived;
    }

    public void setArchived(boolean archived)
    {
        this.archived = archived;
    }

    public Date getUpdated()
    {
        return updated;
    }

    public void setUpdated(Date updated)
    {
        this.updated = updated;
    }

    @Override
    public String toString()
    {
        return "PageDetail [id=" + id + ", name=" + name + ", htmlTitle=" + htmlTitle + ", abStatus=" + abStatus
                + ", draft=" + draft + ", archived=" + archived + ", updated=" + updated + "]";
    }
}
