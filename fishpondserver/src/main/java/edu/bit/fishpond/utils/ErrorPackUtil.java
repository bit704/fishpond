package edu.bit.fishpond.utils;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpond.service.entity.ErrorEntity;

public class ErrorPackUtil {

    public static String setError(String info){
        ErrorEntity errorEntity = new ErrorEntity();
        errorEntity.setErrorInfo(info);
        return JSON.toJSONString(errorEntity);
    }
}
