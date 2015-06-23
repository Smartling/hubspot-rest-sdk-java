package com.smartling.connector.hubspot.sdk;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PageDetails
{
    @SerializedName("total_count")
    int totalCount;

    @SerializedName("objects")
    List<PageDetail> detailList = new ArrayList<>();

    public List<PageDetail> getDetailList()
    {
        return detailList;
    }

    public void setDetailList(final List<PageDetail> detailList)
    {
        this.detailList = detailList;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(final int totalCount)
    {
        this.totalCount = totalCount;
    }
}
