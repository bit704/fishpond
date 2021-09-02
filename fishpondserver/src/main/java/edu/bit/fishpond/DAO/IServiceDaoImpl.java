package edu.bit.fishpond.DAO;

import edu.bit.fishpond.DAO.DO.*;
import edu.bit.fishpond.DAO.mapper.*;
import edu.bit.fishpond.service.IServiceDao;
import edu.bit.fishpond.utils.DAOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private List<String> message2String(List<MessageDO> messageDOList) {
        List<String> result = new LinkedList<>();
        for (MessageDO messageDO : messageDOList) {
            StringJoiner message = new StringJoiner("#");
            message.add(String.valueOf(messageDO.getSender()));
            message.add(messageDO.getMtype());
            message.add(messageDO.getSend_time());
            message.add(messageDO.getContent());
            result.add(message.toString());
        }
        return result;
    }

    @Override
    public int recordNewUser(String userName, String password, String securityQuestion, String answer) throws DAOException {
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
                null,
                true);
        if (insertNum2 != 1) {
            throw new DAOException("注册新用户失败");
        }
        return newid;
    }

    @Override
    public boolean queryOnlineStatusById(int userId) throws DAOException {
        if (!hadUser(userId)) throw new DAOException("没有此用户");
        return userInfoMapper.selectState(userId);
    }

    @Override
    public void updateOnlineStatusById(int userId) throws DAOException {
        if (!hadUser(userId)) throw new DAOException("没有此用户");
        boolean state = userInfoMapper.selectState(userId);
        if (state == false) {
            LocalDateTime now = LocalDateTime.now();
            userInfoMapper.updateOne("last_offline", addQuotes(now.toString()), userId);
            //标记现在的时间为用户的离线时间
        }
        state = !state;
        int updateNum = userInfoMapper.updateOne("state", String.valueOf(state), userId);
        if (updateNum != 1) throw new DAOException("更新用户状态失败");
        return;
    }

    @Override
    public void recordFriendRequest(int applierId, int recipientId, String explain, String sendTime) throws DAOException {
        int insertNum = friendRequestMapper.insertOne(applierId, recipientId, explain, sendTime);
        if (insertNum != 1) throw new DAOException("好友申请失败");
        return;
    }

    @Override
    public boolean checkPassword(int userId, String passwordHash) {
        UserDO userDO = userMapper.selectOneById(userId);
        String password = userDO.getPassword();
        return password.equals(passwordHash);
    }

    @Override
    public List<String> getUnreadMessage(int recipientId) {
        UserInfoDO userInfoDO = userInfoMapper.selectOneById(recipientId);
        //获取用户上次离线时间
        LocalDateTime last_offline = LocalDateTime.parse(userInfoDO.getLast_offline());
        List<MessageDO> messageDOList = messageMapper.selectByReceiverBeforeTime(recipientId, last_offline.toString());
        return message2String(messageDOList);
    }

    @Override
    public List<String> queryAllMessage(int userId) {
        UserInfoDO userInfoDO = userInfoMapper.selectOneById(userId);
        List<MessageDO> messageDOList = new LinkedList<>();
        messageDOList.addAll(messageMapper.selectByReceiver(userId));
        messageDOList.addAll(messageMapper.selectBySender(userId));
        return message2String(messageDOList);
    }


    @Override
    public List<String> queryFriendRequest(int recipientId) {
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
    public void recordFriendship(int userId1, int userId2, String beFriendTime) throws DAOException {
        int insertNum = friendshipMapper.insertOne(userId1,userId2,beFriendTime);
        if(insertNum != 1) throw new DAOException("添加好友关系失败");
        return;
    }

    @Override
    public void recordSystemMessage(int userId, String sendTime, String messageType, String content) throws DAOException {
        int insertNum = sysMessageMapper.insertOne(userId,sendTime,messageType,content);
        if(insertNum != 1) throw new DAOException("添加系统消息失败");
        return;
    }

    @Override
    public void deleteFriendRequest(int applierId, int recipientId) throws DAOException {
        int deleteNum = friendRequestMapper.deleteByPK(applierId,recipientId);
        if(deleteNum != 1) throw new DAOException("删除好友申请失败");
        return;

    }

    @Override
    public List<String> queryFriendshipById(int senderId) {
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
        List<String> result = new LinkedList<>();
        //查询用户名
        for(Integer friendId : friendList) {
            String friendName = userInfoMapper.selectName(friendId);
            result.add(String.valueOf(friendId) + "#" + friendName);
        }
        return result;
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
    public void recordMessage(int senderId, int recipientId, String messageType, String sendTime, String messageContent) throws DAOException {
        int insertNum = messageMapper.insertOne(senderId,recipientId,sendTime,messageType,messageContent);
        if(insertNum != 1) throw new DAOException("插入失败");
        return;
    }
}