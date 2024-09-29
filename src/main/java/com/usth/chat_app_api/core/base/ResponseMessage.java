package com.usth.chat_app_api.core.base;

import java.util.HashMap;
import java.util.Map;

public abstract class ResponseMessage {
    private static int KEY_SUCCESS = 200;
    private static String VALUE_SUCCESS = "Success";

    public static String getMessage(int code){
        Map<Integer, String> mapMessage = new HashMap<>();
        mapMessage.put(KEY_SUCCESS, VALUE_SUCCESS);
        return mapMessage.get(code);
    }
}
