package edu.bit.fishpond.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static javax.websocket.CloseReason.CloseCodes.GOING_AWAY;

public class WebSocketServer {

    private static final ConcurrentMap<Integer, WebSocketConnect> idConnectMap = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    public static void SendMessageTo(int id, String message){
        WebSocketConnect webSocketConnect = GetConnectById(id);
        if (webSocketConnect == null){
            logger.error(String.format("发送失败:找不到id:%d对应的连接",id));
            return;
        }

        webSocketConnect.sendMessageDirect(message);


    }

    public static void sendMessageToAll() {
        for (WebSocketConnect connect : idConnectMap.values()){
            connect.sendMessageDirect("ping");
        }
    }

    private static WebSocketConnect GetConnectById(int id){
        return idConnectMap.getOrDefault(id, null);
    }

    public static void NewConnect(WebSocketConnect webSocketConnect, Integer id) throws IOException {
        if (idConnectMap.containsKey(id)){
            logger.warn(String.format("Id:%d已在线，将导致原有登录下线", id));
            idConnectMap.get(id).close(new CloseReason(GOING_AWAY,"账号在别的位置登录"));
        }

        idConnectMap.put(id, webSocketConnect);
        if (!idConnectMap.containsKey(id)){
            logger.error(String.format("Id%d未成功记录", id));
        }
    }

    public static void DisConnect(Integer id){
        if (!idConnectMap.containsKey(id)){
            logger.warn(String.format("Id:%d未登录，不必断开连接",id));
        }
        idConnectMap.remove(id);
    }
}
