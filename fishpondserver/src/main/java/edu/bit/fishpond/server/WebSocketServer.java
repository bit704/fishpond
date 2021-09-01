package edu.bit.fishpond.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebSocketServer {

    public static ConcurrentMap<Integer, Session> idSessionMap = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    public static void SendMessageTo(int id, String head, String body){
        Session session = GetSessionById(id);
        if (session == null){
            logger.error(String.format("发送失败:找不到id:%d对应的Session",id));
            return;
        }
        String message = head + "|" + body;
        logger.info(String.format("向客户端:%d发送了消息:%s",id, message));
        session.getAsyncRemote().sendText(message);
    }

    private static Session GetSessionById(int id){
        return idSessionMap.getOrDefault(id, null);
    }

    public static void NewConnect(Session session, Integer id){
        idSessionMap.putIfAbsent(id, session);
    }

    public static void DisConnect(Integer id){
        Session res = idSessionMap.remove(id);
        if (res == null){
            logger.error("移除失败:" + id);
        }
    }
}
