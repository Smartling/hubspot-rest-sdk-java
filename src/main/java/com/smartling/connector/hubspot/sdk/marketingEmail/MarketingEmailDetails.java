package com.smartling.connector.hubspot.sdk.marketingEmail;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MarketingEmailDetails {
    int totalCount;

    @SerializedName("objects")
    List<MarketingEmailDetail> detailList = new ArrayList<>();

    public List<MarketingEmailDetail> getDetailList()
    {
        return detailList;
    }

    public void setDetailList(final List<MarketingEmailDetail> detailList)
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
