package edu.bit.fishpond.service;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpond.server.WebSocketServer;
import edu.bit.fishpond.service.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

@Service("UserService")
public class UserService {

    @Autowired
    public UserService(@Qualifier("serviceDao") IServiceDao userDao){
        this.userDao = userDao;
    }

    private final IServiceDao userDao;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public String register(RegisterClientEntity registerClientEntity){
        String userName = registerClientEntity.getUserName();
        String password = registerClientEntity.getPassword();
        String securityQuestion = registerClientEntity.getSecurityQuestion();
        String answer = registerClientEntity.getAnswer();

        // 获取新用户的id
        int id = userDao.recordNewUser(userName, password, securityQuestion, answer);
        UserIdEntity userIdEntity = new UserIdEntity();
        userIdEntity.setUserId(id);
        return JSON.toJSONString(userIdEntity);
    }

    public String friendRequestHandler(FriendRequestGetEntity friendRequestGetEntity){
        int applierId = friendRequestGetEntity.getApplierId();
        int recipientId = friendRequestGetEntity.getRecipientId();
        String explain = friendRequestGetEntity.getExplain();
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String sendTime = dateFormat.toString();
        boolean onlineStatus = userDao.queryOnlineStatusById(recipientId);
        String sendMessageBody = "";

        //添加新的好友申请
        userDao.recordFriendRequest(applierId, recipientId,explain, sendTime);

        //异常处理
        //logger.error(String.format("数据层错误，无法添加该好友申请: applierId:%d,recipientId:%d,explain:%s,sendTime:%s",
        //applierId,recipientId,explain,sendTime));
        //ErrorEntity errorEntity = new ErrorEntity();
        //errorEntity.setErrorInfo("服务器错误，无法添加好友");
        //sendMessageBody = JSON.toJSONString(errorEntity);

        //如果接收者在线
        if (onlineStatus){
            //发送
            FriendRequestSendEntity friendRequestSendEntity = new FriendRequestSendEntity();
            friendRequestSendEntity.setSenderId(applierId);
            friendRequestSendEntity.setSendTime(sendTime);
            friendRequestSendEntity.setExplain(explain);
            sendMessageBody = JSON.toJSONString(friendRequestSendEntity);
            return sendMessageBody;
        }

        return sendMessageBody;
    }

    public boolean login(LoginClientEntity loginClientEntity){
        int loginId = loginClientEntity.getLoginUserId();
        String passwordHash = loginClientEntity.getPasswordHash();
        boolean queryResult = userDao.checkPassword(loginId, passwordHash);
        if (queryResult){
            userDao.updateOnlineStatusById(loginId);
        }


        return queryResult;
    }

    public String getAllMessage(UserIdEntity userIdEntity){
        int id = userIdEntity.getUserId();
        List<String> queryResult = userDao.queryAllMessage(id);
        List<MessageEntity> messageList = new ArrayList<>();

        if (!queryResult.isEmpty()){
            for (String queryLine : queryResult){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 4){
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setSenderId(Integer.parseInt(queryDataArray[0]));
                    messageEntity.setMessageType(queryDataArray[1]);
                    messageEntity.setSendTime(queryDataArray[2]);
                    messageEntity.setMessageContent(queryDataArray[3]);
                    messageList.add(messageEntity);
                }
                else {
                    logger.error(String.format("无法解析:%s",queryLine));
                }
            }
        }

        return JSON.toJSONString(messageList);
    }

    public String getFriendRequest(int loginId){
        List<String> queryResult = userDao.queryFriendRequest(loginId);
        List<FriendRequestSendEntity> friendRequestList = new ArrayList<>();
        if (!queryResult.isEmpty()){
            for (String queryLine: queryResult) {
                //解析dataLine，将解析结果转化成JSON
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 3){
                    FriendRequestSendEntity friendRequestSendEntity = new FriendRequestSendEntity();
                    friendRequestSendEntity.setSenderId(Integer.parseInt(queryDataArray[0]));
                    friendRequestSendEntity.setSendTime(queryDataArray[1]);
                    friendRequestSendEntity.setExplain(queryDataArray[2]);
                    friendRequestList.add(friendRequestSendEntity);
                }
                else {
                    logger.error(String.format("无法解析:%s",queryLine));
                }

            }
        }

        return JSON.toJSONString(friendRequestList);
    }

    public String FriendRequestFeedbackHandler(FRFGetEntity frfGetEntity){
        int senderId = frfGetEntity.getSenderId();
        int recipientId = frfGetEntity.getRecipientId();
        boolean requestResult = frfGetEntity.getResult();

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String currentTime = dateFormat.toString();
        boolean applierOnlineStatus = userDao.queryOnlineStatusById(senderId);
        boolean recipientOnlineStatus = userDao.queryOnlineStatusById(recipientId);
        String sendMessageBody = "";
        //如果接受申请,则成为好友
        if (requestResult){
            userDao.recordFriendship(senderId, recipientId, currentTime);

            //如果申请人在线
            if (applierOnlineStatus){
                //获取新的好友列表
                UserIdEntity userIdEntity = new UserIdEntity();
                userIdEntity.setUserId(senderId);
                sendMessageBody = FriendListService(userIdEntity);
            }
            if (recipientOnlineStatus){
                UserIdEntity userIdEntity = new UserIdEntity();
                userIdEntity.setUserId(recipientId);
                WebSocketServer.SendMessageTo(recipientId, "FriendList",FriendListService(userIdEntity));
            }


        }
        else {
            if (!applierOnlineStatus){
                userDao.recordSystemMessage(senderId, currentTime, "", "你的好友申请已被拒绝");
            }
            else {
                SystemMessageEntity systemMessageEntity = new SystemMessageEntity();
                systemMessageEntity.setUserId(senderId);
                systemMessageEntity.setSendTime(currentTime);
                systemMessageEntity.setMessageType("");
                systemMessageEntity.setContent("你的好友申请已被拒绝");
                sendMessageBody = JSON.toJSONString(systemMessageEntity);
            }
        }

        return sendMessageBody;
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
        List<SearchUserServerEntity> resultList = new ArrayList<>();

        String searchInput = searchUserClientEntity.getSearchInput();

        //将搜索信息作为用户名的子串查询
        List<String> queryResult = userDao.queryUserInfoByName(searchInput);

        if (!queryResult.isEmpty()){
            for (String queryLine : queryResult){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 5){
                    SearchUserServerEntity searchUserServerEntity = new SearchUserServerEntity();
                    searchUserServerEntity.setUserId(Integer.parseInt(queryDataArray[0]));
                    searchUserServerEntity.setUsername(queryDataArray[1]);
                    searchUserServerEntity.setSex(queryDataArray[2]);
                    searchUserServerEntity.setBirthday(queryDataArray[3]);
                    searchUserServerEntity.setRegion(queryDataArray[4]);
                    resultList.add(searchUserServerEntity);
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
        if (queryDataArray.length == 5){
            SearchUserServerEntity searchUserServerEntity = new SearchUserServerEntity();
            searchUserServerEntity.setUserId(Integer.parseInt(queryDataArray[0]));
            searchUserServerEntity.setUsername(queryDataArray[1]);
            searchUserServerEntity.setSex(queryDataArray[2]);
            searchUserServerEntity.setBirthday(queryDataArray[3]);
            searchUserServerEntity.setRegion(queryDataArray[4]);
            resultList.add(searchUserServerEntity);
        }
        else {
            logger.error(String.format("无法解析:%s",queryLine));
        }

        return JSON.toJSONString(resultList);
    }

}
