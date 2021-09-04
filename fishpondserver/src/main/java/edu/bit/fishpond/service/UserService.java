package edu.bit.fishpond.service;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpond.service.entity.*;
import edu.bit.fishpond.utils.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("UserService")
public class UserService {

    @Autowired
    public UserService(IServiceDao serviceDao){
        this.serviceDao = serviceDao;
    }

    private final IServiceDao serviceDao;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<ServerMessage> registerHandler(RegisterClientEntity registerClientEntity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        String userName = registerClientEntity.getUserName();
        String password = registerClientEntity.getPassword();
        String securityQuestion = registerClientEntity.getSecurityQuestion();
        String answer = registerClientEntity.getAnswer();

        // 获取新用户的id
        int id = serviceDao.recordNewUser(userName, password, securityQuestion, answer);
        UserIdEntity userIdEntity = new UserIdEntity();
        userIdEntity.setUserId(id);
        String sendMessageBody = JSON.toJSONString(userIdEntity);

        serverMessageList.add(new ServerMessage(0, "RegisterResult", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> sendFriendRequestHandler(FriendRequestClientEntity entity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int applierId = entity.getApplierId();
        int recipientId = entity.getRecipientId();
        String explain = entity.getExplain();
        LocalDateTime currentTime = LocalDateTime.now();
        boolean onlineStatus = serviceDao.queryOnlineStatusById(recipientId);
        String sendMessageBody;

        //添加新的好友申请
        serviceDao.recordFriendRequest(applierId, recipientId,explain, currentTime.toString());

        //如果接收者在线
        if (onlineStatus){
            //发送
            FriendRequestServerEntity friendRequestServerEntity = new FriendRequestServerEntity();
            friendRequestServerEntity.setApplierId(applierId);
            friendRequestServerEntity.setSendTime(currentTime.toString());
            friendRequestServerEntity.setExplain(explain);
            sendMessageBody = JSON.toJSONString(friendRequestServerEntity);
            serverMessageList.add(new ServerMessage(recipientId, "NewFriendRequest", sendMessageBody));
        }

        return serverMessageList;
    }

    public String getAllMessage(UserIdEntity userIdEntity){
        int id = userIdEntity.getUserId();
        List<String> queryResult = serviceDao.queryAllMessage(id);
        List<MessageEntity> messageList = new ArrayList<>();

        if (!queryResult.isEmpty()){
            for (String queryLine : queryResult){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 5){
                    MessageEntity messageEntity = new MessageEntity();
                    int senderId = Integer.parseInt(queryDataArray[0]);
                    String senderName = "";
                    messageEntity.setSenderId(senderId);
                    String queryResult2 = serviceDao.queryUserInfoById(senderId);
                    String[] querySplitArray2 = queryResult2.split("#");
                    if (querySplitArray2.length == 6){
                        senderName = querySplitArray2[1];
                    }
                    messageEntity.setSenderName(senderName);
                    int recipientId = Integer.parseInt(queryDataArray[1]);
                    String recipientName = "";
                    messageEntity.setRecipientId(recipientId);
                    String queryResult3 = serviceDao.queryUserInfoById(recipientId);
                    String[] querySplitArray3 = queryResult3.split("#");
                    if (querySplitArray3.length == 6){
                        recipientName = querySplitArray3[1];
                    }
                    messageEntity.setRecipientName(recipientName);
                    messageEntity.setMessageType(queryDataArray[2]);
                    messageEntity.setSendTime(queryDataArray[3]);
                    messageEntity.setMessageContent(queryDataArray[4]);


                    messageList.add(messageEntity);
                }
                else {
                    logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为5",
                            queryLine,queryDataArray.length));
                }
            }
        }

        return JSON.toJSONString(messageList);
    }

    public String getLatestMessage(UserIdEntity userIdEntity){
        int id = userIdEntity.getUserId();
        List<String> queryResult = serviceDao.queryLatestMessage(id);
        List<MessageEntity> messageList = getMessageEntityList(queryResult);

        return JSON.toJSONString(messageList);
    }

    public List<ServerMessage> getUnreadFriendRequestHandler(UserIdEntity userIdEntity){
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int loginId = userIdEntity.getUserId();
        List<String> queryResult = serviceDao.queryFriendRequest(loginId);
        List<FriendRequestServerEntity> friendRequestList = new ArrayList<>();
        if (!queryResult.isEmpty()){
            for (String queryLine: queryResult) {
                //解析dataLine，将解析结果转化成JSON
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 3){
                    FriendRequestServerEntity friendRequestServerEntity = new FriendRequestServerEntity();
                    //获取申请人Id
                    int applierId = Integer.parseInt(queryDataArray[0]);
                    friendRequestServerEntity.setApplierId(applierId);
                    friendRequestServerEntity.setSendTime(queryDataArray[1]);
                    friendRequestServerEntity.setExplain(queryDataArray[2]);
                    //获取申请人的用户名
                    UserInfoServerEntity userInfoServerEntity = getUserInfoById(applierId);
                    friendRequestServerEntity.setApplierName(userInfoServerEntity.getUsername());
                    friendRequestList.add(friendRequestServerEntity);
                }
                else {
                    logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为3",
                            queryLine,queryDataArray.length));
                }

            }
        }

        String sendMessageBody = JSON.toJSONString(friendRequestList);
        serverMessageList.add(new ServerMessage(0, "UnreadFriendRequest", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> getFriendRequestFeedbackHandler(FriendRequestFeedbackClientEntity entity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int senderId = entity.getSenderId();
        int recipientId = entity.getRecipientId();
        boolean requestResult = entity.getResult();

        LocalDateTime currentTime = LocalDateTime.now();
        boolean applierOnlineStatus = serviceDao.queryOnlineStatusById(senderId);
        boolean recipientOnlineStatus = serviceDao.queryOnlineStatusById(recipientId);
        String sendMessageBody;

        //收到反馈后即可删除好友申请
        serviceDao.deleteFriendRequest(senderId, recipientId);
        //如果接受申请,则成为好友
        if (requestResult){
            serviceDao.recordFriendship(senderId, recipientId, currentTime.toString());

            //如果申请人在线
            if (applierOnlineStatus){
                //获取新的好友，即接收人的信息
                UserInfoServerEntity userInfo = getUserInfoById(recipientId);
                sendMessageBody = JSON.toJSONString(userInfo);
                serverMessageList.add(new ServerMessage(senderId, "NewFriend", sendMessageBody));

            }
            //如果接收人在线
            if (recipientOnlineStatus){
                //获取新的好友，即申请人的信息
                UserInfoServerEntity userInfo = getUserInfoById(senderId);
                sendMessageBody = JSON.toJSONString(userInfo);
                serverMessageList.add(new ServerMessage(0, "NewFriend", sendMessageBody));
            }


        }
        else {
            if (!applierOnlineStatus){
                serviceDao.recordSystemMessage(senderId, currentTime.toString(), "", "你的好友申请已被拒绝");
            }
            else {
                SystemMessageEntity systemMessageEntity = new SystemMessageEntity();
                systemMessageEntity.setUserId(senderId);
                systemMessageEntity.setSendTime(currentTime.toString());
                systemMessageEntity.setMessageType("");
                systemMessageEntity.setContent("你的好友申请已被拒绝");
                sendMessageBody = JSON.toJSONString(systemMessageEntity);
                serverMessageList.add(new ServerMessage(senderId, "SystemMessage", sendMessageBody));
            }
        }

        return serverMessageList;
    }

    public String getFriendList(UserIdEntity userIdEntity){
        int id = userIdEntity.getUserId();
        List<Integer> queryResult = serviceDao.queryFriendshipById(id);
        List<UserInfoServerEntity> friendList = new ArrayList<>();

        for (int friendId : queryResult){
            UserInfoServerEntity friendInfoServerEntity = getUserInfoById(friendId);
            friendList.add(friendInfoServerEntity);
        }

        return JSON.toJSONString(friendList);
    }

    public List<ServerMessage> searchUserHandler(SearchUserClientEntity searchUserClientEntity){
        List<ServerMessage> serverMessageList = new ArrayList<>();

        List<UserInfoServerEntity> resultList = new ArrayList<>();

        String searchInput = searchUserClientEntity.getSearchInput();
        String sendMessageBody;

        //将搜索信息作为用户名的子串查询
        List<String> queryResult = serviceDao.queryUserInfoByName(searchInput);

        if (!queryResult.isEmpty()){
            for (String queryLine : queryResult){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 6){
                    UserInfoServerEntity userInfoServerEntity = new UserInfoServerEntity();
                    userInfoServerEntity.setUserId(Integer.parseInt(queryDataArray[0]));
                    userInfoServerEntity.setUsername(queryDataArray[1]);
                    userInfoServerEntity.setSex(queryDataArray[2]);
                    userInfoServerEntity.setBirthday(queryDataArray[3]);
                    userInfoServerEntity.setRegion(queryDataArray[4]);
                    userInfoServerEntity.setAvatarUrl("");
                    resultList.add(userInfoServerEntity);
                }
                else {
                    logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为6",
                            queryLine,queryDataArray.length));
                }
            }
        }
        //将搜索信息作为Id查询
        int id;
        try {
            id = Integer.parseInt(searchInput);
            String queryLine = serviceDao.queryUserInfoById(id);
            if (!queryLine.isEmpty()){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 6){
                    UserInfoServerEntity userInfoServerEntity = new UserInfoServerEntity();
                    userInfoServerEntity.setUserId(id);
                    userInfoServerEntity.setUsername(queryDataArray[1]);
                    userInfoServerEntity.setSex(queryDataArray[2]);
                    userInfoServerEntity.setBirthday(queryDataArray[3]);
                    userInfoServerEntity.setRegion(queryDataArray[4]);
                    userInfoServerEntity.setRegisterTime(queryDataArray[5]);
                    resultList.add(userInfoServerEntity);
                }
                else {
                    logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为6",
                            queryLine,queryDataArray.length));
                }
            }

        }
        catch (NumberFormatException ignored){

        }
        finally {
            sendMessageBody = JSON.toJSONString(resultList);
        }

        serverMessageList.add(new ServerMessage(0, "SearchUserResult", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> sendMessageHandler(MessageEntity messageEntity) throws DAOException{
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int senderId = messageEntity.getSenderId();
        int recipientId = messageEntity.getRecipientId();
        String sendTime = LocalDateTime.now().toString();
        String messageType = messageEntity.getMessageType();
        String messageContent = messageEntity.getMessageContent();

        //将消息写入
        serviceDao.recordMessage(senderId, recipientId, messageType, sendTime, messageContent);
        boolean queryResult = serviceDao.queryOnlineStatusById(recipientId);
        // 如果接收者在线
        if (queryResult) {
            String sendMessageBody = JSON.toJSONString(messageEntity);
            serverMessageList.add(new ServerMessage(recipientId, "NewMessage", sendMessageBody));
        }

        return serverMessageList;
    }

    public List<ServerMessage> getUnreadMessage(UserIdEntity userIdEntity){
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int id = userIdEntity.getUserId();
        List<String> queryResult = serviceDao.getUnreadMessage(id);
        List<MessageEntity> messageEntityList = getMessageEntityList(queryResult);

        String sendMessageBody = JSON.toJSONString(messageEntityList);
        serverMessageList.add(new ServerMessage(0, "UnreadMessage", sendMessageBody));
        return serverMessageList;
    }

    public List<ServerMessage> getAllMessageBetweenHandler(PersonMessageClientEntity entity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int id1 = entity.getUserId1();
        int id2 = entity.getUserId2();

        List<String> queryResult = serviceDao.queryAllMessageBetween(id1, id2);
        List<MessageEntity> messageList = getMessageEntityList(queryResult);
        String sendMessageBody = JSON.toJSONString(messageList);

        serverMessageList.add(new ServerMessage(0, "MessageBetween", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> getUserInfo(UserIdEntity userIdEntity){
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int id = userIdEntity.getUserId();
        UserInfoServerEntity userInfoServerEntity = getUserInfoById(id);
        String sendMessageBody = JSON.toJSONString(userInfoServerEntity);
        serverMessageList.add(new ServerMessage(0, "UserInfo",sendMessageBody));

        return serverMessageList;
    }

    private List<MessageEntity> getMessageEntityList(List<String> queryResult){
        List<MessageEntity> messageList = new ArrayList<>();
        //对查询出来的每条Message
        for (String queryLine : queryResult){
            //通过#分解
            String[] queryDataArray = queryLine.split("#");
            if (queryDataArray.length == 5){
                MessageEntity messageEntity = new MessageEntity();
                //得到senderId
                int senderId = Integer.parseInt(queryDataArray[0]);

                //根据senderId得到senderName
                String senderName = "";
                messageEntity.setSenderId(senderId);
                String queryResult2 = serviceDao.queryUserInfoById(senderId);
                String[] querySplitArray2 = queryResult2.split("#");
                if (querySplitArray2.length == 6){
                    senderName = querySplitArray2[1];
                }
                messageEntity.setSenderName(senderName);

                //得到recipientId
                int recipientId = Integer.parseInt(queryDataArray[1]);

                //根据recipientId得到recipientName
                String recipientName = "";
                messageEntity.setRecipientId(recipientId);
                String queryResult3 = serviceDao.queryUserInfoById(recipientId);
                String[] querySplitArray3 = queryResult3.split("#");
                if (querySplitArray3.length == 6){
                    recipientName = querySplitArray3[1];
                }


                messageEntity.setRecipientName(recipientName);
                messageEntity.setMessageType(queryDataArray[2]);
                messageEntity.setSendTime(queryDataArray[3]);
                messageEntity.setMessageContent(queryDataArray[4]);

                messageList.add(messageEntity);
            }
            else {
                logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为5",
                        queryLine,queryDataArray.length));
            }
        }

        return messageList;
    }

    private UserInfoServerEntity getUserInfoById(int id){
        UserInfoServerEntity userInfoServerEntity = new UserInfoServerEntity();
        String queryLine = serviceDao.queryUserInfoById(id);
        logger.info(String.valueOf(id));
        logger.info(queryLine);
        if (!queryLine.isEmpty()){
            String[] queryDataArray = queryLine.split("#",-1);
            if (queryDataArray.length == 6){
                userInfoServerEntity.setUserId(id);
                userInfoServerEntity.setUsername(queryDataArray[1]);
                userInfoServerEntity.setSex(queryDataArray[2]);
                userInfoServerEntity.setBirthday(queryDataArray[3]);
                userInfoServerEntity.setRegion(queryDataArray[4]);
                userInfoServerEntity.setRegisterTime(queryDataArray[5]);
                userInfoServerEntity.setAvatarUrl("");
            }
            else {
                logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为6",
                        queryLine,queryDataArray.length));
            }
        }
        return userInfoServerEntity;
    }

}
