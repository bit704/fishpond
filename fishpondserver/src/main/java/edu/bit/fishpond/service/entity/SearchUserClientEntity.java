package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class SearchUserClientEntity {

    @JSONField
    private String searchInput;

    public String getSearchInput() {
        return searchInput;
    }

    public void setSearchInput(String searchInput) {
        this.searchInput = searchInput;
    }
}
