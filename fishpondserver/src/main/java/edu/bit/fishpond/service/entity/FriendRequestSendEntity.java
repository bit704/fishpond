package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 向客户端发送好友请求的通信实体类
 */
public class FriendRequestSendEntity {

    @JSONField(ordinal = 1)
    private int senderId;

    @JSONField(ordinal = 2)
    private String sendTime;

    @JSONField(ordinal = 3)
    private String explain;

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
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
}
