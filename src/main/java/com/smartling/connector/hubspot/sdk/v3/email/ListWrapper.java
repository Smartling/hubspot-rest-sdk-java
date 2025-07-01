package com.smartling.connector.hubspot.sdk.v3.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListWrapper<T> {

    private int total;

    private List<T> results = new ArrayList<>();

    private Paging paging;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Paging {
        private Next next;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Next {
            private String after;
            private String link;
        }
    }
}
