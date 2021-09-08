package edu.bit.fishpond.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import edu.bit.fishpond.service.*;
import edu.bit.fishpond.service.entity.*;
import edu.bit.fishpond.utils.DAOException;
import edu.bit.fishpond.utils.MessageHeadException;
import edu.bit.fishpond.utils.SecureForServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.List;

@ServerEndpoint("/websocket")
@Component
public class WebSocketConnect {
    private final Logger logger = LoggerFactory.getLogger(WebSocketConnect.class);

    private Session session;
    private int id = 0;
    private PrivateKey privateKey;

    private static UserService userService;
    private static ConnectService connectService;
    private static GroupService groupService;
    private static MessageService messageService;

    @Autowired
    public void setUserService
            (UserService  userService, ConnectService connectService, GroupService groupService,
            MessageService messageService){
        WebSocketConnect.userService = userService;
        WebSocketConnect.connectService = connectService;
        WebSocketConnect.groupService = groupService;
        WebSocketConnect.messageService = messageService;
    }

    @OnOpen
    public void onOpen(Session newSession){
        session = newSession;
//        KeyPair keyPair = SecureForServer.generateKeyPair();
//        privateKey = keyPair.getPrivate();
//        sendMessageDirect("Key", JSON.toJSONString(SecureForServer.getPublicKeyDTO(keyPair)));
        logger.info(String.format("与%s建立新的会话", session.getId()));
    }

    @OnClose
    public void onClose(CloseReason closeReason){
        logger.info("关闭原因：" + closeReason.getCloseCode() + ",关闭原因描述：" + closeReason.getReasonPhrase());
        if (id != 0) {
            WebSocketServer.DisConnect(id);
            SingleIntEntity singleIntEntity = new SingleIntEntity();
            singleIntEntity.setUserId(id);
            try {
                connectService.offLine(singleIntEntity);
            } catch (DAOException daoException) {
                daoException.printStackTrace();
            }
            logger.info(String.format("与%d的会话已关闭",id));
        }
        else {
            logger.info(String.format("与%s的会话已关闭",session.getId()));
        }

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
        List<ServerMessage> serverMessageList;
        //根据消息头解析消息体
        try {
            switch (head){
                case "Login":
                    LoginClientEntity loginClientEntity = JSONObject.parseObject(body, LoginClientEntity.class);

                    boolean queryResult = connectService.login(loginClientEntity);
                    //boolean queryResult = connectService.login(loginClientEntity);
                    LoginServerEntity loginServerEntity = new LoginServerEntity();
                    loginServerEntity.setLoginResult(queryResult);
                    sendMessageBody = JSON.toJSONString(loginServerEntity);
                    sendMessageHead = "LoginResult";
                    if (queryResult){
                        id = loginClientEntity.getLoginUserId();
                        logger.info(String.format("Id：%d成功登录",id));
                        WebSocketServer.NewConnect(this, id);
                    }

                    sendMessageDirect(sendMessageHead, sendMessageBody);
                    break;
                case "Register":
                    RegisterClientEntity registerClientEntity = JSONObject.parseObject(body, RegisterClientEntity.class);
                    serverMessageList = userService.registerHandler(registerClientEntity);
                    //serverMessageList = userService.registerHandler(registerClientEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "SendFriendRequestTo":
                    FriendRequestClientEntity friendRequestClientEntity =
                            JSONObject.parseObject(body, FriendRequestClientEntity.class);
                    serverMessageList = userService.sendFriendRequestHandler(friendRequestClientEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "FriendRequestFeedback":
                    FriendRequestFeedbackClientEntity friendRequestFeedbackClientEntity =
                            JSONObject.parseObject(body, FriendRequestFeedbackClientEntity.class);
                    serverMessageList = userService.getFriendRequestFeedbackHandler(friendRequestFeedbackClientEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetFriendList":
                    SingleIntEntity singleIntEntity = JSONObject.parseObject(body, SingleIntEntity.class);
                    sendMessageBody = userService.getFriendList(singleIntEntity);
                    sendMessageHead = "FriendList";
                    sendMessageDirect(sendMessageHead, sendMessageBody);
                    break;
                case "GetLatestMessage":
                    SingleIntEntity getLatestMessageSingleIntEntity = JSONObject.parseObject(body, SingleIntEntity.class);
                    serverMessageList = messageService.getLatestMessageHandler(getLatestMessageSingleIntEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetLatestGroupMessage":
                    SingleIntEntity getLatestGroupMessageSingleIntEntity = JSONObject.parseObject(body, SingleIntEntity.class);
                    serverMessageList = groupService.getLatestGroupMessage(getLatestGroupMessageSingleIntEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetUnreadMessage":
                    SingleIntEntity getUnreadMessageSingleIntEntity = JSONObject.parseObject(body, SingleIntEntity.class);
                    serverMessageList = messageService.getUnreadMessage(getUnreadMessageSingleIntEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetUnreadGroupMessage":
                    SingleIntEntity getUnreadGroupMessageSingleIntEntity = JSONObject.parseObject(body, SingleIntEntity.class);
                    serverMessageList = groupService.getUnreadGroupMessageHandler(getUnreadGroupMessageSingleIntEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetUnreadFriendRequest":
                    SingleIntEntity getUnreadFriendRequest = JSONObject.parseObject(body, SingleIntEntity.class);
                    serverMessageList = userService.getUnreadFriendRequestHandler(getUnreadFriendRequest);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetMessageBetween":
                    PersonMessageClientEntity personMessageClientEntity =
                            JSONObject.parseObject(body,PersonMessageClientEntity.class);
                    serverMessageList = messageService.getAllMessageBetweenHandler(personMessageClientEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetMessageIn":
                    SingleIntEntity getGroupMessageInSingleIntEntity = JSONObject.parseObject(body, SingleIntEntity.class);
                    serverMessageList = groupService.getGroupMessageIn(getGroupMessageInSingleIntEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetGroupList":
                    SingleIntEntity groupListSingleIntEntity = JSONObject.parseObject(body, SingleIntEntity.class);
                    serverMessageList = groupService.getGroupListHandler(groupListSingleIntEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "SendMessageTo":
                    MessageEntity sendMessageEntity = JSONObject.parseObject(body, MessageEntity.class);
                    serverMessageList = messageService.sendMessageHandler(sendMessageEntity);
                    if (serverMessageList.size() > 0) {
                        logger.info(serverMessageList.get(0).getMessage());
                    }
                    sendServerMessage(serverMessageList);
                    break;
                case "SendGroupMessage":
                    MessageEntity groupMessageEntity = JSONObject.parseObject(body, MessageEntity.class);
                    serverMessageList = groupService.sendMessageToGroup(groupMessageEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "SearchUser":
                    SearchUserClientEntity searchUserClientEntity =
                            JSONObject.parseObject(body, SearchUserClientEntity.class);
                    serverMessageList = userService.searchUserHandler(searchUserClientEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "CreateGroup":
                    GroupCreateClientEntity entity = JSONObject.parseObject(body, GroupCreateClientEntity.class);
                    serverMessageList = groupService.groupCreateHandler(entity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetUserInfo":
                    SingleIntEntity getUserInfoSingleIntEntity = JSON.parseObject(body, SingleIntEntity.class);
                    serverMessageList = userService.getUserInfo(getUserInfoSingleIntEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetGroupMembers":
                    SingleIntEntity groupIdEntity = JSONObject.parseObject(body, SingleIntEntity.class);
                    serverMessageList = groupService.getGroupMember(groupIdEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "DeleteMessage":
                    SingleIntEntity deleteMessageSingleIntEntity = JSON.parseObject(body, SingleIntEntity.class);
                    serverMessageList = messageService.deleteMessage(deleteMessageSingleIntEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "DeleteGroupMessage":
                    SingleIntEntity deleteGroupMessageSingleIntEntity = JSON.parseObject(body, SingleIntEntity.class);
                    serverMessageList = groupService.deleteGroupMessage(deleteGroupMessageSingleIntEntity);
                    sendServerMessage(serverMessageList);
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
            jsonException.printStackTrace();
        }
        catch (IOException ioException) {
            logger.error("ioException");
            ioException.printStackTrace();
        }
        catch (DAOException daoException) {
            ErrorEntity errorEntity = new ErrorEntity();
            errorEntity.setErrorInfo("数据库错误");
            sendMessageBody = JSON.toJSONString(errorEntity);
            sendMessageHead = "Error";
            sendMessageDirect(sendMessageHead, sendMessageBody);
            daoException.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
        if (id != 0){
            logger.error(String.format("与%d的会话发生错误",id));
        }
        else {
            logger.error(String.format("与%s的会话发生错误",session.getId()));
        }
        error.printStackTrace();
    }

    private void sendMessageDirect(String head, String body) {
        String message = head + "|" + body;
        session.getAsyncRemote().sendText(message, sendResult -> {
            if (!sendResult.isOK()){
                logger.error(sendResult.getException().getMessage());
            }
            else {
                logger.info(String.format("向客户端:%d发送了消息:%s",id, message));
            }
        });

    }

    public void close(CloseReason closeReason) {
        try {
            session.close(closeReason);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMessageDirect(String message) {
        session.getAsyncRemote().sendText(message, sendResult -> {
            if (!sendResult.isOK()){
                logger.error(sendResult.getException().getMessage());
            }
            else {
                logger.info(String.format("向客户端:%d发送了消息:%s",id, message));
            }
        });

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
