package com.smartling.connector.hubspot.sdk.blog;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BlogPostDetails
{
    int totalCount;

    @SerializedName("objects")
    List<BlogPostDetail> detailList = new ArrayList<>();

    public List<BlogPostDetail> getDetailList()
    {
        return detailList;
    }

    public void setDetailList(final List<BlogPostDetail> detailList)
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
