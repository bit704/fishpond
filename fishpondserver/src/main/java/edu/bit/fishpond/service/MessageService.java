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

import java.util.ArrayList;
import java.util.List;

@Service("MessageService")
public class MessageService {

    private final IServiceDao iServiceDao;
    private final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    public MessageService(@Qualifier("IServiceDaoImpl") IServiceDao iServiceDao){
        this.iServiceDao = iServiceDao;
    }

    public List<ServerMessage> sendMessageHandler(MessageEntity messageEntity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int senderId = messageEntity.getSenderId();
        int recipientId = messageEntity.getRecipientId();
        String sendTime = messageEntity.getSendTime();
        String messageType = messageEntity.getMessageType();
        String messageContent = messageEntity.getMessageContent();

        //将消息写入
        int id = iServiceDao.recordNewMessage(senderId, recipientId, messageType, sendTime, messageContent);
        messageEntity.setMessageId(id);
        String sendMessageBody = JSON.toJSONString(messageEntity);

        boolean RecipientOnlineStatus = iServiceDao.queryOnlineStatusById(recipientId);
        boolean senderOnlineStatus = iServiceDao.queryOnlineStatusById(senderId);
        // 如果发送者在线
        if (senderOnlineStatus) {
            serverMessageList.add(new ServerMessage(0, "MessageId", sendMessageBody));
        }
        // 如果接收者在线
        if (RecipientOnlineStatus) {
            serverMessageList.add(new ServerMessage(recipientId, "NewMessage", sendMessageBody));
        }

        return serverMessageList;
    }

    public List<ServerMessage> getAllMessageBetweenHandler(PersonMessageClientEntity entity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int id1 = entity.getUserId1();
        int id2 = entity.getUserId2();

        List<MessageEntity> messageBetweenList = new ArrayList<>();
        List<Integer> messageIdList = iServiceDao.queryAllMessageBetween(id1, id2);
        for (int messageId : messageIdList) {
            messageBetweenList.add(getMessageInfoById(messageId));
        }
        String sendMessageBody = JSON.toJSONString(messageBetweenList);

        serverMessageList.add(new ServerMessage(0, "MessageBetween", sendMessageBody));

        return serverMessageList;
    }

    public List<ServerMessage> getUnreadMessage(SingleIntEntity singleIntEntity){
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int id = singleIntEntity.getUserId();
        if (!iServiceDao.checkUserIdExist(id)){
            logger.warn("用户不存在" + id);
            serverMessageList.add(ErrorPackUtil.getCustomError("用户不存在" + id,0));
            return serverMessageList;
        }
        List<Integer> messageIdList = iServiceDao.queryUnreadMessageList(id);
        List<MessageEntity> unreadMessageList = new ArrayList<>();
        for (int messageId : messageIdList) {
            unreadMessageList.add(getMessageInfoById(messageId));
        }

        String sendMessageBody = JSON.toJSONString(unreadMessageList);
        serverMessageList.add(new ServerMessage(0, "UnreadMessage", sendMessageBody));
        return serverMessageList;
    }

    public List<ServerMessage> getLatestMessageHandler(SingleIntEntity singleIntEntity) {
        List<ServerMessage> serverMessageList = new ArrayList<>();
        int id = singleIntEntity.getUserId();

        if (!iServiceDao.checkUserIdExist(id)){
            logger.warn("用户不存在" + id);
            serverMessageList.add(ErrorPackUtil.getCustomError("用户不存在" + id,0));
            return serverMessageList;
        }

        List<Integer> messageIdList = iServiceDao.queryLatestMessageList(id);
        List<MessageEntity> latestMessageList = new ArrayList<>();
        for (int messageId : messageIdList) {
            latestMessageList.add(getMessageInfoById(messageId));
        }

        String messageBody = JSON.toJSONString(latestMessageList);
        serverMessageList.add(new ServerMessage(0, "LatestMessage", messageBody));

        return serverMessageList;
    }

    public List<ServerMessage> deleteMessage(SingleIntEntity messageIdEntity) throws DAOException {
        List<ServerMessage> serverMessageList = new ArrayList<>();

        int messageId = messageIdEntity.getUserId();
        if (!iServiceDao.checkMessageExist(messageId)){
            logger.warn("消息不存在" + messageId);
            serverMessageList.add(ErrorPackUtil.getCustomError("消息不存在" + messageId,0));
            return serverMessageList;
        }
        iServiceDao.deleteMessage(messageId);
        int recipientId = getMessageInfoById(messageId).getRecipientId();
        if (iServiceDao.queryOnlineStatusById(recipientId)) {
            String sendMessageBody = JSON.toJSONString(messageIdEntity);
            serverMessageList.add(new ServerMessage(recipientId, "DeleteMessage", sendMessageBody));
        }

        return serverMessageList;
    }

    private MessageEntity getMessageInfoById(int messageId) {
        MessageEntity messageEntity = new MessageEntity();
        String messageInfoString = iServiceDao.queryMessageInfoById(messageId);

        String[] queryDataArray = messageInfoString.split("#",-1);
        if (queryDataArray.length == 5) {
            messageEntity.setMessageId(messageId);

            //得到senderId
            int senderId = Integer.parseInt(queryDataArray[0]);
            messageEntity.setSenderId(senderId);

            UserInfoServerEntity userInfo;
            //根据senderId得到senderName
            userInfo = getUserInfoById(senderId);
            messageEntity.setSenderName(userInfo.getUsername());

            //得到recipientId
            int recipientId = Integer.parseInt(queryDataArray[1]);
            messageEntity.setRecipientId(recipientId);
            //根据recipientId得到recipientName
            userInfo = getUserInfoById(recipientId);
            messageEntity.setRecipientName(userInfo.getUsername());

            messageEntity.setMessageType(queryDataArray[2]);
            messageEntity.setSendTime(queryDataArray[3]);
            messageEntity.setMessageContent(queryDataArray[4]);
        }

        return messageEntity;
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
