package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 仅包含一个用户id的通信实体
 */
public class SingleIntEntity {

    @JSONField
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
