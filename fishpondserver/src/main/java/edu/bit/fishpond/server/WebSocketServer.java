package edu.bit.fishpond.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static javax.websocket.CloseReason.CloseCodes.GOING_AWAY;

public class WebSocketServer {

    private static final ConcurrentMap<Integer, Session> idSessionMap = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    public static void SendMessageTo(int id, String message){
        Session session = GetSessionById(id);
        if (session == null){
            logger.error(String.format("发送失败:找不到id:%d对应的Session",id));
            return;
        }

        session.getAsyncRemote().sendText(message, sendResult -> {
            if (!sendResult.isOK()){
                logger.error(sendResult.getException().getMessage());
            }
            else {
                logger.info(String.format("向客户端:%d发送了消息:%s",id, message));
            }
        });


    }

    private static Session GetSessionById(int id){
        return idSessionMap.getOrDefault(id, null);
    }

    public static void NewConnect(Session session, Integer id) throws IOException {
        if (idSessionMap.containsKey(id)){
            logger.warn(String.format("Id:%d已在线，将导致原有登录下线", id));
            idSessionMap.get(id).close(new CloseReason(GOING_AWAY,"账号在别的位置登录"));
        }

        idSessionMap.put(id, session);
        if (!idSessionMap.containsKey(id)){
            logger.error(String.format("Id%d未成功记录", id));
        }
    }

    public static void DisConnect(Integer id){
        if (!idSessionMap.containsKey(id)){
            logger.warn(String.format("Id:%d未登录，不必断开连接",id));
        }
        idSessionMap.remove(id);
    }
}
