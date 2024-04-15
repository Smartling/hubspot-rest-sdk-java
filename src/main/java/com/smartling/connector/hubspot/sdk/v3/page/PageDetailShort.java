package com.smartling.connector.hubspot.sdk.v3.page;

import com.smartling.connector.hubspot.sdk.common.CurrentState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDetailShort
{
    public static final String FIELDS = "id,name,currentState,archivedAt,createdAt,updatedAt,slug,language";

    private long    id;
    private String  name;
    private CurrentState currentState;
    private boolean archivedAt;
    private Date    createdAt;
    private Date    updatedAt;
    private String  slug;
    private String  language;
}
