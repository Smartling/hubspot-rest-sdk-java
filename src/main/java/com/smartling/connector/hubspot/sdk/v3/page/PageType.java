package com.smartling.connector.hubspot.sdk.v3.page;

public enum PageType
{
    SITE_PAGE("site-pages"),
    LANDING_PAGE("landing-pages");

    private String pathParam;

    PageType(String pathParam)
    {
        this.pathParam = pathParam;
    }

    public String getPathParam()
    {
        return pathParam;
    }
}
