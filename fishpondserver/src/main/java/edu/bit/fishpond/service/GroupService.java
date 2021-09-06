package edu.bit.fishpond.service;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpond.service.entity.*;
import edu.bit.fishpond.utils.DAOException;
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

    public List<ServerMessage> sendMessageToGroup(MessageEntity messageEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int senderId = messageEntity.getSenderId();
        int recipientGroupId = messageEntity.getRecipientId();
        String messageType = messageEntity.getMessageType();
        String messageContent = messageEntity.getMessageContent();
        String senderTime = messageEntity.getSendTime();

        

        return serverMessageList;
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
