package edu.bit.fishpond.service;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpond.service.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("UserService")
public class UserService {

    @Autowired
    public UserService(@Qualifier("serviceDao") IServiceDao userDao){
        this.userDao = userDao;
    }

    private final IServiceDao userDao;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public ServiceResult register(RegisterClientEntity registerClientEntity){
        String userName = registerClientEntity.getUserName();
        String password = registerClientEntity.getPassword();
        String securityQuestion = registerClientEntity.getSecurityQuestion();
        String answer = registerClientEntity.getAnswer();

        // 获取新用户的id
        int id = userDao.recordNewUser(userName, password, securityQuestion, answer);
        UserIdEntity userIdEntity = new UserIdEntity();
        userIdEntity.setUserId(id);
        String sendMessageBody = JSON.toJSONString(userIdEntity);

        //返回结果
        ServiceResult result = new ServiceResult();
        Map<Integer, String> senderMessageMap = new HashMap<>();
        senderMessageMap.put(0, "RegisterResult" + "|" + sendMessageBody);
        result.setSendMessage(true);
        result.setSenderMessageMap(senderMessageMap);
        return result;
    }

    public List<ServerMessage> register2(RegisterClientEntity registerClientEntity){
        List<ServerMessage> serverMessageList = new ArrayList<>();

        String userName = registerClientEntity.getUserName();
        String password = registerClientEntity.getPassword();
        String securityQuestion = registerClientEntity.getSecurityQuestion();
        String answer = registerClientEntity.getAnswer();

        // 获取新用户的id
        int id = userDao.recordNewUser(userName, password, securityQuestion, answer);
        UserIdEntity userIdEntity = new UserIdEntity();
        userIdEntity.setUserId(id);
        String sendMessageBody = JSON.toJSONString(userIdEntity);

        serverMessageList.add(new ServerMessage(0, "RegisterResult", sendMessageBody));

        return serverMessageList;
    }

    public String friendRequestHandler(FriendRequestClientEntity friendRequestClientEntity){
        int applierId = friendRequestClientEntity.getApplierId();
        int recipientId = friendRequestClientEntity.getRecipientId();
        String explain = friendRequestClientEntity.getExplain();
        LocalDateTime currentTime = LocalDateTime.now();
        boolean onlineStatus = userDao.queryOnlineStatusById(recipientId);
        String sendMessageBody = "";

        //添加新的好友申请
        userDao.recordFriendRequest(applierId, recipientId,explain, currentTime.toString());

        //异常处理
        //logger.error(String.format("数据层错误，无法添加该好友申请: applierId:%d,recipientId:%d,explain:%s,sendTime:%s",
        //applierId,recipientId,explain,sendTime));
        //ErrorEntity errorEntity = new ErrorEntity();
        //errorEntity.setErrorInfo("服务器错误，无法添加好友");
        //sendMessageBody = JSON.toJSONString(errorEntity);

        //如果接收者在线
        if (onlineStatus){
            //发送
            FriendRequestServerEntity friendRequestServerEntity = new FriendRequestServerEntity();
            friendRequestServerEntity.setApplierId(applierId);
            friendRequestServerEntity.setSendTime(currentTime.toString());
            friendRequestServerEntity.setExplain(explain);
            sendMessageBody = JSON.toJSONString(friendRequestServerEntity);
            return sendMessageBody;
        }

        return sendMessageBody;
    }

    public String getAllMessage(UserIdEntity userIdEntity){
        int id = userIdEntity.getUserId();
        List<String> queryResult = userDao.queryAllMessage(id);
        List<MessageEntity> messageList = new ArrayList<>();

        if (!queryResult.isEmpty()){
            for (String queryLine : queryResult){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 5){
                    MessageEntity messageEntity = new MessageEntity();
                    int senderId = Integer.parseInt(queryDataArray[0]);
                    String senderName = "";
                    messageEntity.setSenderId(senderId);
                    String queryResult2 = userDao.queryUserInfoById(senderId);
                    String[] querySplitArray2 = queryResult2.split("#");
                    if (querySplitArray2.length == 6){
                        senderName = querySplitArray2[1];
                    }
                    messageEntity.setSenderName(senderName);
                    int recipientId = Integer.parseInt(queryDataArray[1]);
                    String recipientName = "";
                    messageEntity.setRecipientId(recipientId);
                    String queryResult3 = userDao.queryUserInfoById(recipientId);
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
                    logger.error(String.format("无法解析:%s",queryLine));
                }
            }
        }

        return JSON.toJSONString(messageList);
    }

    public String getLatestMessage(UserIdEntity userIdEntity){
        int id = userIdEntity.getUserId();
        List<String> queryResult = userDao.queryLatestMessage(id);
        List<MessageEntity> messageList = getMessageEntityList(queryResult);

        return JSON.toJSONString(messageList);
    }

    public List<ServerMessage> getFriendRequest(int loginId){
        List<ServerMessage> serverMessageList = new ArrayList<>();

        List<String> queryResult = userDao.queryFriendRequest(loginId);
        List<FriendRequestServerEntity> friendRequestList = new ArrayList<>();
        if (!queryResult.isEmpty()){
            for (String queryLine: queryResult) {
                //解析dataLine，将解析结果转化成JSON
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 3){
                    FriendRequestServerEntity friendRequestServerEntity = new FriendRequestServerEntity();
                    friendRequestServerEntity.setApplierId(Integer.parseInt(queryDataArray[0]));
                    friendRequestServerEntity.setSendTime(queryDataArray[1]);
                    friendRequestServerEntity.setExplain(queryDataArray[2]);
                    friendRequestList.add(friendRequestServerEntity);
                }
                else {
                    logger.error(String.format("无法解析:%s",queryLine));
                }

            }
        }

        String sendMessageBody = JSON.toJSONString(friendRequestList);
        serverMessageList.add(new ServerMessage(0, "FriendRequest", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> FriendRequestFeedbackHandler(FriendRequestFeedbackClientEntity entity){
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int senderId = entity.getSenderId();
        int recipientId = entity.getRecipientId();
        boolean requestResult = entity.getResult();

        LocalDateTime currentTime = LocalDateTime.now();
        boolean applierOnlineStatus = userDao.queryOnlineStatusById(senderId);
        boolean recipientOnlineStatus = userDao.queryOnlineStatusById(recipientId);
        String sendMessageBody;
        //如果接受申请,则成为好友
        if (requestResult){
            userDao.recordFriendship(senderId, recipientId, currentTime.toString());

            //如果申请人在线
            if (applierOnlineStatus){
                //获取新的好友列表
                UserIdEntity userIdEntity = new UserIdEntity();
                userIdEntity.setUserId(senderId);
                sendMessageBody = FriendListService(userIdEntity);
                serverMessageList.add(new ServerMessage(senderId, "FriendList", sendMessageBody));
            }
            if (recipientOnlineStatus){
                UserIdEntity userIdEntity = new UserIdEntity();
                userIdEntity.setUserId(recipientId);
                sendMessageBody = FriendListService(userIdEntity);
                serverMessageList.add(new ServerMessage(recipientId, "FriendList", sendMessageBody));
            }


        }
        //如果拒绝申请，向申请者发送系统信息
        else {
            if (!applierOnlineStatus){
                userDao.recordSystemMessage(senderId, currentTime.toString(), "", "你的好友申请已被拒绝");
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

    public String FriendListService(UserIdEntity userIdEntity){
        int id = userIdEntity.getUserId();
        List<String> queryResult = userDao.queryFriendshipById(id);
        List<FriendServerEntity> friendList = new ArrayList<>();

        if (!queryResult.isEmpty()){
            for (String queryLine : queryResult){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 2){
                    FriendServerEntity friendServerEntity = new FriendServerEntity();
                    friendServerEntity.setFriendId(Integer.parseInt(queryDataArray[0]));
                    friendServerEntity.setFriendName(queryDataArray[1]);
                    friendList.add(friendServerEntity);
                }
                else {
                    logger.error(String.format("无法解析:%s",queryLine));
                }
            }
        }

        return JSON.toJSONString(friendList);
    }

    public String searchUser(SearchUserClientEntity searchUserClientEntity){
        List<UserInfoServerEntity> resultList = new ArrayList<>();

        String searchInput = searchUserClientEntity.getSearchInput();

        //将搜索信息作为用户名的子串查询
        List<String> queryResult = userDao.queryUserInfoByName(searchInput);

        if (!queryResult.isEmpty()){
            for (String queryLine : queryResult){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 5){
                    UserInfoServerEntity userInfoServerEntity = new UserInfoServerEntity();
                    userInfoServerEntity.setUserId(Integer.parseInt(queryDataArray[0]));
                    userInfoServerEntity.setUsername(queryDataArray[1]);
                    userInfoServerEntity.setSex(queryDataArray[2]);
                    userInfoServerEntity.setBirthday(queryDataArray[3]);
                    userInfoServerEntity.setRegion(queryDataArray[4]);
                    resultList.add(userInfoServerEntity);
                }
                else {
                    logger.error(String.format("无法解析:%s",queryLine));
                }
            }
        }
        //将搜索信息作为Id查询
        int id;
        try {
            id = Integer.parseInt(searchInput);
        }
        catch (NumberFormatException exception){
            return JSON.toJSONString(resultList);
        }
        String queryLine = userDao.queryUserInfoById(id);
        String[] queryDataArray = queryLine.split("#");
        if (queryDataArray.length == 6){
            UserInfoServerEntity userInfoServerEntity = new UserInfoServerEntity();
            userInfoServerEntity.setUserId(Integer.parseInt(queryDataArray[0]));
            userInfoServerEntity.setUsername(queryDataArray[1]);
            userInfoServerEntity.setSex(queryDataArray[2]);
            userInfoServerEntity.setBirthday(queryDataArray[3]);
            userInfoServerEntity.setRegion(queryDataArray[4]);
            userInfoServerEntity.setRegisterTime(queryDataArray[5]);
            resultList.add(userInfoServerEntity);
        }
        else {
            logger.error(String.format("无法解析:%s",queryLine));
        }

        return JSON.toJSONString(resultList);
    }

    public ServiceResult getMessage(MessageEntity messageEntity){
        ServiceResult result = new ServiceResult();
        Map<Integer, String> map = new HashMap<>();

        int senderId = messageEntity.getSenderId();
        int recipientId = messageEntity.getRecipientId();
        String sendTime = messageEntity.getSendTime();
        String messageType = messageEntity.getMessageType();
        String messageContent = messageEntity.getMessageContent();

        //将消息写入
        userDao.recordMessage(senderId, recipientId, messageType, sendTime, messageContent);
        boolean queryResult = userDao.queryOnlineStatusById(recipientId);
        // 如果接收者在线
        if (true) {
            String sendMessageBody = JSON.toJSONString(messageEntity);
            map.put(recipientId, "NewMessage|" + sendMessageBody);
            result.setSendMessage(true);
            result.setSenderMessageMap(map);
        }

        return result;
    }

    public ServiceResult groupCreate(GroupCreateClientEntity entity){
        ServiceResult result = new ServiceResult();
        Map<Integer, String> map = new HashMap<>();

        int creatorId = entity.getCreatorId();
        String groupName = entity.getGroupName();
        List<Integer> initialMembers = entity.getInitialMembers();

        LocalDateTime currentTime = LocalDateTime.now();
        String currentTimeString = currentTime.toString();

        int groupId = userDao.recordNewGroup(groupName, creatorId, currentTimeString);
        for (int memberId: initialMembers) {
            userDao.recordNewMember(groupId, memberId, creatorId, currentTimeString);
            boolean onlineStatus = userDao.queryOnlineStatusById(memberId);
            if (onlineStatus){
                List<GroupInfoServerEntity> resultList = new ArrayList<>();
                List<String> queryResult = userDao.queryGroupByUserId(memberId);

                if (!queryResult.isEmpty()){
                    for (String queryLine : queryResult){
                        String[] queryDataArray = queryLine.split("#");
                        if (queryDataArray.length == 2){
                            GroupInfoServerEntity groupInfoServerEntity = new GroupInfoServerEntity();
                            groupInfoServerEntity.setGroupId(Integer.parseInt(queryDataArray[0]));
                            groupInfoServerEntity.setGroupName(queryDataArray[1]);
                            resultList.add(groupInfoServerEntity);
                        }
                        else {
                            logger.error(String.format("无法解析:%s",queryLine));
                        }
                    }
                }

                String sendMessageBody = JSON.toJSONString(resultList);
                map.put(memberId, "AllGroup|" + sendMessageBody);

            }
        }

        result.setSendMessage(true);
        result.setSenderMessageMap(map);
        return result;
    }

    public ServiceResult getGroupInfo(UserIdEntity entity){
        ServiceResult result = new ServiceResult();
        Map<Integer, String> map = new HashMap<>();

        List<GroupInfoServerEntity> resultList = new ArrayList<>();

        int id = entity.getUserId();
        List<String> queryResult = userDao.queryGroupByUserId(id);

        if (!queryResult.isEmpty()){
            for (String queryLine : queryResult){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 2){
                    GroupInfoServerEntity groupInfoServerEntity = new GroupInfoServerEntity();
                    groupInfoServerEntity.setGroupId(Integer.parseInt(queryDataArray[0]));
                    groupInfoServerEntity.setGroupName(queryDataArray[1]);
                    resultList.add(groupInfoServerEntity);
                }
                else {
                    logger.error(String.format("无法解析:%s",queryLine));
                }
            }
        }

        String sendMessageBody = JSON.toJSONString(resultList);
        map.put(id, "AllGroup|" + sendMessageBody);
        result.setSendMessage(true);
        result.setSenderMessageMap(map);

        return result;
    }

    public ServiceResult getAllMessageBetween(PersonMessageClientEntity entity){
        ServiceResult result = new ServiceResult();
        Map<Integer, String> map = new HashMap<>();

        int id1 = entity.getUserId1();
        int id2 = entity.getUserId2();

        List<String> queryResult = userDao.queryAllMessageBetween(id1, id2);

        List<MessageEntity> messageList = getMessageEntityList(queryResult);

        String sendMessageBody = JSON.toJSONString(messageList);
        result.setSendMessage(true);
        map.put(0, "MessageBetween|" + sendMessageBody);
        result.setSenderMessageMap(map);

        return result;

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
                String queryResult2 = userDao.queryUserInfoById(senderId);
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
                String queryResult3 = userDao.queryUserInfoById(recipientId);
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
                logger.error(String.format("无法解析:%s",queryLine));
            }
        }

        return messageList;
    }
}
