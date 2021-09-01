package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;


public class FriendServerEntity {

    @JSONField(name = "id")
    private int friendId;

    @JSONField(name = "name")
    private String friendName;

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
