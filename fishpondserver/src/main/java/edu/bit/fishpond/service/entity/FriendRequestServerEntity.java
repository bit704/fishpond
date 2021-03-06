package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 向客户端发送好友请求的通信实体类
 */
public class FriendRequestServerEntity {

    @JSONField(ordinal = 1)
    private int applierId;

    @JSONField(ordinal = 2)
    private String sendTime;

    @JSONField(ordinal = 3)
    private String explain;

    @JSONField(ordinal = 4)
    private String applierName;

    public int getApplierId() {
        return applierId;
    }

    public void setApplierId(int applierId) {
        this.applierId = applierId;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getApplierName() {
        return applierName;
    }

    public void setApplierName(String applierName) {
        this.applierName = applierName;
    }
}
