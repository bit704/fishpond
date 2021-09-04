package edu.bit.fishpondops.DAO.DO;

public class FriendshipDO {

    /**
     * 用户1编号
     */
    private int uid1;

    /**
     * 用户2编号
     */
    private int uid2;

    /**
     * 成为好友的时间
     */
    private String friend_time;

    public int getUid1() {
        return uid1;
    }

    public int getUid2() {
        return uid2;
    }

    public String getFriend_time() {
        return friend_time;
    }

    @Override
    public String toString() {
        return "FriendshipDO{" +
                "uid1=" + uid1 +
                ", uid2=" + uid2 +
                ", friend_time='" + friend_time + '\'' +
                '}';
    }
}
