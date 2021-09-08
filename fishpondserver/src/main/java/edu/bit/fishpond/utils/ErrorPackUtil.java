package edu.bit.fishpond.utils;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpond.service.ServerMessage;
import edu.bit.fishpond.service.entity.ErrorEntity;

public class ErrorPackUtil {

    public static ServerMessage getCustomError(String info, int targetId){
        ErrorEntity errorEntity = new ErrorEntity();
        errorEntity.setErrorInfo(info);
        String messageBody = JSON.toJSONString(errorEntity);
        String messageHead = "Error";
        return new ServerMessage(targetId, messageHead, messageBody);
    }
}
