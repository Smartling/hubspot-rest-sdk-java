package com.smartling.connector.hubspot.sdk.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageSearchFilter
{
    private Long id;
    private String name;
    private String campaign; // The guid of the marketing campaign this page is associated with
    private PageState pageState;
    private Subcategory subcategory;
    private Boolean archived;
}
