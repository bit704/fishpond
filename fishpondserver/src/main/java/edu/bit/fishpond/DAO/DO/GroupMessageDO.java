package edu.bit.fishpond.DAO.DO;

public class GroupMessageDO {

    /**
     * 唯一编号
     */
    private int gmid;

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

    public int getGmid() {
        return gmid;
    }

    public int getSender() {
        return sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public String getSend_time() {
        return send_time;
    }

    public String getMtype() {
        return mtype;
    }

    public String getContent() {
        return content;
    }

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
