package com.smartling.connector.hubspot.sdk.email;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EmailDetails {
    int totalCount;

    @SerializedName("objects")
    List<EmailDetail> detailList = new ArrayList<>();

    public List<EmailDetail> getDetailList()
    {
        return detailList;
    }

    public void setDetailList(final List<EmailDetail> detailList)
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
