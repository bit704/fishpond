package edu.bit.fishpondops.utils.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class GroupCreateClientEntity {

    @JSONField
    private int creatorId;

    @JSONField
    private String groupName;

    @JSONField
    private List<Integer> initialMembers;

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Integer> getInitialMembers() {
        return initialMembers;
    }

    public void setInitialMembers(List<Integer> initialMembers) {
        this.initialMembers = initialMembers;
    }
}
