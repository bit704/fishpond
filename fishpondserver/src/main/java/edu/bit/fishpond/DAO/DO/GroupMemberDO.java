package edu.bit.fishpond.DAO.DO;

public class GroupMemberDO {

    /**
     * 群聊编号
     */
    private int gid;

    /**
     * 成员编号
     */
    private int memberID;

    /**
     * 邀请人编号
     */
    private int invitorID;

    /**
     * 加入时间
     */
    private String join_time;

    public int getGid() {
        return gid;
    }

    public int getMemberID() {
        return memberID;
    }

    public int getInvitorID() {
        return invitorID;
    }

    public String getJoin_time() {
        return join_time;
    }

    @Override
    public String toString() {
        return "GroupMemberDO{" +
                "gid=" + gid +
                ", memberID=" + memberID +
                ", invitorID=" + invitorID +
                ", join_time='" + join_time + '\'' +
                '}';
    }
}
