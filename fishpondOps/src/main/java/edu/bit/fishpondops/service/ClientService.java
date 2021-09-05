package edu.bit.fishpondops.service;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpondops.utils.FakeClient;
import edu.bit.fishpondops.utils.GenerateRandomInfo;
import edu.bit.fishpondops.utils.entity.LoginClientEntity;
import edu.bit.fishpondops.utils.entity.RegisterClientEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientService {

    public static GenerateRandomInfo generateRandomInfo = new GenerateRandomInfo();

    public static int clientNum = 0;
    public static final String localhost = "localhost:8080";
    public static final String remote = "124.70.67.6:8080";
    public static ConcurrentMap<Integer,FakeClient> clientMap = new ConcurrentHashMap<>();

    public static void initFakeClient(int num) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        for(; clientNum < num; clientNum++) {
            clientMap.put(clientNum,new FakeClient());
            try {
                container.connectToServer(clientMap.get(clientNum), new URI("ws://"+localhost+"/websocket"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void activeClient() {
        for(int i=0; i<clientNum; i++) {
            FakeClient fakeClient = clientMap.get(i);
            registerAndLogin(fakeClient);
            close(fakeClient);
        }
    }

    public static void registerAndLogin(FakeClient fakeClient) {
        RegisterClientEntity registerClientEntity = new RegisterClientEntity();
        registerClientEntity.setUserName(generateRandomInfo.generateName());
        registerClientEntity.setPassword("666");
        registerClientEntity.setSecurityQuestion("666");
        registerClientEntity.setAnswer("666");
        fakeClient.send("Register|"+JSON.toJSONString(registerClientEntity));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LoginClientEntity loginClientEntity = new LoginClientEntity();
        System.out.println("uid:"+fakeClient.getUid());
        loginClientEntity.setLoginUserId(fakeClient.getUid());
        loginClientEntity.setPasswordHash("666");
        fakeClient.send("Login|"+JSON.toJSONString(loginClientEntity));

    }

    public static void close(FakeClient fakeClient) {
        try {
            fakeClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}