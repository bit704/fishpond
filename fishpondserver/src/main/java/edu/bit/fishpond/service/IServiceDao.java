package edu.bit.fishpond.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IServiceDao {

    /**
     * 注册，添加新的用户
     * @param userName 新用户的用户名
     * @param password 新用户的密码
     * @param securityQuestion 新用户的密保问题
     * @param answer 新用户密保问题的答案
     * @return 新用户的ID
     */
    int recordNewUser(String userName, String password, String securityQuestion, String answer);

    /**
     * 通过用户Id查询其在线状态
     * @param userId 要查询的用户的Id
     * @return 在线状态
     */
    boolean queryOnlineStatusById(int userId);

    /**
     * 更新用户的在线状态（直接取反即可）
     * @param userId 用户id
     */
    void updateOnlineStatusById(int userId);

    /**
     * 好友申请，添加新的好友申请
     * @param applierId 申请者Id
     * @param recipientId 接收者Id
     * @param explain 附加信息
     * @param sendTime 申请发送时间
     */
    void recordFriendRequest(int applierId, int recipientId, String explain, String sendTime);

    /**
     * 检查用户id和密码是否匹配
     * @param userId 用户id
     * @param passwordHash 密码hash值
     * @return 是否匹配
     */
    boolean checkPassword(int userId, String passwordHash);

    /**
     * 获取发送给指定用户的未读消息(根据上次离线时间和消息发送时间判断)
     * @param recipientId 消息接收者id
     * @return 未读消息列表（格式：发送者ID#消息类型#发送时间#消息内容）,无则返回empty，绝对不要返回null
     */
    List<String> getUnreadMessage(int recipientId);

    /**
     * 获取所有与该用户有关的消息，按时间顺序排列
     * @param userId 用户id
     * @return 消息列表（格式：发送者ID#接收者ID#消息类型#发送时间#消息内容）
     */
    List<String> queryAllMessage(int userId);

    /**
     * 获取该用户作为接收者时的好友申请
     * @param recipientId 用户Id（作为接收者）
     * @return 好友申请列表（格式：发送者ID#发送时间#附加信息）
     */
    List<String> queryFriendRequest(int recipientId);

    /**
     * 新的好友关系
     * @param userId1 用户1
     * @param userId2 用户2
     * @param beFriendTime 成为好友的时间
     */
    void recordFriendship(int userId1, int userId2, String beFriendTime);

    /**
     * 新的系统消息
     * @param userId 目标用户id
     * @param sendTime 发送时间
     * @param messageType 消息类型
     * @param content 消息内容
     */
    void recordSystemMessage(int userId, String sendTime, String messageType, String content);

    /**
     * 删除指定申请者和接收者的好友申请
     * @param applierId 申请者id
     * @param recipientId 接收者id
     */
    void deleteFriendRequest(int applierId, int recipientId);

    /**
     * 通过ID获取该用户的好友列表及好友信息
     * @return 返回该用户的所有好友（格式：好友ID#好友用户名）
     */
    List<String> queryFriendshipById(int senderId);

    /**
     * 通过ID获取其信息
     * @param id 要查询的用户的ID
     * @return 返回该用户的用户信息，如果ID不存在，则返回""
     */
    String queryUserInfoById(int id);

    /**
     * 通过用户名获取其信息
     * @param nameSubString 用于查询用户名的子串
     * @return 返回符合的所有用户的用户信息（格式：ID#用户名#性别#生日#地区#注册时间），如果没有符合条件的用户，返回empty
     */
    List<String> queryUserInfoByName(String nameSubString);

    /**
     * 记录一条新的消息
     * @param senderId 发送者Id
     * @param recipientId 接收者Id
     * @param messageType 消息类型
     * @param sendTime 发送时间
     * @param messageContent 消息内容
     */
    void recordMessage(int senderId, int recipientId, String messageType, String sendTime, String messageContent);

}
