package edu.bit.fishpond.server;

import com.alibaba.fastjson.annotation.JSONField;

public class SessionMessage {

    @JSONField
    private String head;

    @JSONField
    private String body;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
