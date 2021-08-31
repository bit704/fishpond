package edu.bit.fishpond.DAO.DO;

public class UserInfoDO {

    /**
     * 用户编号
     */
    int uid;

    /**
     * 用户名
     */
    String name;

    /**
     * 性别
     */
    String sex;

    /**
     * 生日
     */
    String birthday;

    /**
     * 地区
     */
    String region;

    /**
     * 在线状态
     */
    Boolean state;

    /**
     * 创建日期
     */
    String create_time;

    /**
     * 上次离线时间
     */
    String last_offline;

    /**
     * 是否是真实用户
     */
    Boolean real;

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
