package edu.bit.fishpondops.DAO.DO;

public class UserDO {

    /**
     * 用户编号
     */
    private int uid;

    /**
     * 密码
     */
    private String password;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;

    public int getUid() {
        return uid;
    }

    public String getPassword() {
        return password;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return "UserDO{" +
                "uid=" + uid +
                ", password='" + password + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
