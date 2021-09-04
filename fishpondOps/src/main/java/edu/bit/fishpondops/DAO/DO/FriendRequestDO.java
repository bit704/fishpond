package edu.bit.fishpondops.DAO.DO;

public class FriendRequestDO {

    /**
     * 申请者编号
     */
    private int requester;

    /**
     * 接收者编号
     */
    private int receiver;

    /**
     * 申请时间
     */
    private String request_time;

    /**
     * 附加信息
     */
    private String explanation;

    public int getRequester() {
        return requester;
    }

    public int getReceiver() {
        return receiver;
    }

    public String getRequest_time() {
        return request_time;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return "FriendRequestDO{" +
                "requester=" + requester +
                ", receiver=" + receiver +
                ", request_time='" + request_time + '\'' +
                ", explanation='" + explanation + '\'' +
                '}';
    }
}
