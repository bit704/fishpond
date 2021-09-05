package edu.bit.fishpondops.utils.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class ErrorEntity {

    @JSONField
    private String errorInfo;

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
