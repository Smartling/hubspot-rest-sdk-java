package com.smartling.connector.hubspot.sdk;


public class PageSearchFilter
{
    private Integer offset;
    private Integer limit;
    private String campaign;
    private Boolean archived;
    private Boolean draft;
    private String name;
    
    public Integer getOffset()
    {
        return offset;
    }
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public String getCampaign()
    {
        return campaign;
    }
    public void setCampaign(String campaign)
    {
        this.campaign = campaign;
    }
    
    public Boolean getArchived()
    {
        return archived;
    }
    public void setArchived(Boolean archived)
    {
        this.archived = archived;
    }
    
    public Boolean getDraft()
    {
        return draft;
    }
    public void setDraft(Boolean draft)
    {
        this.draft = draft;
    }
    
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    
    @Override
    public String toString()
    {
        return "PageSearchFilter [offset=" + offset + ", limit=" + limit + ", campaign=" + campaign + ", archived="
                + archived + ", draft=" + draft + ", name=" + name + "]";
    }
}
