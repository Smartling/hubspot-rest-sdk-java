package com.smartling.connector.hubspot.sdk.v3.page;

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
    @SerializedName("total")
    private int totalCount;

    @SerializedName("results")
    private List<T> detailList = new ArrayList<>();
}
