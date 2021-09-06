package edu.bit.fishpondops.service;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpondops.utils.FakeClient;
import edu.bit.fishpondops.utils.GenerateRandomInfo;
import edu.bit.fishpondops.utils.entity.LoginClientEntity;
import edu.bit.fishpondops.utils.entity.MessageEntity;
import edu.bit.fishpondops.utils.entity.RegisterClientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ClientService {

    @Autowired
    QueryService queryService;

    public final String localhost = "localhost:8080";
    public final String remote = "124.70.67.6:8080";
    public int clientNum = 0;
    public List<FakeClient> fakeClientList = new LinkedList<>();
    public ConcurrentMap<Integer, FakeClient> clientMap = new ConcurrentHashMap<>();
    public List<Integer> idList = new ArrayList<>();
    private final GenerateRandomInfo generateRandomInfo = new GenerateRandomInfo();

    public void reset() {
        clientNum = 0;
        fakeClientList.clear();
        idList.clear();
        clientMap.clear();
        queryService.clearDb();
    }

    public void initFakeClient(int num) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        for (; clientNum < num; clientNum++) {
            FakeClient fakeClient = new FakeClient();
            fakeClientList.add(fakeClient);
            try {
                container.connectToServer(fakeClient, new URI("ws://" + remote + "/websocket"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void activeClient() {
        for (FakeClient fakeClient : fakeClientList) {
            registerAndLogin(fakeClient);
        }
        communicate();
        for (FakeClient fakeClient : fakeClientList) {
            close(fakeClient);
        }
    }

    public void registerAndLogin(FakeClient fakeClient) {
        RegisterClientEntity registerClientEntity = new RegisterClientEntity();
        registerClientEntity.setUserName(generateRandomInfo.generateName());
        registerClientEntity.setPassword("666");
        registerClientEntity.setSecurityQuestion("666");
        registerClientEntity.setAnswer("666");
        fakeClient.send("Register|" + JSON.toJSONString(registerClientEntity));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clientMap.put(fakeClient.getUid(), fakeClient);
        idList.add(fakeClient.getUid());

        LoginClientEntity loginClientEntity = new LoginClientEntity();
        loginClientEntity.setLoginUserId(fakeClient.getUid());
        loginClientEntity.setPasswordHash("666");
        fakeClient.send("Login|" + JSON.toJSONString(loginClientEntity));
    }


    public void communicate() {
        int turn = 10;
        queryService.queryMessages();
        for (int i = 0; i < turn; i++) {
            for(int j=0; j<1000 ;j++) {
                Random random = new Random(System.currentTimeMillis());
                int len = idList.size();
                int rand1 = random.nextInt(len);
                int rand2 = random.nextInt(len);
                sendMessage(idList.get(rand1), idList.get(rand2));
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            queryService.queryMessages();
        }
    }

    private void sendMessage(int senderId, int recipientId) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setSenderId(senderId);
        messageEntity.setRecipientId(recipientId);
        messageEntity.setSendTime(LocalDateTime.now().toString());
        messageEntity.setMessageContent("测试消息");
        //发送消息
        clientMap.get(senderId).send("SendMessageTo|" + JSON.toJSONString(messageEntity));
    }


    public void close(FakeClient fakeClient) {
        try {
            fakeClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}