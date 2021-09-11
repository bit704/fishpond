package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class NewGroupMemberClientEntity {

    @JSONField
    private int groupId;

    @JSONField
    private int newMemberId;

    @JSONField
    private int invitorId;


    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getNewMemberId() {
        return newMemberId;
    }

    public void setNewMemberId(int newMemberId) {
        this.newMemberId = newMemberId;
    }

    public int getInvitorId() {
        return invitorId;
    }

    public void setInvitorId(int invitorId) {
        this.invitorId = invitorId;
    }
}
