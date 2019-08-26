package com.smartling.connector.hubspot.sdk.email;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
    @SerializedName("totalCount")
    int totalCount;
    @SerializedName("objects")
    List<EmailDetail> detailList = new ArrayList<>();
}
