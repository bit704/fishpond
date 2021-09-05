package edu.bit.fishpondops.utils;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpondops.service.ClientService;
import edu.bit.fishpondops.utils.entity.SingleIntEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;

@ClientEndpoint
public class FakeClient {

    private Logger logger = LoggerFactory.getLogger(FakeClient.class);
    private Session session;
    private int uid;

    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected to endpoint: " + session.getAsyncRemote());
        this.session = session;
    }

    @OnClose
    public void onClose(){
        logger.info("Websocket closed");
    }

    @OnMessage
    public void onMessage(String message){
        System.out.println("收到的消息: " + message);
        String[] strings = message.split("\\|");
        switch (strings[0]) {
            case "RegisterResult":
                SingleIntEntity singleIntEntity = JSON.parseObject(strings[1], SingleIntEntity.class);
                uid = singleIntEntity.getUserId();
                break;
        }
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }

    public void send(String message){
        this.session.getAsyncRemote().sendText(message);
    }

    public void close() throws IOException{
        if(this.session.isOpen()){
            this.session.close();
        }
    }

    public int getUid() {
        return uid;
    }
}


