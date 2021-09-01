package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 用户注册时从客户端发往服务端的通信实体
 */
public class RegisterClientEntity {

    @JSONField(ordinal = 1)
    private String userName;

    @JSONField(ordinal = 2)
    private String password;

    @JSONField(ordinal = 3)
    private String securityQuestion;

    @JSONField(ordinal = 4)
    private String answer;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
