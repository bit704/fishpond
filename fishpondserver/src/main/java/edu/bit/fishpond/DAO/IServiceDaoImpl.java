package edu.bit.fishpond.DAO;

import edu.bit.fishpond.DAO.DO.*;
import edu.bit.fishpond.DAO.mapper.*;
import edu.bit.fishpond.service.IServiceDao;
import edu.bit.fishpond.utils.DAOException;
import edu.bit.fishpond.utils.secureplus.SecureForServerp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

@Component
public class IServiceDaoImpl implements IServiceDao {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    FriendRequestMapper friendRequestMapper;

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    FriendshipMapper friendshipMapper;

    @Autowired
    SysMessageMapper sysMessageMapper;

    @Autowired
    GroupInfoMapper groupInfoMapper;

    @Autowired
    GroupMemberMapper groupMemberMapper;

    @Autowired
    GroupMessageMapper groupMessageMapper;


    @Override
    public void clearDAO() {
        userMapper.deleteAll();
        userInfoMapper.deleteAll();
        groupInfoMapper.deleteAll();
        groupMemberMapper.deleteAll();
        groupMessageMapper.deleteAll();
        messageMapper.deleteAll();
        sysMessageMapper.deleteAll();
        friendRequestMapper.deleteAll();
        friendshipMapper.deleteAll();
        return;
    }

    /**
     * 判断是否有此用户
     *
     * @param userId
     * @return 是否有此用户
     */
    private boolean hadUser(int userId) {
        UserDO userDO = userMapper.selectOneById(userId);
        if (userDO == null) {
            return false;
        }
        return true;
    }

    /**
     * 给字符串添加单引号包围
     *
     * @param string
     * @return
     */
    private String addQuotes(String string) {
        return "'" + string + "'";
    }

    /**
     * 将消息类转化为指定的消息String
     *
     * @param messageDOList
     * @return
     */
    private List<String> messages2Strings(List<MessageDO> messageDOList) {
        List<String> result = new LinkedList<>();
        for (MessageDO messageDO : messageDOList) {
            result.add(message2String(messageDO));
        }
        return result;
    }

    private String message2String(MessageDO messageDO) {
        if (messageDO == null) return "";
        StringJoiner message = new StringJoiner("#");
        message.add(String.valueOf(messageDO.getSender()));
        message.add(String.valueOf(messageDO.getReceiver()));
        message.add(messageDO.getMtype());
        message.add(messageDO.getSend_time());
        message.add(messageDO.getContent());
        return message.toString();
    }

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.SERIALIZABLE)
    @Override
    public int recordNewUser(String userName, String password, String securityQuestion, String answer) throws DAOException {
        password = SecureForServerp.encryptPBKDF2(password);
        int insertNum1 = userMapper.insertOne(password, securityQuestion, answer);
        if (insertNum1 != 1) {
            throw new DAOException("注册新用户失败");
        }
        int newid = userMapper.getLastSqValue(); //插入用户的id
        LocalDateTime now = LocalDateTime.now(); //当前日期
        int insertNum2 = userInfoMapper.insertOne(
                newid,
                userName,
                null,
                null,
                null,
                false,
                now.toString(),
                "2000-01-01 00:00:00",

                true);
        if (insertNum2 != 1) {
            throw new DAOException("注册新用户失败");
        }
        return newid;
    }

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.SERIALIZABLE)
    @Override
    public int recordNewGroup(String groupName, int creatorId, String createTime) throws DAOException {
        int insertNum = groupInfoMapper.insertOne(groupName,creatorId,createTime,creatorId,true);
        if(insertNum != 1) throw new DAOException("插入群失败");
        return groupInfoMapper.getLastSqValue();
    }

    @Override
    public boolean queryOnlineStatusById(int userId) throws DAOException {
        if (!hadUser(userId)) throw new DAOException("没有此用户");
        return userInfoMapper.selectState(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ)
    @Override
    public void updateOnlineStatus(int userId) throws DAOException {
        if (!hadUser(userId)) throw new DAOException("没有此用户");
        boolean state = userInfoMapper.selectState(userId);
        if (state == true) {
            LocalDateTime now = LocalDateTime.now();
            userInfoMapper.updateOne("last_offline", addQuotes(now.toString()), userId);
            //标记现在的时间为用户的上次离线时间
        }
        state = !state;
        int updateNum = userInfoMapper.updateOne("state", String.valueOf(state), userId);
        if (updateNum != 1) throw new DAOException("更新用户状态失败");
        return;
    }

    @Override
    public void recordNewFriendRequest(int applierId, int recipientId, String explain, String sendTime) throws DAOException {
        int insertNum = friendRequestMapper.insertOne(applierId, recipientId, sendTime, explain);
        if (insertNum != 1) throw new DAOException("好友申请失败");
    }

    @Override
    public boolean checkPassword(int userId, String password) {
        UserDO userDO = userMapper.selectOneById(userId);
        String passwordHash = userDO.getPassword();
        return SecureForServerp.verifyPBKDF2(password, passwordHash);
    }


    @Override
    public List<Integer> queryAllMessageBetween(int userId1, int userId2) {
        return messageMapper.selectMidByPartner(userId1, userId2);
    }


    @Override
    public List<String> queryFriendRequestList(int recipientId) {
        List<FriendRequestDO> friendRequestDOList = friendRequestMapper.selectByReceiver(recipientId);
        List<String> result = new LinkedList<>();
        for (FriendRequestDO friendRequestDO : friendRequestDOList) {
            StringJoiner stringJoiner = new StringJoiner("#");
            stringJoiner.add(String.valueOf(friendRequestDO.getRequester()));
            stringJoiner.add(friendRequestDO.getRequest_time());
            stringJoiner.add(friendRequestDO.getExplanation());
            result.add(stringJoiner.toString());
        }
        return result;
    }

    @Override
    public void recordNewFriendship(int userId1, int userId2, String beFriendTime) throws DAOException {
        int insertNum = friendshipMapper.insertOne(userId1,userId2,beFriendTime);
        if(insertNum != 1) throw new DAOException("添加好友关系失败");
        return;
    }

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.SERIALIZABLE)
    @Override
    public int recordSystemMessage(int userId, String sendTime, String messageType, String content) throws DAOException {
        int insertNum = sysMessageMapper.insertOne(userId, sendTime, messageType, content);
        if (insertNum != 1) throw new DAOException("添加系统消息失败");
        return sysMessageMapper.getLastSqValue();
    }

    @Transactional(propagation = Propagation.REQUIRED,isolation  = Isolation.READ_COMMITTED)
    @Override
    public void deleteFriendRequest(int applierId, int recipientId) throws DAOException {
        int deleteNum1 = friendRequestMapper.deleteByPK(applierId, recipientId);
        int deleteNum2 = friendRequestMapper.deleteByPK(recipientId, applierId);
        //if(deleteNum != 1) throw new DAOException("删除好友申请失败");
        return;
    }

    @Override
    public List<Integer> queryFriendList(int senderId) {
        //查询好友列表
        List<FriendshipDO> friendshipDOList = friendshipMapper.selectById(senderId);
        List<Integer> friendList = new LinkedList<>();
        for(FriendshipDO friendshipDO : friendshipDOList) {
            if(friendshipDO.getUid1() == senderId) {
                friendList.add(friendshipDO.getUid2());
            }
            else {
                friendList.add(friendshipDO.getUid1());
            }
        }
        return friendList;
    }

    @Override
    public String queryUserInfoById(int id) {
        if(!hadUser(id)) return  "";
        UserInfoDO userInfoDO = userInfoMapper.selectOneById(id);
        StringJoiner stringJoiner = new StringJoiner("#");
        stringJoiner.add(String.valueOf(id));
        stringJoiner.add(userInfoDO.getName());
        stringJoiner.add(userInfoDO.getSex());
        stringJoiner.add(userInfoDO.getBirthday());
        stringJoiner.add(userInfoDO.getRegion());
        stringJoiner.add(userInfoDO.getCreate_time());
        return stringJoiner.toString();
    }

    @Override
    public List<String> queryUserInfoByName(String nameSubString) {
        List<String> result = new LinkedList<>();
        List<UserInfoDO> userInfoDOList = userInfoMapper.selectBySubName(nameSubString);
        for(UserInfoDO userInfoDO: userInfoDOList) {
            StringJoiner stringJoiner = new StringJoiner("#");
            stringJoiner.add(String.valueOf(userInfoDO.getUid()));
            stringJoiner.add(userInfoDO.getName());
            stringJoiner.add(userInfoDO.getSex());
            stringJoiner.add(userInfoDO.getBirthday());
            stringJoiner.add(userInfoDO.getRegion());
            stringJoiner.add(userInfoDO.getCreate_time());
            result.add(stringJoiner.toString());
        }
        return result;
    }


    @Override
    public List<Integer> queryLatestMessageList(int userId) {
        List<FriendshipDO> friendshipDOList = friendshipMapper.selectById(userId);
        List<Integer> friendList = new LinkedList<>();
        for (FriendshipDO friendshipDO : friendshipDOList) {
            if (friendshipDO.getUid1() == userId) {
                friendList.add(friendshipDO.getUid2());
            } else {
                friendList.add(friendshipDO.getUid1());
            }
        }
        List<Integer> result = new ArrayList<>();
        for(Integer fid : friendList) {
            result.add(messageMapper.selectMidByPartnerLatest(fid, userId).get(0));
        }
        return result;

    }

    @Override
    public void recordNewMember(int groupId, int userId, int invitorId, String inTime) throws DAOException {
        int insertNum = groupMemberMapper.insertOne(groupId,userId,invitorId,inTime);
        if(insertNum != 1) throw new DAOException("添加群成员失败");
        return;
    }

    @Override
    public List<Integer> queryGroupList(int userId) {
        List<Integer> result = groupMemberMapper.selectGroupByUser(userId);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public void deleteUser(int userId) {
        userInfoMapper.deleteOne(userId);
        userMapper.deleteOne(userId);
        return;
    }

    @Override
    public boolean checkFriendRequestExist(int applierId, int recipientId) {
        int selectNum = friendRequestMapper.selectByPK(applierId,recipientId);
        if(selectNum == 1)
            return true;
        else
            return false;
    }

    @Override
    public List<Integer> queryGroupMemberList(int groupId) {
        return groupMemberMapper.selectGroupMemberListById(groupId);
    }

    @Override
    public void deleteMessage(int messageId) {
        messageMapper.deleteByMid(messageId);
    }

    @Override
    public boolean checkUserIdExist(int userId) {
        UserDO userDO = userMapper.selectOneById(userId);
        if (userDO == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkGroupIdExist(int groupId) {
        String groupName = groupInfoMapper.selectNameById(groupId);
        if(groupName == null) {
            return false;
        }
        else return true;
    }

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.SERIALIZABLE)
    @Override
    public int recordNewMessage(int senderId, int recipientId, String messageType, String sendTime, String messageContent) {
        messageMapper.insertOne(senderId, recipientId, sendTime, messageType, messageContent);
        return messageMapper.getLastSqValue();
    }

    @Override
    public List<Integer> queryUnreadMessageList(int recipientId) {
        //查询接收者的上次登录时间
        String last_offline = userInfoMapper.selectLast_offlineByUid(recipientId);
        return messageMapper.selectByReceiverBeforeTime(recipientId, last_offline);
    }

    @Override
    public String queryMessageInfoById(int messageId) {
        return message2String(messageMapper.selectByMid(messageId));
    }

    @Override
    public boolean checkMessageExist(int checkId) {
        MessageDO messageDO = messageMapper.selectByMid(checkId);
        return messageDO != null;
    }

    @Override
    public String queryGroupInfoById(int groupId) {
        GroupInfoDO groupInfoDO = groupInfoMapper.selectById(groupId);
        StringJoiner stringJoiner = new StringJoiner("#");
        stringJoiner.add(groupInfoDO.getName());
        stringJoiner.add(String.valueOf(groupInfoDO.getCreator()));
        stringJoiner.add(String.valueOf(groupInfoDO.getManager()));
        stringJoiner.add(String.valueOf(groupInfoDO.getCreate_time()));
        return stringJoiner.toString();
    }

    @Override
    public void deleteGroup(int groupId) {
        groupInfoMapper.deleteByGid(groupId);
    }

    @Override
    public String querySystemMessageInfoById(int systemMessageId) {
        SysMessageDO sysMessageDO = sysMessageMapper.selectBySmid(systemMessageId);
        StringJoiner stringJoiner = new StringJoiner("#");
        stringJoiner.add(String.valueOf(sysMessageDO.getUser()));
        stringJoiner.add(sysMessageDO.getSend_time());
        stringJoiner.add(sysMessageDO.getMtype());
        stringJoiner.add(sysMessageDO.getContent());
        return stringJoiner.toString();
    }

    @Override
    public List<Integer> queryUnreadSystemMessageList(int userId) {
        String last_offline = userInfoMapper.selectLast_offlineByUid(userId);
        return sysMessageMapper.selectBeforeTime(userId,last_offline);
    }

    @Override
    public List<Integer> queryUnreadGroupMessageList(int userId) {
        String last_offline = userInfoMapper.selectLast_offlineByUid(userId);
        return groupMessageMapper.selectBeforeTime(userId,last_offline);
    }

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.SERIALIZABLE)
    @Override
    public int recordNewGroupMessage(int senderId, int groupId, String messageType, String sendTime, String messageContent) {
        groupMessageMapper.insertOne(senderId,groupId,sendTime,messageType,messageContent);
        return groupMessageMapper.getLastSqValue();
    }

    @Override
    public String queryGroupMessageInfoById(int messageId) {
        GroupMessageDO groupMessageDO = groupMessageMapper.selectByGmid(messageId);
        StringJoiner stringJoiner = new StringJoiner("#");
        stringJoiner.add(String.valueOf(groupMessageDO.getSender()));
        stringJoiner.add(String.valueOf(groupMessageDO.getReceiver()));
        stringJoiner.add(groupMessageDO.getMtype());
        stringJoiner.add(groupMessageDO.getSend_time());
        stringJoiner.add(groupMessageDO.getContent());
        return stringJoiner.toString();
    }

    @Override
    public List<Integer> queryLatestGroupMessageList(int userId) {
        List<Integer> groupList = groupMemberMapper.selectGroupMemberListById(userId);
        List<Integer> result = new ArrayList<>();
        for (Integer gid : groupList)
        {
            result.add(groupMessageMapper.selectGmidByPartnerLatest(userId,gid).get(0));
        }
        return result;
    }

    @Override
    public void deleteGroupMessage(int groupMessageId) {
        groupMessageMapper.deleteByGmid(groupMessageId);
    }

    @Override
    public boolean checkGroupMessageExist(int checkId) {
        GroupMessageDO groupMessageDO = groupMessageMapper.selectByGmid(checkId);
        return groupMessageDO != null;
    }

    @Override
    public List<Integer> queryAllMessageIn(int groupId) {
        return groupMessageMapper.selectAllGmid(groupId);
    }

    @Override
    public boolean checkFriendshipExist(int userId1, int userId2) {
        int num = friendshipMapper.selectByFriendship(userId1,userId2);
        return num != 0;
    }

    @Override
    public boolean checkGroupMemberExist(int userId, int groupId) {
        List<Integer> groupList = groupMemberMapper.selectGroupByUser(userId);
        return groupList.contains(groupId);
    }

    @Override
    public void updateUserInfo(int userId, String username, String sex, String birthday, String region) {
        int updateNum = userInfoMapper.updateInfo(userId,username,sex,birthday,region);
    }

    @Override
    public void deleteFriendship(int userId1, int userId2) {
        friendshipMapper.deleteByFriendship(userId1,userId2);
    }

    @Override
    public void deleteGroupMember(int memberId, int groupId) {
        groupMemberMapper.deleteGroupMember(memberId,groupId);
    }

    @Override
    public String queryEncryptedQuestion(int userId) {
        UserDO userDO = userMapper.selectOneById(userId);
        return userDO.getQuestion();
    }

    @Override
    public boolean checkEncryptedAnswer(int userId, String answer) {
        UserDO userDO = userMapper.selectOneById(userId);
        return userDO.getAnswer().equals(answer);
    }

    @Override
    public void updateUserSecureInfo(int userId, String password, String question, String answer) {
        userMapper.updateSecureInfo(userId,password,question,answer);
    }
}
