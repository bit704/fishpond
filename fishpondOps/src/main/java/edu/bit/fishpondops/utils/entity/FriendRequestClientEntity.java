package edu.bit.fishpondops.utils.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class FriendRequestClientEntity {

    @JSONField(ordinal = 1)
    private int applierId;

    @JSONField(ordinal = 2)
    private int recipientId;

    @JSONField(ordinal = 3)
    private String explain;

    public int getApplierId() {
        return applierId;
    }

    public void setApplierId(int applierId) {
        this.applierId = applierId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }
}
