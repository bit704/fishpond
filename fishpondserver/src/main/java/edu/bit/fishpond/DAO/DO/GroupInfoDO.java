package edu.bit.fishpond.DAO.DO;

public class GroupInfoDO {

    /**
     * 群聊编号
     */
    private int gid;

    /**
     * 群聊名称
     */
    private String name;

    /**
     * 创建者ID
     */
    private int creator;

    /**
     * 创建时间
     */
    private String create_time;

    /**
     * 群主ID
     */
    private int manager;

    /**
     * 是否是真实群
     */
    private boolean real;

    @Override
    public String toString() {
        return "GroupInfoDO{" +
                "gid=" + gid +
                ", name='" + name + '\'' +
                ", creator=" + creator +
                ", create_time='" + create_time + '\'' +
                ", manager=" + manager +
                ", real=" + real +
                '}';
    }
}
