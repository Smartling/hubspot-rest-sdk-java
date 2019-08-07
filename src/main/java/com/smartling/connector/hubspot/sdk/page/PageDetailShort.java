package com.smartling.connector.hubspot.sdk.page;

import com.smartling.connector.hubspot.sdk.NameAware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDetailShort implements NameAware
{
    public static final String FIELDS = "id,name,current_state,archived,created,updated,slug,language";

    private long    id;
    private String  name;
    private PageState  currentState;
    private boolean archived;
    private Date    created;
    private Date    updated;
    private String  slug;
    private String  language;
}
