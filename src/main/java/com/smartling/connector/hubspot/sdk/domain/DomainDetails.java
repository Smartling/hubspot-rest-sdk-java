package com.smartling.connector.hubspot.sdk.domain;

import com.google.gson.annotations.SerializedName;
import com.smartling.connector.hubspot.sdk.common.Paging;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DomainDetails {
    private int totalCount;
    @SerializedName("results")
    private List<DomainDetail> detailList = new ArrayList<>();
    private Paging paging;
}
