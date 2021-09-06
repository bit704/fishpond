package edu.bit.fishpond.service;

import edu.bit.fishpond.utils.DAOException;

import java.util.List;

public interface IServiceDao {

    /**
     * 清空数据库
     */
    void clearDAO();
    /**
     * 注册，添加新的用户
     * @param userName 新用户的用户名
     * @param password 新用户的密码
     * @param securityQuestion 新用户的密保问题
     * @param answer 新用户密保问题的答案
     * @return 新用户的ID
     */
    int recordNewUser(String userName, String password, String securityQuestion, String answer) throws DAOException;

    /**
     * 创建新的群聊
     * @param groupName 新群聊的名称
     * @param creatorId 创建者ID
     * @param createTime 创建时间
     * @return 系统分配的群聊Id
     */
    int recordNewGroup(String groupName, int creatorId, String createTime);

    /**
     * 通过用户Id查询其在线状态
     * @param userId 要查询的用户的Id
     * @return 在线状态
     */
    boolean queryOnlineStatusById(int userId) throws DAOException;

    /**
     * 更新用户的在线状态（直接取反即可）
     * @param userId 用户id
     */
    void updateOnlineStatus(int userId) throws DAOException;

    /**
     * 好友申请，添加新的好友申请
     * @param applierId 申请者Id
     * @param recipientId 接收者Id
     * @param explain 附加信息
     * @param sendTime 申请发送时间
     */
    void recordNewFriendRequest(int applierId, int recipientId, String explain, String sendTime) throws DAOException;

    /**
     * 检查用户id和密码是否匹配
     * @param userId 用户id
     * @param passwordHash 密码hash值
     * @return 是否匹配
     */
    boolean checkPassword(int userId, String passwordHash);

    /**
     * 获取两个用户间的所有消息,按时间顺序从老到新排列
     * @param userId1 用户1id
     * @param userId2 用户2id
     * @return 两个用户间的消息ID列表
     */
    List<Integer> queryAllMessageBetween(int userId1, int userId2);

    /**
     * 获取该用户作为接收者时的好友申请
     * @param recipientId 用户Id（作为接收者）
     * @return 好友申请列表（格式：发送者ID#发送时间#附加信息）
     */
    List<String> queryFriendRequestList(int recipientId);

    /**
     * 新的好友关系
     * @param userId1 用户1
     * @param userId2 用户2
     * @param beFriendTime 成为好友的时间
     */
    void recordNewFriendship(int userId1, int userId2, String beFriendTime) throws DAOException;

    /**
     * 新的系统消息
     * @param userId 目标用户id
     * @param sendTime 发送时间
     * @param messageType 消息类型
     * @param content 消息内容
     * @return 返回系统消息id
     */
    int recordSystemMessage(int userId, String sendTime, String messageType, String content) throws DAOException;

    /**
     * 需求更新！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
     * 删除指定申请者和接收者的好友申请，并同时删除可能出现的申请者、接收者相反的好友申请
     * @param applierId 申请者id
     * @param recipientId 接收者id
     */
    void deleteFriendRequest(int applierId, int recipientId) throws DAOException;

    /**
     * 通过ID获取该用户的好友列表
     * @return 返回该用户的所有好友Id
     */
    List<Integer> queryFriendList(int senderId);

    /**
     * 通过ID获取其信息
     * @param id 要查询的用户的ID
     * @return 返回该用户的用户信息（格式：ID#用户名#性别#生日#地区#注册时间），如果ID不存在，则返回""
     */
    String queryUserInfoById(int id);

    /**
     * 通过用户名获取其信息
     * @param nameSubString 用于查询用户名的子串
     * @return 返回符合的所有用户的用户信息（格式：ID#用户名#性别#生日#地区#注册时间），如果没有符合条件的用户，返回empty
     */
    List<String> queryUserInfoByName(String nameSubString);

    /**
     * 获取最新的消息
     * @param userId 用户id
     * @return 与该用户有关的所有消息的最后一条消息Id列表
     */
    List<Integer> queryLatestMessageList(int userId);

    /**
     * 向群聊中添加新的成员
     * @param groupId 群聊ID
     * @param userId 用户ID
     * @param invitorId 邀请人ID
     * @param inTime 加入时间
     */
    void recordNewMember(int groupId, int userId, int invitorId, String inTime) throws DAOException;

    /**
     * 查询id对应用户加入的所有群聊ID
     * @param userId 要查询的用户的id
     * @return 群id列表
     */
    List<Integer> queryGroupList(int userId);

    /**
     * 删除指定id对应的用户
     * @param userId 用户id
     */
    void deleteUser(int userId);

    /**
     * 检查好友申请是否已经存在
     * @return 是否存在
     */
    boolean checkFriendRequestExist(int applierId, int recipientId);

    /**
     * 通过群id查询该群所有群成员
     * @param groupId 群id
     * @return 群成员的id列表
     */
    List<Integer> queryGroupMemberList(int groupId);

    /**
     * 删除这条消息
     * @param messageId 消息Id
     */
    void deleteMessage(int messageId);

    /**
     * 查询该id是否存在
     * @param userId 要查询的id
     * @return 是否存在
     */
    boolean checkUserIdExist(int userId);

    /**
     * 查询群id是否存在
     * @param groupId 要查询的id
     * @return 是否存在
     */
    boolean checkGroupIdExist(int groupId);

    /**
     * 记录一条新的消息
     * @param senderId 发送者Id
     * @param recipientId 接收者Id
     * @param messageType 消息类型
     * @param sendTime 发送时间
     * @param messageContent 消息内容
     * @return 消息的id
     */
    int recordNewMessage(int senderId, int recipientId, String messageType, String sendTime, String messageContent);

    /**
     * 获取发送给指定用户的未读消息(根据上次离线时间和消息发送时间判断),应按时间顺序从老到新排列
     * @param recipientId 用户Id
     * @return 返回未读消息ID列表,无则返回empty，绝对不要返回null
     */
    List<Integer> queryUnreadMessageList(int recipientId);

    /**
     * 通过消息的Id查询消息
     * @param messageId 消息Id
     * @return 返回该消息的信息（格式：发送者ID#接收者ID#消息类型#发送时间#消息内容）
     */
    String queryMessageInfoById(int messageId);

    /**
     * 检查该id对应的消息是否存在
     * @param checkId 要检查的id
     * @return 是否存在
     */
    boolean checkMessageExist(int checkId);

    /**
     * 通过群聊id查询群聊信息
     * @param groupId 群聊id
     * @return 群聊信息（格式：群聊名称#创建者id#群主id#创建时间）
     */
    String queryGroupInfoById(int groupId);

    /**
     * 删除指定的群聊
     * @param groupId 群聊id
     */
    void deleteGroup(int groupId);

    /**
     * 查询该ID对应的系统消息的信息
     * @param systemMessageId 系统消息id
     * @return 系统消息信息（格式：目标用户id#发送时间#消息类型#消息内容）
     */
    String querySystemMessageInfoById(int systemMessageId);

    /**
     * 获取该用户未读的系统消息
     * @param userId 用户id
     * @return 未读系统消息id列表
     */
    List<Integer> queryUnreadSystemMessageList(int userId);

    /**
     * 获取该用户未读的群消息
     * @param userId 用户id
     * @return 未读群消息id列表
     */
    List<Integer> queryUnreadGroupMessageList(int userId);

    /**
     * 记录一条新的消息
     * @param senderId 发送者Id
     * @param groupId 群聊Id
     * @param messageType 消息类型
     * @param sendTime 发送时间
     * @param messageContent 消息内容
     * @return 消息的id
     */
    int recordNewGroupMessage(int senderId, int groupId, String messageType, String sendTime, String messageContent);

    /**
     * 通过群消息的Id查询消息信息
     * @param messageId 消息Id
     * @return 返回该消息的信息（格式：发送者ID#接收者ID#消息类型#发送时间#消息内容）
     */
    String queryGroupMessageInfoById(int messageId);

    /**
     * 获取与该用户相关的最新的群消息
     * @param userId 用户id
     * @return 与该用户有关的所有群消息的最后一条群消息Id列表
     */
    List<Integer> queryLatestGroupMessageList(int userId);

    /**
     * 删除该群消息
     * @param groupMessageId 要删除的群消息id
     */
    void deleteGroupMessage(int groupMessageId);

    /**
     * 检查该id对应的消息是否存在
     * @param checkId 要检查的id
     * @return 是否存在
     */
    boolean checkGroupMessageExist(int checkId);

    /**
     * 获取这个群的所有消息
     * @param groupId 群id
     * @return 该群的所有消息id列表
     */
    List<Integer> queryAllMessageIn(int groupId);

}
