package edu.bit.fishpond.service;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpond.service.entity.*;
import edu.bit.fishpond.utils.DAOException;
import edu.bit.fishpond.utils.ErrorPackUtil;
import edu.bit.fishpond.utils.secureplus.RSAPrivateKey;
import edu.bit.fishpond.utils.secureplus.SecureForServerp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service("UserService")
public class UserService {

    @Autowired
    public UserService(@Qualifier("IServiceDaoImpl") IServiceDao iServiceDao){
        this.iServiceDao = iServiceDao;
    }

    private final IServiceDao iServiceDao;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<ServerMessage> registerHandler(RegisterClientEntity registerClientEntity, RSAPrivateKey privateKey)
            throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        String userName = registerClientEntity.getUserName();
        String password = registerClientEntity.getPassword();
        String securityQuestion = registerClientEntity.getSecurityQuestion();
        String answer = registerClientEntity.getAnswer();
        String passwordDecrypt = SecureForServerp.decryptRSA(password, privateKey);

        // 获取新用户的id
        int id = iServiceDao.recordNewUser(userName, passwordDecrypt, securityQuestion, answer);
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
            UserInfoEntity userInfoEntity = getUserInfoById(applierId);
            friendRequestServerEntity.setApplierName(userInfoEntity.getUsername());
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
                    UserInfoEntity userInfoEntity = getUserInfoById(applierId);
                    friendRequestServerEntity.setApplierName(userInfoEntity.getUsername());
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
            if (iServiceDao.checkFriendshipExist(senderId, recipientId)) {
                return serverMessageList;
            }
            iServiceDao.recordNewFriendship(senderId, recipientId, currentTime.toString());

            //如果申请人在线
            if (applierOnlineStatus){
                //获取新的好友，即接收人的信息
                UserInfoEntity userInfo = getUserInfoById(recipientId);
                sendMessageBody = JSON.toJSONString(userInfo);
                serverMessageList.add(new ServerMessage(senderId, "NewFriend", sendMessageBody));

            }
            //如果接收人在线
            if (recipientOnlineStatus){
                //获取新的好友，即申请人的信息
                UserInfoEntity userInfo = getUserInfoById(senderId);
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
        List<UserInfoEntity> friendList = new ArrayList<>();

        for (int friendId : queryResult){
            UserInfoEntity friendInfoServerEntity = getUserInfoById(friendId);
            friendList.add(friendInfoServerEntity);
        }

        return JSON.toJSONString(friendList);
    }

    public List<ServerMessage> searchUserHandler(SearchUserClientEntity searchUserClientEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        List<UserInfoEntity> resultList = new ArrayList<>();

        String searchInput = searchUserClientEntity.getSearchInput();
        String sendMessageBody;

        //将搜索信息作为用户名的子串查询
        List<String> queryResult = iServiceDao.queryUserInfoByName(searchInput);

        if (!queryResult.isEmpty()){
            for (String queryLine : queryResult){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 6){
                    UserInfoEntity userInfoEntity = new UserInfoEntity();
                    userInfoEntity.setUserId(Integer.parseInt(queryDataArray[0]));
                    userInfoEntity.setUsername(queryDataArray[1]);
                    userInfoEntity.setSex(queryDataArray[2]);
                    userInfoEntity.setBirthday(queryDataArray[3]);
                    userInfoEntity.setRegion(queryDataArray[4]);
                    userInfoEntity.setAvatarUrl("");
                    resultList.add(userInfoEntity);
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
                    UserInfoEntity userInfoEntity = new UserInfoEntity();
                    userInfoEntity.setUserId(id);
                    userInfoEntity.setUsername(queryDataArray[1]);
                    userInfoEntity.setSex(queryDataArray[2]);
                    userInfoEntity.setBirthday(queryDataArray[3]);
                    userInfoEntity.setRegion(queryDataArray[4]);
                    userInfoEntity.setRegisterTime(queryDataArray[5]);
                    resultList.add(userInfoEntity);
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

    public List<ServerMessage> editUserInfo(UserInfoEntity userInfoEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int userId = userInfoEntity.getUserId();
        String username = userInfoEntity.getUsername();
        String sex = userInfoEntity.getSex();
        String birthday = userInfoEntity.getBirthday();
        String region = userInfoEntity.getRegion();

        if (!iServiceDao.checkUserIdExist(userId)) {
            logger.warn("用户不存在" + userId);
            serverMessageList.add(ErrorPackUtil.getCustomError("用户不存在" + userId,0));
            return serverMessageList;
        }

        iServiceDao.updateUserInfo(userId, username, sex, birthday, region);
        UserInfoEntity newUserInfo = getUserInfoById(userId);
        serverMessageList.add(new ServerMessage(0, "NewUserInfo", JSON.toJSONString(newUserInfo)));

        //向所有在线好友发送消息


        return serverMessageList;
    }

    public List<ServerMessage> deleteFriend(PersonMessageClientEntity personMessageClientEntity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int userId1 = personMessageClientEntity.getUserId1();
        int userId2 = personMessageClientEntity.getUserId2();

        if (iServiceDao.checkFriendshipExist(userId1, userId2)) {
            logger.warn(String.format("%d和%d的好友关系不存在", userId1, userId2));
            serverMessageList.add(ErrorPackUtil.getCustomError(
                    String.format("%d和%d的好友关系不存在", userId1, userId2),0)
            );
            return serverMessageList;
        }

        iServiceDao.deleteFriendship(userId1, userId2);
        boolean user1OnlineStatus = iServiceDao.queryOnlineStatusById(userId1);
        boolean user2OnlineStatus = iServiceDao.queryOnlineStatusById(userId2);
        SingleIntEntity deleteUser = new SingleIntEntity();
        String sendMessageBody;
        if (user1OnlineStatus) {
            deleteUser.setUserId(userId2);
            sendMessageBody = JSON.toJSONString(deleteUser);
            serverMessageList.add(new ServerMessage(userId1, "DeleteFriend", sendMessageBody));
        }
        if (user2OnlineStatus) {
            deleteUser.setUserId(userId1);
            sendMessageBody = JSON.toJSONString(deleteUser);
            serverMessageList.add(new ServerMessage(userId2, "DeleteFriend", sendMessageBody));
        }

        return serverMessageList;
    }

    public List<ServerMessage> getUserInfoHandler(SingleIntEntity singleIntEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int id = singleIntEntity.getUserId();
        UserInfoEntity userInfoEntity = getUserInfoById(id);
        String sendMessageBody = JSON.toJSONString(userInfoEntity);
        serverMessageList.add(new ServerMessage(0, "UserInfo",sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> getSecureQuestion(SingleIntEntity singleIntEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int userId = singleIntEntity.getUserId();
        if (!iServiceDao.checkUserIdExist(userId)) {
            logger.warn("用户不存在" + userId);
            serverMessageList.add(ErrorPackUtil.getCustomError("用户不存在" + userId,0));
            return serverMessageList;
        }

        SecureQuestionEntity secureQuestionEntity = new SecureQuestionEntity();
        String queryString = iServiceDao.querySecureQuestion(userId);
        String[] splitArray = queryString.split("#");
        if (splitArray.length == 2) {
            secureQuestionEntity.setQuestion(splitArray[0]);
            secureQuestionEntity.setAnswer(splitArray[1]);
        }
        else {
            logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为2",
                    queryString,splitArray.length));
        }
        String sendMessageBody = JSON.toJSONString(secureQuestionEntity);
        serverMessageList.add(new ServerMessage(0, "SecureQuestion", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> setNewSecureInfo(NewSecureInfoEntity newSecureInfoEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int userId = newSecureInfoEntity.getUserId();
        String password = newSecureInfoEntity.getNewPassword();
        String question = newSecureInfoEntity.getNewSecureQuestion();
        String answer = newSecureInfoEntity.getNewAnswer();

        if (!iServiceDao.checkUserIdExist(userId)) {
            logger.warn("用户不存在" + userId);
            serverMessageList.add(ErrorPackUtil.getCustomError("用户不存在" + userId,0));
            return serverMessageList;
        }
        iServiceDao.updateUserSecureInfo(userId, password, question, answer);
        LoginServerEntity loginServerEntity = new LoginServerEntity();
        loginServerEntity.setLoginResult(true);

        String sendMessageBody  = JSON.toJSONString(loginServerEntity);
        serverMessageList.add(new ServerMessage(0, "SecureChangeResult", sendMessageBody));

        return serverMessageList;
    }

    private UserInfoEntity getUserInfoById(int id){
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        String queryLine = iServiceDao.queryUserInfoById(id);
        logger.info("queryUserInfoById id:" + id);
        logger.info("queryUserInfoById result:" + queryLine);
        if (!queryLine.isEmpty()){
            String[] queryDataArray = queryLine.split("#",-1);
            if (queryDataArray.length == 6){
                userInfoEntity.setUserId(id);
                userInfoEntity.setUsername(queryDataArray[1]);
                userInfoEntity.setSex(queryDataArray[2]);
                userInfoEntity.setBirthday(queryDataArray[3]);
                userInfoEntity.setRegion(queryDataArray[4]);
                userInfoEntity.setRegisterTime(queryDataArray[5]);
                userInfoEntity.setAvatarUrl("");
            }
            else {
                logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为6",
                        queryLine,queryDataArray.length));
            }
        }
        return userInfoEntity;
    }

}
