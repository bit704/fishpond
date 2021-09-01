package edu.bit.fishpond.service.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class LoginServerEntity {

    @JSONField
    private boolean loginResult;

    public boolean getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(boolean loginResult) {
        this.loginResult = loginResult;
    }
}
