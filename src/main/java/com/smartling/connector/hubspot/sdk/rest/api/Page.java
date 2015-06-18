package com.smartling.connector.hubspot.sdk.rest.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import feign.Param;

public class Page
{
    private long id;

    @SerializedName("html_title")
    private String htmlTitle;

    @SerializedName("meta_description")
    private String metaDescription;

    @SerializedName("footer_html")
    private String footerHtml;

    @SerializedName("head_html")
    private String headHtml;

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

    public String getMetaDescription()
    {
        return metaDescription;
    }

    public void setMetaDescription(final String metaDescription)
    {
        this.metaDescription = metaDescription;
    }

    public String getFooterHtml()
    {
        return footerHtml;
    }

    public void setFooterHtml(final String footerHtml)
    {
        this.footerHtml = footerHtml;
    }

    public String getHeadHtml()
    {
        return headHtml;
    }

    public void setHeadHtml(final String headHtml)
    {
        this.headHtml = headHtml;
    }

    public static class Expander implements Param.Expander
    {
        @Override
        public String expand(final Object value)
        {
            return new Gson().toJson(value);
        }
    }
}
