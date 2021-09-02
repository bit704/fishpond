package edu.bit.fishpond.service;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Map;

public class ServiceResult {

    @JSONField
    private boolean sendMessage = false;

    @JSONField
    private Map<Integer, String> senderMessageMap;

    public boolean isSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(boolean sendMessage) {
        this.sendMessage = sendMessage;
    }

    public Map<Integer, String> getSenderMessageMap() {
        return senderMessageMap;
    }

    public void setSenderMessageMap(Map<Integer, String> senderMessageMap) {
        this.senderMessageMap = senderMessageMap;
    }
}
