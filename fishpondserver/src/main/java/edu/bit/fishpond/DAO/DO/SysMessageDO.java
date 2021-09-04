package edu.bit.fishpond.DAO.DO;

public class SysMessageDO {

    /**
     * 目标用户ID
     */
    private int user;

    /**
     * 发送时间
     */
    private String send_time;

    /**
     * 消息类型
     */
    private String mtype;

    /**
     * 消息内容
     */
    private String content;

    @Override
    public String toString() {
        return "SysMessageDO{" +
                "user=" + user +
                ", send_time='" + send_time + '\'' +
                ", mtype='" + mtype + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
