package edu.bit.fishpond.DAO.DO;

public class UserInfoDO {

    /**
     * 用户编号
     */
    private int uid;

    /**
     * 用户名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 地区
     */
    private String region;

    /**
     * 在线状态
     */
    private Boolean state;

    /**
     * 创建日期
     */
    private String create_time;

    /**
     * 上次离线时间
     */
    private String last_offline;

    /**
     * 是否是真实用户
     */
    private Boolean real;

    @Override
    public String toString() {
        return "UserInfoDO{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", region='" + region + '\'' +
                ", state=" + state +
                ", create_time='" + create_time + '\'' +
                ", last_offline='" + last_offline + '\'' +
                ", real=" + real +
                '}';
    }
}
