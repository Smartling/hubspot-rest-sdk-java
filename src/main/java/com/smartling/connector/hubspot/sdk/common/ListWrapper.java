package com.smartling.connector.hubspot.sdk.common;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListWrapper<T>
{
    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("objects")
    private List<T> detailList = new ArrayList<>();
}
