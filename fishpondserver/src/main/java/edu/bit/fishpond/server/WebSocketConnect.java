package edu.bit.fishpond.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import edu.bit.fishpond.service.ConnectService;
import edu.bit.fishpond.service.ServerMessage;
import edu.bit.fishpond.service.ServiceResult;
import edu.bit.fishpond.service.entity.*;
import edu.bit.fishpond.service.UserService;
import edu.bit.fishpond.utils.MessageHeadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

@ServerEndpoint("/websocket")
@Component
public class WebSocketConnect {
    private final Logger logger = LoggerFactory.getLogger(WebSocketConnect.class);

    private Session session;
    private int id = 0;

    private static UserService userService;
    private static ConnectService connectService;

    @Autowired
    public void setUserService(UserService  userService, ConnectService connectService){
        WebSocketConnect.userService = userService;
        WebSocketConnect.connectService = connectService;
    }

    @OnOpen
    public void onOpen(Session newSession){
        session = newSession;
        logger.info("建立新的会话");
        //sendMessageToOne("hello");
    }

    @OnClose
    public void onClose(){
        if (id != 0){
            WebSocketServer.DisConnect(id);
        }
        logger.info(String.format("与%d的会话已关闭",id));
    }

    @OnMessage
    public void onMessage(String message){

        logger.info(String.format("客户端:%d发送了消息：%s",id,message));

        //解析整条消息，得到消息头和消息体
        String[] messageSplitArray = message.split("\\|",2);
        if (messageSplitArray.length != 2){
            logger.warn(String.format("无法解析：%s,消息格式错误",message));
            return;
        }

        String head = messageSplitArray[0];
        String body = messageSplitArray[1];
        String sendMessageBody;
        String sendMessageHead;
        ServiceResult result;
        List<ServerMessage> serverMessageList;
        //根据消息头解析消息体
        try {
            switch (head){
                case "Login":
                    LoginClientEntity loginClientEntity = JSONObject.parseObject(body, LoginClientEntity.class);

                    boolean queryResult = connectService.login(loginClientEntity);
                    LoginServerEntity loginServerEntity = new LoginServerEntity();
                    loginServerEntity.setLoginResult(queryResult);
                    sendMessageBody = JSON.toJSONString(loginServerEntity);
                    sendMessageHead = "LoginResult";
                    if (queryResult){
                        id = loginClientEntity.getLoginUserId();
                        logger.info(String.format("Id：%d成功登录",id));
                        WebSocketServer.NewConnect(session, id);
                    }

                    sendMessageDirect(sendMessageHead, sendMessageBody);
                    break;
                case "Register":
                    RegisterClientEntity registerClientEntity = JSONObject.parseObject(body, RegisterClientEntity.class);
                    result = userService.register(registerClientEntity);
                    resultHandler(result);
                    break;

                case "FriendRequest":
                    FriendRequestClientEntity friendRequestClientEntity =
                            JSONObject.parseObject(body, FriendRequestClientEntity.class);
                    sendMessageBody = userService.friendRequestHandler(friendRequestClientEntity);
                    if (!sendMessageBody.isEmpty()){
                        sendMessageDirect("FriendRequestFromOther", sendMessageBody);
                    }
                    break;
                case "FriendRequestFeedback":
                    FriendRequestFeedbackClientEntity friendRequestFeedbackClientEntity =
                            JSONObject.parseObject(body, FriendRequestFeedbackClientEntity.class);
                    serverMessageList = userService.FriendRequestFeedbackHandler(friendRequestFeedbackClientEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetFriendList":
                    UserIdEntity userIdEntity = JSONObject.parseObject(body, UserIdEntity.class);
                    sendMessageBody = userService.FriendListService(userIdEntity);
                    sendMessageHead = "FriendList";
                    sendMessageDirect(sendMessageHead, sendMessageBody);
                    break;
                case "GetLatestMessage":
                    UserIdEntity getLatestMessageUserIdEntity = JSONObject.parseObject(body,UserIdEntity.class);
                    sendMessageBody = userService.getLatestMessage(getLatestMessageUserIdEntity);
                    sendMessageHead = "LatestMessage";
                    sendMessageDirect(sendMessageHead, sendMessageBody);
                    break;
                case "GetMessageBetween":
                    PersonMessageClientEntity personMessageClientEntity =
                            JSONObject.parseObject(body,PersonMessageClientEntity.class);
                    result = userService.getAllMessageBetween(personMessageClientEntity);
                    resultHandler(result);
                    break;
                case "GetGroupList":
                    break;
                case "GetAllMessage":
                    UserIdEntity getMessageUserIdEntity = JSONObject.parseObject(body,UserIdEntity.class);
                    sendMessageBody = userService.getAllMessage(getMessageUserIdEntity);
                    sendMessageHead = "AllMessage";
                    sendMessageDirect(sendMessageHead, sendMessageBody);
                    break;
                case "SendMessageTo":
                    MessageEntity sendMessageEntity = JSONObject.parseObject(body, MessageEntity.class);
                    result = userService.getMessage(sendMessageEntity);
                    resultHandler(result);
                    break;
                case "SearchUser":
                    SearchUserClientEntity searchUserClientEntity =
                            JSONObject.parseObject(body, SearchUserClientEntity.class);
                    sendMessageBody = userService.searchUser(searchUserClientEntity);
                    sendMessageHead = "SearchUserResult";
                    sendMessageDirect(sendMessageHead,sendMessageBody);
                    break;
                case "OffLine":
                    UserIdEntity offLineUserIdEntity = JSONObject.parseObject(body, UserIdEntity.class);
                    connectService.offLine(offLineUserIdEntity);
                    onClose();
                    break;
                default:
                    throw new MessageHeadException(String.format("无法解析:%s,未知的消息头%s",message,head));
            }
        }
        catch (MessageHeadException messageHeadException) {
            logger.warn(messageHeadException.getMessage());
        }
        catch (JSONException jsonException) {
            logger.warn(String.format("无法解析:%s,未知的消息体%s",message,body));
        }
    }

    @OnError
    public void onError(Session session, Throwable error){
        logger.error(String.format("与%d的会话发生错误",id));
    }

    public void sendMessageDirect(String head, String body) {
        String message = head + "|" + body;
        logger.info(String.format("向客户端:%d发送了消息:%s",id, message));
        session.getAsyncRemote().sendText(message);
    }
    
    public void sendMessageDirect(String message) {
        logger.info(String.format("向客户端:%d发送了消息:%s",id, message));
        session.getAsyncRemote().sendText(message);
    }

    private void resultHandler(ServiceResult result){
        if (result.isSendMessage()){
            for (Map.Entry<Integer, String> entry: result.getSenderMessageMap().entrySet()) {
                if (entry.getKey() == 0){
                    sendMessageDirect(entry.getValue());
                }
                else {
                    WebSocketServer.SendMessageTo(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void sendServerMessage(List<ServerMessage> serverMessageList){
        for (ServerMessage serverMessage: serverMessageList) {
            int targetId = serverMessage.getTargetId();
            if (targetId == 0){
                sendMessageDirect(serverMessage.getMessage());
            }
            else {
                WebSocketServer.SendMessageTo(targetId, serverMessage.getMessage());
            }
        }
    }


}
