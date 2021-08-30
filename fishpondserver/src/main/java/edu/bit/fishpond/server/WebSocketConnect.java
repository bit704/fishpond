package edu.bit.fishpond.server;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocket/{id}")
@Component
public class WebSocketConnect {

    private static CopyOnWriteArraySet<WebSocketConnect> currentConnects = new CopyOnWriteArraySet<>();
    private Session session;
    private Logger logger = LoggerFactory.getLogger(WebSocketConnect.class);

    private StringBuffer stringBuffer;
    private int id;

    @OnOpen
    public void onOpen(Session newSession, @PathParam("id") int newId){
        session = newSession;
        currentConnects.add(this);
        id = newId;

        logger.info("与" + newId + "建立会话");
    }

    @OnClose
    public void onClose(){
        currentConnects.remove(this);
        logger.info("与" + id + "的会话关闭");
    }

    @OnMessage
    public void onMessage(Session sendSession, String message){
        stringBuffer = new StringBuffer();
        stringBuffer.append("客户端：");
        stringBuffer.append(id);
        stringBuffer.append("发送消息：");
        stringBuffer.append(message);
        logger.info(stringBuffer.toString());
        //使用Json作消息解析
        SessionMessage sessionMessage = JSON.parseObject(message, SessionMessage.class);
    }

    @OnError
    public void onError(Session errorSession, Throwable error){
        logger.error("与" + id + "的会话发生错误", error);
    }

}
