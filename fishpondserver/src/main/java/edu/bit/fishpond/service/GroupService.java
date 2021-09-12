package edu.bit.fishpond.service;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpond.service.entity.*;
import edu.bit.fishpond.utils.DAOException;
import edu.bit.fishpond.utils.ErrorPackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service("GroupService")
public class GroupService {

    @Autowired
    public GroupService(IServiceDao iServiceDao){
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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = currentTime.format(dateTimeFormatter);

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
                newGroupInfo.setCreatorId(creatorId);
                newGroupInfo.setCreateTime(currentTimeString);
                newGroupInfo.setGroupName(groupName);
                newGroupInfo.setManagerId(creatorId);


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

        int userId = entity.getUserId();
        if (!serviceDao.checkUserIdExist(userId)){
            logger.warn("用户不存在" + userId);
            serverMessageList.add(ErrorPackUtil.getCustomError("用户不存在" + userId,0));
            return serverMessageList;
        }
        List<Integer> groupIdList = serviceDao.queryGroupList(userId);

        for (int groupId : groupIdList) {
            groupInfoList.add(getGroupInfoById(groupId));
        }

        String sendMessageBody = JSON.toJSONString(groupInfoList);
        serverMessageList.add(new ServerMessage(0, "GroupList", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> getGroupMemberList(SingleIntEntity singleIntEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();
        List<UserInfoEntity> memberInfoList = new ArrayList<>();

        int groupId = singleIntEntity.getUserId();
        List<Integer> memberIdList = serviceDao.queryGroupMemberList(groupId);
        for (int memberId : memberIdList){
            UserInfoEntity memberInfo = getUserInfoById(memberId);
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
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = currentTime.format(dateTimeFormatter);

        int messageId = serviceDao.recordNewGroupMessage(senderId, groupId, messageType, currentTimeString, messageContent);
        messageEntity.setMessageId(messageId);
        messageEntity.setSendTime(currentTimeString);
        String sendMessageBody = JSON.toJSONString(messageEntity);

        boolean senderOnlineStatus = serviceDao.queryOnlineStatusById(senderId);

        List<Integer> groupMemberList = serviceDao.queryGroupMemberList(groupId);
        for (int memberId : groupMemberList) {
            boolean memberOnlineStatus = serviceDao.queryOnlineStatusById(memberId);
            if (memberOnlineStatus) {
                if (memberId != senderId){
                    serverMessageList.add(
                            new ServerMessage(memberId, "NewGroupMessage", sendMessageBody));
                }
                else {
                    if (senderOnlineStatus) {
                        serverMessageList.add(
                                new ServerMessage(0, "NewGroupMessage", sendMessageBody)
                        );
                    }

                }
            }
        }

        return serverMessageList;
    }

    public List<ServerMessage> getUnreadGroupMessageHandler(SingleIntEntity singleIntEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int userId = singleIntEntity.getUserId();
        if (!serviceDao.checkUserIdExist(userId)) {
            logger.warn("用户不存在:" + userId);
            serverMessageList.add(ErrorPackUtil.getCustomError("用户不存在:" + userId,0));
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
            logger.warn("用户不存在:" + userId);
            serverMessageList.add(ErrorPackUtil.getCustomError("用户不存在:" + userId,0));
            return serverMessageList;
        }

        List<Integer> messageIdList = serviceDao.queryLatestGroupMessageList(userId);
        logger.info(String.valueOf(messageIdList.size()));
        List<MessageEntity> latestGroupMessageList = new ArrayList<>();

        for (int messageId : messageIdList) {
            logger.info("messageId" + messageId);
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

    public List<ServerMessage> deleteGroupMessage(SingleIntEntity messageIdEntity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int messageId = messageIdEntity.getUserId();

        if (!serviceDao.checkGroupMessageExist(messageId)) {
            logger.warn("群消息不存在:" + messageId);
            serverMessageList.add(ErrorPackUtil.getCustomError("群消息不存在" + messageId,0));
            return serverMessageList;
        }

        MessageEntity groupMessageEntity = getGroupMessageInfoById(messageId);
        int groupId = groupMessageEntity.getRecipientId();
        serviceDao.deleteGroupMessage(messageId);
        List<Integer> messageIdList = serviceDao.queryGroupMemberList(groupId);
        for (int memberId : messageIdList) {
            boolean onlineStatus = serviceDao.queryOnlineStatusById(memberId);
            if (onlineStatus) {
                String sendMessageBody = JSON.toJSONString(groupMessageEntity);
                serverMessageList.add(new ServerMessage(memberId, "DeleteGroupMessage", sendMessageBody));
            }
        }


        return serverMessageList;
    }

    public List<ServerMessage> newGroupMember(NewGroupMemberClientEntity newGroupMemberClientEntity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int newMemberId = newGroupMemberClientEntity.getNewMemberId();
        int invitorId = newGroupMemberClientEntity.getInvitorId();
        int groupId = newGroupMemberClientEntity.getGroupId();
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = currentTime.format(dateTimeFormatter);

        if (serviceDao.checkGroupMemberExist(newMemberId, groupId)) {
            logger.warn("该成员已在群中:" + invitorId);
            serverMessageList.add(ErrorPackUtil.getCustomError("该成员已在群中" + invitorId,0));
            return serverMessageList;
        }

        serviceDao.recordNewMember(groupId, newMemberId, invitorId, currentTimeString);

        UserInfoEntity newMemberInfo = getUserInfoById(newMemberId);
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity();
        groupMemberEntity.setGroupId(groupId);
        groupMemberEntity.setUserId(newMemberInfo.getUserId());
        groupMemberEntity.setUsername(newMemberInfo.getUsername());
        groupMemberEntity.setSex(newMemberInfo.getSex());
        groupMemberEntity.setBirthday(newMemberInfo.getBirthday());
        groupMemberEntity.setRegion(newMemberInfo.getRegion());
        groupMemberEntity.setRegisterTime(newMemberInfo.getRegisterTime());
        String sendMessageBody = JSON.toJSONString(groupMemberEntity);

        List<Integer> groupMemberList = serviceDao.queryGroupMemberList(groupId);
        boolean newMemberOnlineStatus = serviceDao.queryOnlineStatusById(newMemberId);
        for (int memberId : groupMemberList) {
            boolean memberOnlineStatus = serviceDao.queryOnlineStatusById(memberId);
            if (memberOnlineStatus) {
                if (memberId != newMemberId){
                    serverMessageList.add(
                            new ServerMessage(memberId, "NewGroupMember", sendMessageBody));
                }
                else {
                    if (newMemberOnlineStatus) {
                        serverMessageList.add(
                                new ServerMessage(0, "NewGroupMember", sendMessageBody)
                        );
                    }

                }
            }
        }


        return serverMessageList;
    }

    public List<ServerMessage> exitGroupHandler(PersonMessageClientEntity personMessageClientEntity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int exitMemberId = personMessageClientEntity.getUserId1();
        int groupId = personMessageClientEntity.getUserId2();

        if (!serviceDao.checkGroupMemberExist(exitMemberId, groupId)) {
            logger.warn("该成员不在群中:" + exitMemberId);
            serverMessageList.add(ErrorPackUtil.getCustomError("该成员不在群中" + exitMemberId,0));
            return serverMessageList;
        }

        serviceDao.deleteGroupMember(exitMemberId, groupId);

        UserInfoEntity exitMemberInfo = getUserInfoById(exitMemberId);
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity();
        groupMemberEntity.setGroupId(groupId);
        groupMemberEntity.setUserId(exitMemberInfo.getUserId());
        groupMemberEntity.setUsername(exitMemberInfo.getUsername());
        groupMemberEntity.setSex(exitMemberInfo.getSex());
        groupMemberEntity.setBirthday(exitMemberInfo.getBirthday());
        groupMemberEntity.setRegion(exitMemberInfo.getRegion());
        groupMemberEntity.setRegisterTime(exitMemberInfo.getRegisterTime());
        String sendMessageBody = JSON.toJSONString(groupMemberEntity);

        List<Integer> groupMemberList = serviceDao.queryGroupMemberList(groupId);
        boolean newMemberOnlineStatus = serviceDao.queryOnlineStatusById(exitMemberId);
        for (int aMemberId : groupMemberList) {
            boolean memberOnlineStatus = serviceDao.queryOnlineStatusById(aMemberId);
            if (memberOnlineStatus) {
                if (aMemberId != exitMemberId){
                    serverMessageList.add(
                            new ServerMessage(aMemberId, "GroupMemberExit", sendMessageBody)
                    );
                }
                else {
                    if (newMemberOnlineStatus) {
                        serverMessageList.add(
                                new ServerMessage(0, "GroupMemberExit", sendMessageBody)
                        );
                    }

                }
            }
        }

        return serverMessageList;
    }

    private MessageEntity getGroupMessageInfoById(int messageId) {
        MessageEntity messageEntity = new MessageEntity();
        String messageInfoString = serviceDao.queryGroupMessageInfoById(messageId);
        logger.info(messageInfoString);
        String[] queryDataArray = messageInfoString.split("#",-1);
        if (queryDataArray.length == 5) {
            messageEntity.setMessageId(messageId);

            //得到senderId
            int senderId = Integer.parseInt(queryDataArray[0]);
            messageEntity.setSenderId(senderId);

            //根据senderId得到senderName
            UserInfoEntity userInfo = getUserInfoById(senderId);
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
        else {
            logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为5",
                    messageInfoString,queryDataArray.length));
        }

        return messageEntity;
    }

    private UserInfoEntity getUserInfoById(int id){
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        String queryLine = serviceDao.queryUserInfoById(id);
//        logger.info("queryUserInfoById id:" + id);
//        logger.info("queryUserInfoById result:" + queryLine);
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
