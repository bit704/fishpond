package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class NewSecureInfoEntity {

    @JSONField
    private int userId;

    @JSONField
    private String newPassword;

    @JSONField
    private String newSecureQuestion;

    @JSONField
    private String newAnswer;

    public int getUserId() {
        return userId;
    }

    public String getNewAnswer() {
        return newAnswer;
    }

    public String getNewSecureQuestion() {
        return newSecureQuestion;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setNewSecureQuestion(String newSecureQuestion) {
        this.newSecureQuestion = newSecureQuestion;
    }

    public void setNewAnswer(String newAnswer) {
        this.newAnswer = newAnswer;
    }
}
