package edu.bit.fishpond.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebSocketServer {

    private static final ConcurrentMap<Integer, Session> idSessionMap = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    public static void SendMessageTo(int id, String message){
        Session session = GetSessionById(id);
        if (session == null){
            logger.error(String.format("发送失败:找不到id:%d对应的Session",id));
            return;
        }
        logger.info(String.format("向客户端:%d发送了消息:%s",id, message));
        session.getAsyncRemote().sendText(message);
    }

    private static Session GetSessionById(int id){
        return idSessionMap.getOrDefault(id, null);
    }

    public static void NewConnect(Session session, Integer id){
        if (idSessionMap.containsKey(id)){
            logger.warn(String.format("Id:%d已在线", id));
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
