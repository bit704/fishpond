package edu.bit.fishpond.service;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpond.service.entity.*;
import edu.bit.fishpond.utils.DAOException;
import edu.bit.fishpond.utils.ErrorPackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("GroupService")
public class GroupService {

    @Autowired
    public GroupService(@Qualifier("IServiceDaoImpl") IServiceDao iServiceDao){
        this.serviceDao = iServiceDao;
    }

    private final IServiceDao serviceDao;
    private final Logger logger = LoggerFactory.getLogger(GroupService.class);

    public List<ServerMessage> groupCreateHandler(GroupCreateClientEntity entity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int creatorId = entity.getCreatorId();
        String groupName = entity.getGroupName();
        List<Integer> initialMembers = entity.getInitialMembers();

        //错误处理
        if (!initialMembers.contains(creatorId)){
            initialMembers.add(creatorId);
        }

        LocalDateTime currentTime = LocalDateTime.now();
        String currentTimeString = currentTime.toString();

        //获取新建的群聊的ID
        int groupId = serviceDao.recordNewGroup(groupName, creatorId, currentTimeString);
        for (int memberId: initialMembers) {
            // 将初始的每个群成员记录
            serviceDao.recordNewMember(groupId, memberId, creatorId, currentTimeString);
            // 查询每个初始成员的在线状态
            boolean onlineStatus = serviceDao.queryOnlineStatusById(memberId);
            //如果该成员在线，向该成员发送信息
            if (onlineStatus){
                GroupInfoServerEntity newGroupInfo = new GroupInfoServerEntity();
                newGroupInfo.setGroupId(groupId);
                newGroupInfo.setGroupName(groupName);


                String sendMessageBody = JSON.toJSONString(newGroupInfo);
                if (memberId == creatorId) {
                    serverMessageList.add(new ServerMessage(0, "NewGroup", sendMessageBody));
                }
                else {
                    serverMessageList.add(new ServerMessage(memberId, "NewGroup", sendMessageBody));
                }
            }
        }

        return serverMessageList;
    }

    public List<ServerMessage> getGroupListHandler(SingleIntEntity entity){
        List<ServerMessage> serverMessageList = new ArrayList<>();

        List<GroupInfoServerEntity> groupInfoList = new ArrayList<>();

        int id = entity.getUserId();
        if (!serviceDao.checkGroupIdExist(id)){
            return serverMessageList;
        }
        List<Integer> groupIdList = serviceDao.queryGroupList(id);

        for (int groupId : groupIdList) {
            groupInfoList.add(getGroupInfoById(groupId));
        }

        String sendMessageBody = JSON.toJSONString(groupInfoList);
        serverMessageList.add(new ServerMessage(0, "GroupList", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> getGroupMember(SingleIntEntity singleIntEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();
        List<UserInfoServerEntity> memberInfoList = new ArrayList<>();

        int groupId = singleIntEntity.getUserId();
        List<Integer> memberIdList = serviceDao.queryGroupMemberList(groupId);
        for (int memberId : memberIdList){
            UserInfoServerEntity memberInfo = getUserInfoById(memberId);
            memberInfoList.add(memberInfo);
        }
        String sendMessageBody = JSON.toJSONString(memberInfoList);
        serverMessageList.add(new ServerMessage(0, "GroupMembers", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> sendMessageToGroup(MessageEntity messageEntity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int senderId = messageEntity.getSenderId();
        int groupId = messageEntity.getRecipientId();
        String messageType = messageEntity.getMessageType();
        String messageContent = messageEntity.getMessageContent();
        String senderTime = messageEntity.getSendTime();

        int messageId = serviceDao.recordNewGroupMessage(senderId, groupId, messageType, senderTime, messageContent);
        messageEntity.setMessageId(messageId);
        String sendMessageBody = JSON.toJSONString(messageEntity);

        boolean senderOnlineStatus = serviceDao.queryOnlineStatusById(senderId);
        // 如果发送者在线
        if (senderOnlineStatus) {
            serverMessageList.add(new ServerMessage(0, "MessageId", sendMessageBody));
        }

        List<Integer> groupMemberList = serviceDao.queryGroupMemberList(groupId);
        for (int memberId : groupMemberList) {
            boolean memberOnlineStatus = serviceDao.queryOnlineStatusById(memberId);
            if (memberOnlineStatus) {
                serverMessageList.add(new ServerMessage(memberId, "NewGroupMessage", sendMessageBody));
            }
        }

        return serverMessageList;
    }

    public List<ServerMessage> getUnreadGroupMessageHandler(SingleIntEntity singleIntEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int userId = singleIntEntity.getUserId();
        if (!serviceDao.checkUserIdExist(userId)) {
            logger.warn("用户不存在" + userId);
            serverMessageList.add(ErrorPackUtil.getCustomError("用户不存在" + userId,0));
            return serverMessageList;
        }

        List<Integer> messageIdList = serviceDao.queryUnreadGroupMessageList(userId);
        List<MessageEntity> unreadGroupMessageList = new ArrayList<>();

        for (int messageId : messageIdList) {
            unreadGroupMessageList.add(getGroupMessageInfoById(messageId));
        }

        String sendMessageBody = JSON.toJSONString(unreadGroupMessageList);
        serverMessageList.add(new ServerMessage(0, "UnreadGroupMessage", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> getLatestGroupMessage(SingleIntEntity singleIntEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int userId = singleIntEntity.getUserId();
        if (!serviceDao.checkUserIdExist(userId)) {
            logger.warn("用户不存在" + userId);
            serverMessageList.add(ErrorPackUtil.getCustomError("用户不存在" + userId,0));
            return serverMessageList;
        }

        List<Integer> messageIdList = serviceDao.queryLatestGroupMessageList(userId);
        List<MessageEntity> latestGroupMessageList = new ArrayList<>();

        for (int messageId : messageIdList) {
            latestGroupMessageList.add(getGroupMessageInfoById(messageId));
        }

        String sendMessageBody = JSON.toJSONString(latestGroupMessageList);
        serverMessageList.add(new ServerMessage(0, "LatestGroupMessage", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> getGroupMessageIn(SingleIntEntity singleIntEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();
        int groupId = singleIntEntity.getUserId();

        if (!serviceDao.checkGroupIdExist(groupId)) {
            logger.warn("群聊不存在:" + groupId);
            serverMessageList.add(ErrorPackUtil.getCustomError("群聊不存在" + groupId,0));
            return serverMessageList;
        }

        List<Integer> messageIdList = serviceDao.queryAllMessageIn(groupId);
        List<MessageEntity> messageList = new ArrayList<>();

        for (int messageId : messageIdList) {
            messageList.add(getGroupMessageInfoById(messageId));
        }

        String sendMessageBody = JSON.toJSONString(messageList);
        serverMessageList.add(new ServerMessage(0, "MessageInGroup", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> deleteGroupMessage() {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        return serverMessageList;
    }

    private MessageEntity getGroupMessageInfoById(int messageId) {
        MessageEntity messageEntity = new MessageEntity();
        String messageInfoString = serviceDao.queryMessageInfoById(messageId);

        String[] queryDataArray = messageInfoString.split("#",-1);
        if (queryDataArray.length == 5) {
            messageEntity.setMessageId(messageId);

            //得到senderId
            int senderId = Integer.parseInt(queryDataArray[0]);
            messageEntity.setSenderId(senderId);

            //根据senderId得到senderName
            UserInfoServerEntity userInfo = getUserInfoById(senderId);
            messageEntity.setSenderName(userInfo.getUsername());

            //得到groupId
            int groupId = Integer.parseInt(queryDataArray[1]);
            messageEntity.setRecipientId(groupId);
            //根据groupId得到groupName
            GroupInfoServerEntity groupInfo = getGroupInfoById(groupId);
            messageEntity.setRecipientName(groupInfo.getGroupName());

            messageEntity.setMessageType(queryDataArray[2]);
            messageEntity.setSendTime(queryDataArray[3]);
            messageEntity.setMessageContent(queryDataArray[4]);
        }

        return messageEntity;
    }

    private UserInfoServerEntity getUserInfoById(int id){
        UserInfoServerEntity userInfoServerEntity = new UserInfoServerEntity();
        String queryLine = serviceDao.queryUserInfoById(id);
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

    private GroupInfoServerEntity getGroupInfoById(int groupId) {
        GroupInfoServerEntity groupInfo = new GroupInfoServerEntity();
        String groupInfoString = serviceDao.queryGroupInfoById(groupId);

        String[] queryDataArray = groupInfoString.split("#",-1);
        if (queryDataArray.length == 4){
            groupInfo.setGroupId(groupId);
            groupInfo.setGroupName(queryDataArray[0]);
            groupInfo.setCreatorId( Integer.parseInt(queryDataArray[1]));
            groupInfo.setManagerId(Integer.parseInt(queryDataArray[2]));
            groupInfo.setCreateTime(queryDataArray[3]);
        }
        else {
            logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为4",
                    groupInfoString,queryDataArray.length));
        }

        return groupInfo;
    }

}
