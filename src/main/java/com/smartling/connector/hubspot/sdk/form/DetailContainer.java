package com.smartling.connector.hubspot.sdk.form;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DetailContainer<T>
{
    @SerializedName("total_count")
    int totalCount;

    @SerializedName("objects")
    List<T> detailList = new ArrayList<>();

    public List<T> getDetailList()
    {
        return detailList;
    }

    public void setDetailList(final List<T> detailList)
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
