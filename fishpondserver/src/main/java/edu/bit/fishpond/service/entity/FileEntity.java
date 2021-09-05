package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class FileEntity {

    @JSONField
    private String fileName;

    @JSONField
    private String extensionName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtensionName() {
        return extensionName;
    }

    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }
}
