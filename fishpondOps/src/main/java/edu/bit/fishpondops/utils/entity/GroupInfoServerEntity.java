package edu.bit.fishpondops.utils.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class GroupInfoServerEntity {

    @JSONField
    private int groupId;

    @JSONField
    private String groupName;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
