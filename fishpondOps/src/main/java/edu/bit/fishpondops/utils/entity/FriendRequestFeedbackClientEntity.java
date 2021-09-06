package edu.bit.fishpondops.utils.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 好友申请反馈通信实体
 * 即好友申请的接收者对好友申请的反馈（接受或拒绝）
 */
public class FriendRequestFeedbackClientEntity {

    @JSONField(ordinal = 1)
    private int senderId;// 好友申请的发送者ID

    @JSONField(ordinal = 2)
    private int recipientId;//好友申请的接收者ID

    @JSONField(ordinal = 3)
    private boolean result;//好友申请的接收者的态度，接受或拒绝


    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
