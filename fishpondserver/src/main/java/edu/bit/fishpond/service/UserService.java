package edu.bit.fishpond.service;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpond.service.entity.*;
import edu.bit.fishpond.utils.DAOException;
import edu.bit.fishpond.utils.SecureForServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service("UserService")
public class UserService {

    @Autowired
    public UserService(@Qualifier("IServiceDaoImpl") IServiceDao iServiceDao){
        this.iServiceDao = iServiceDao;
        this.iServiceDao.clearDAO();
    }

    private final IServiceDao iServiceDao;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<ServerMessage> registerHandler(RegisterClientEntity registerClientEntity)
            throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        String userName = registerClientEntity.getUserName();
        String password = registerClientEntity.getPassword();
        String securityQuestion = registerClientEntity.getSecurityQuestion();
        String answer = registerClientEntity.getAnswer();
        //String passwordDecrypt = SecureForServer.decryptRSA(password, privateKey);

        // 获取新用户的id
        int id = iServiceDao.recordNewUser(userName, password, securityQuestion, answer);
        SingleIntEntity singleIntEntity = new SingleIntEntity();
        singleIntEntity.setUserId(id);
        String sendMessageBody = JSON.toJSONString(singleIntEntity);

        serverMessageList.add(new ServerMessage(0, "RegisterResult", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> sendFriendRequestHandler(FriendRequestClientEntity entity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int applierId = entity.getApplierId();
        int recipientId = entity.getRecipientId();
        String explain = entity.getExplain();
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = currentTime.format(dateTimeFormatter);
        boolean onlineStatus = iServiceDao.queryOnlineStatusById(recipientId);
        logger.info("用户:" + recipientId + "的在线状态为" + onlineStatus);
        String sendMessageBody;

        boolean hasRequest = iServiceDao.checkFriendRequestExist(applierId, recipientId);
        if (hasRequest){
            logger.info(String.valueOf(true));
            return serverMessageList;
        }

        //添加新的好友申请
        iServiceDao.recordNewFriendRequest(applierId, recipientId, explain, currentTimeString);

        //如果接收者在线
        if (onlineStatus){
            //发送
            FriendRequestServerEntity friendRequestServerEntity = new FriendRequestServerEntity();
            friendRequestServerEntity.setApplierId(applierId);
            friendRequestServerEntity.setSendTime(currentTimeString);
            friendRequestServerEntity.setExplain(explain);
            UserInfoServerEntity userInfoServerEntity = getUserInfoById(applierId);
            friendRequestServerEntity.setApplierName(userInfoServerEntity.getUsername());
            sendMessageBody = JSON.toJSONString(friendRequestServerEntity);
            serverMessageList.add(new ServerMessage(recipientId, "NewFriendRequest", sendMessageBody));
        }

        return serverMessageList;
    }

    public List<ServerMessage> getUnreadFriendRequestHandler(SingleIntEntity singleIntEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int loginId = singleIntEntity.getUserId();
        List<String> queryResult = iServiceDao.queryFriendRequestList(loginId);
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

    public List<ServerMessage> getFriendRequestFeedbackHandler(FriendRequestFeedbackClientEntity entity)
            throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int senderId = entity.getSenderId();
        int recipientId = entity.getRecipientId();
        boolean requestResult = entity.getResult();

        LocalDateTime currentTime = LocalDateTime.now();
        boolean applierOnlineStatus = iServiceDao.queryOnlineStatusById(senderId);
        boolean recipientOnlineStatus = iServiceDao.queryOnlineStatusById(recipientId);
        String sendMessageBody;

        //收到反馈后即可删除好友申请
        iServiceDao.deleteFriendRequest(senderId, recipientId);
        //如果接受申请,则成为好友
        if (requestResult){
            iServiceDao.recordNewFriendship(senderId, recipientId, currentTime.toString());

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
                iServiceDao.recordSystemMessage(senderId, currentTime.toString(), "", "你的好友申请已被拒绝");
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

    public String getFriendList(SingleIntEntity singleIntEntity) {
        int id = singleIntEntity.getUserId();
        List<Integer> queryResult = iServiceDao.queryFriendList(id);
        List<UserInfoServerEntity> friendList = new ArrayList<>();

        for (int friendId : queryResult){
            UserInfoServerEntity friendInfoServerEntity = getUserInfoById(friendId);
            friendList.add(friendInfoServerEntity);
        }

        return JSON.toJSONString(friendList);
    }

    public List<ServerMessage> searchUserHandler(SearchUserClientEntity searchUserClientEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        List<UserInfoServerEntity> resultList = new ArrayList<>();

        String searchInput = searchUserClientEntity.getSearchInput();
        String sendMessageBody;

        //将搜索信息作为用户名的子串查询
        List<String> queryResult = iServiceDao.queryUserInfoByName(searchInput);

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
            String queryLine = iServiceDao.queryUserInfoById(id);
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

    public List<ServerMessage> getUserInfo(SingleIntEntity singleIntEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int id = singleIntEntity.getUserId();
        UserInfoServerEntity userInfoServerEntity = getUserInfoById(id);
        String sendMessageBody = JSON.toJSONString(userInfoServerEntity);
        serverMessageList.add(new ServerMessage(0, "UserInfo",sendMessageBody));

        return serverMessageList;
    }

    private UserInfoServerEntity getUserInfoById(int id){
        UserInfoServerEntity userInfoServerEntity = new UserInfoServerEntity();
        String queryLine = iServiceDao.queryUserInfoById(id);
        logger.info("queryUserInfoById id:" + id);
        logger.info("queryUserInfoById result:" + queryLine);
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
