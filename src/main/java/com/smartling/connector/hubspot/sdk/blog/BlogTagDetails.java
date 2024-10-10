package com.smartling.connector.hubspot.sdk.blog;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogTagDetails {
    private int totalCount;

    @SerializedName("results")
    private List<BlogTagDetail> detailList = new ArrayList<>();
}
