package com.smartling.connector.hubspot.sdk.blog;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BlogDetails
{
    @SerializedName("total")
    int totalCount;

    @SerializedName("objects")
    List<BlogDetail> detailList = new ArrayList<>();

    public List<BlogDetail> getDetailList()
    {
        return detailList;
    }

    public void setDetailList(final List<BlogDetail> detailList)
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
