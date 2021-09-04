package edu.bit.fishpondops.DAO.DO;

public class GroupMessageDO {

    /**
     * 申请者编号
     */
    private int sender;

    /**
     * 接收群编号
     */
    private int receiver;

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
        return "GroupMessageDO{" +
                "sender=" + sender +
                ", receiver=" + receiver +
                ", send_time='" + send_time + '\'' +
                ", mtype='" + mtype + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
