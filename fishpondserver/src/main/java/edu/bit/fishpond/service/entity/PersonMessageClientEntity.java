package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class PersonMessageClientEntity {

    @JSONField
    private int userId1;

    @JSONField
    private int userId2;

    public int getUserId1() {
        return userId1;
    }

    public void setUserId1(int userId1) {
        this.userId1 = userId1;
    }

    public int getUserId2() {
        return userId2;
    }

    public void setUserId2(int userId2) {
        this.userId2 = userId2;
    }
}
