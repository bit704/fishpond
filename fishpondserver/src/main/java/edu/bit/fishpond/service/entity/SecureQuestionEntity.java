package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class SecureQuestionEntity {

    @JSONField
    private String question;

    @JSONField
    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
