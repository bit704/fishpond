package edu.bit.fishpond.DAO.DO;

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

    @Override
    public String toString() {
        return "FriendshipDO{" +
                "uid1=" + uid1 +
                ", uid2=" + uid2 +
                ", friend_time='" + friend_time + '\'' +
                '}';
    }
}
