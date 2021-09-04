package edu.bit.fishpond.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import edu.bit.fishpond.service.ConnectService;
import edu.bit.fishpond.service.ServerMessage;
import edu.bit.fishpond.service.entity.*;
import edu.bit.fishpond.service.UserService;
import edu.bit.fishpond.utils.DAOException;
import edu.bit.fishpond.utils.MessageHeadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;

import static javax.websocket.CloseReason.CloseCodes.GOING_AWAY;

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
        logger.info(String.format("与%s建立新的会话", session.getId()));
    }

    @OnClose
    public void onClose(CloseReason closeReason){
        if (id != 0) {
            WebSocketServer.DisConnect(id);
            logger.info(String.format("与%d的会话已关闭",id));
        }
        else {
            logger.info(String.format("与%s的会话已关闭",session.getId()));
        }
        logger.info("关闭原因：" + closeReason.getCloseCode() + ",关闭原因描述：" + closeReason.getReasonPhrase());
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
                    serverMessageList = userService.registerHandler(registerClientEntity);
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
                    UserIdEntity userIdEntity = JSONObject.parseObject(body, UserIdEntity.class);
                    sendMessageBody = userService.getFriendList(userIdEntity);
                    sendMessageHead = "FriendList";
                    sendMessageDirect(sendMessageHead, sendMessageBody);
                    break;
                case "GetLatestMessage":
                    UserIdEntity getLatestMessageUserIdEntity = JSONObject.parseObject(body,UserIdEntity.class);
                    sendMessageBody = userService.getLatestMessage(getLatestMessageUserIdEntity);
                    sendMessageHead = "LatestMessage";
                    sendMessageDirect(sendMessageHead, sendMessageBody);
                    break;
                case "GetUnreadMessage":
                    UserIdEntity getUnreadMessageUserIdEntity = JSONObject.parseObject(body, UserIdEntity.class);
                    serverMessageList = userService.getUnreadMessage(getUnreadMessageUserIdEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetUnreadFriendRequest":
                    UserIdEntity getUnreadFriendRequest = JSONObject.parseObject(body, UserIdEntity.class);
                    serverMessageList = userService.getUnreadFriendRequestHandler(getUnreadFriendRequest);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetMessageBetween":
                    PersonMessageClientEntity personMessageClientEntity =
                            JSONObject.parseObject(body,PersonMessageClientEntity.class);
                    serverMessageList = userService.getAllMessageBetweenHandler(personMessageClientEntity);
                    sendServerMessage(serverMessageList);
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
                    serverMessageList = userService.sendMessageHandler(sendMessageEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "SearchUser":
                    SearchUserClientEntity searchUserClientEntity =
                            JSONObject.parseObject(body, SearchUserClientEntity.class);
                    serverMessageList = userService.searchUserHandler(searchUserClientEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "GetUserInfo":
                    UserIdEntity getUserInfoUserIdEntity = JSON.parseObject(body, UserIdEntity.class);
                    serverMessageList = userService.getUserInfo(getUserInfoUserIdEntity);
                    sendServerMessage(serverMessageList);
                    break;
                case "OffLine":
                    UserIdEntity offLineUserIdEntity = JSONObject.parseObject(body, UserIdEntity.class);
                    connectService.offLine(offLineUserIdEntity);
                    onClose(new CloseReason(GOING_AWAY,"账号在别的位置登录"));
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
        catch (IOException ioException){
            logger.error("ioException");
        }
        catch (DAOException daoException) {
            ErrorEntity errorEntity = new ErrorEntity();
            errorEntity.setErrorInfo("数据库错误");
            sendMessageBody = JSON.toJSONString(errorEntity);
            sendMessageHead = "Error";
            sendMessageDirect(sendMessageHead, sendMessageBody);
        }
    }

    @OnError
    public void onError(Session session, Throwable error){
        if (id != 0){
            logger.error(String.format("与%d的会话发生错误",id));
        }
        else {
            logger.error(String.format("与%s的会话发生错误",session.getId()));
        }
        error.printStackTrace();
    }

    public void sendMessageDirect(String head, String body) {
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
