package edu.bit.fishpondops.service;

import edu.bit.fishpondops.DAO.DO.GroupInfoDO;
import edu.bit.fishpondops.DAO.DO.UserInfoDO;
import edu.bit.fishpondops.DAO.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@Component
public class QueryService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    GroupInfoMapper groupInfoMapper;

    @Autowired
    FriendshipMapper friendshipMapper;

    @Autowired
    GroupMemberMapper groupMemberMapper;

    @Autowired
    MessageMapper messageMapper;

    /**
     * 用户信息的中文表示
     *
     * @param userInfoDO
     * @return
     */
    private String userinfo2String(UserInfoDO userInfoDO) {
        StringJoiner stringJoiner = new StringJoiner(",");
        stringJoiner.add("用户ID：" + userInfoDO.getUid());
        stringJoiner.add("用户昵称：" + userInfoDO.getName());
        stringJoiner.add("性别：" + userInfoDO.getSex());
        stringJoiner.add("生日：" + userInfoDO.getBirthday());
        stringJoiner.add("地区：" + userInfoDO.getRegion());
        stringJoiner.add("在线状态：" + userInfoDO.getState());
        stringJoiner.add("建号时间：" + userInfoDO.getCreate_time());
        stringJoiner.add("上次在线时间：" + userInfoDO.getLast_offline());
        stringJoiner.add("是否为真实用户：" + userInfoDO.getReal());
        return stringJoiner.toString();
    }

    /**
     * 群信息的中文表示
     *
     * @param groupInfoDO
     * @return
     */
    private String groupinfo2String(GroupInfoDO groupInfoDO) {
        StringJoiner stringJoiner = new StringJoiner(",");
        stringJoiner.add("群聊ID：" + groupInfoDO.getGid());
        stringJoiner.add("群聊名称：" + groupInfoDO.getName());
        stringJoiner.add("创建者ID：" + groupInfoDO.getCreator());
        stringJoiner.add("创建时间：" + groupInfoDO.getCreate_time());
        stringJoiner.add("群主ID：" + groupInfoDO.getManager());
        stringJoiner.add("是否真实群：" + groupInfoDO.isReal());
        return stringJoiner.toString();
    }

    /**
     * 获取所有用户
     *
     * @return
     */
    public String getAllUsers() {
        StringBuffer buffer = new StringBuffer();
        int usersNum = userMapper.selectCount();
        if (usersNum <= 20) {
            for (UserInfoDO userInfoDO : userInfoMapper.selectAll()) {
                buffer.append(userinfo2String(userInfoDO) + "\n");
            }
        } else {
            buffer.append(getUsers(20));
            buffer.append("...\n");
            buffer.append("最多显示20条\n");
        }
        return buffer.toString();
    }

    /**
     * 获取所有群
     * @return
     */
    public String getAllGroups() {
        StringBuffer buffer = new StringBuffer();
        int groupsNum = groupInfoMapper.selectCount();
        if(groupsNum <= 20) {
            for(GroupInfoDO groupInfoDO : groupInfoMapper.selectAll()) {
                buffer.append(groupinfo2String(groupInfoDO) + "\n");
            }
        } else {
            buffer.append(getGroups(20));
            buffer.append("...\n");
            buffer.append("最多显示20条\n");
        }
        return buffer.toString();
    }

    /**
     * 获取指定数量的用户
     *
     * @param num
     * @return
     */
    public String getUsers(int num) {
        StringBuffer buffer = new StringBuffer();
        for (UserInfoDO userInfoDO : userInfoMapper.selectBatch(num)) {
            buffer.append(userinfo2String(userInfoDO) + "\n");
        }
        return buffer.toString();
    }

    /**
     * 获取指定数量的群
     *
     * @param num
     * @return
     */
    public String getGroups(int num) {
        StringBuffer buffer = new StringBuffer();
        for(GroupInfoDO groupInfoDO : groupInfoMapper.selectBatch(num)) {
            buffer.append(groupinfo2String(groupInfoDO) + "\n");
        }
        return buffer.toString();
    }

    /**
     * 获取指定用户
     *
     * @param uid
     * @return
     */
    public String getSpecifiedUser(int uid) {
        UserInfoDO userInfoDO = userInfoMapper.selectOneById(uid);
        if (userInfoDO == null) return "此用户不存在";
        StringBuffer buffer = new StringBuffer();
        buffer.append(userinfo2String(userInfoDO) + "\n");
        buffer.append("好友数：" + friendshipMapper.selectFriendNumById(uid) + "\n");
        buffer.append("加群数：" + groupMemberMapper.selectGroupNumByUid(uid) + "\n");
        return buffer.toString();
    }

    /**
     * 获取指定群
     *
     * @param gid
     * @return
     */
    public String getSpecifiedGroup(int gid) {
        GroupInfoDO groupInfoDO = groupInfoMapper.selectOneById(gid);
        if(groupInfoDO == null) return "此群不存在";
        StringBuffer buffer = new StringBuffer();
        buffer.append(groupinfo2String(groupInfoDO)+"\n");
        buffer.append("群成员数："+ groupMemberMapper.selectUserNumByGid(gid));
        return buffer.toString();
    }

    /**
     * 获取负载
     *
     * @return
     */
    public String getLoad() {
        StringJoiner stringJoiner = new StringJoiner("\n");
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分ss秒");
        stringJoiner.add(String.format("%-25s", "统计时间：") + dTF.format(LocalDateTime.now()));
        stringJoiner.add(String.format("%-25s", "用户总数：") + userMapper.selectCount());
        stringJoiner.add(String.format("%-25s", "在线用户：") + userInfoMapper.selectCountActive());
        stringJoiner.add(String.format("%-25s", "群数：") + groupInfoMapper.selectCount());
        LocalDateTime time = LocalDateTime.now();
        //减去一分钟
        time = time.minusMinutes(1);
        //过去一分钟系统新增的消息数
        int messageNum = messageMapper.selectCountBeforeTime(time.toString());
        stringJoiner.add(String.format("%-25s", "每秒钟流通消息数：") + String.format("%.2f", (double) messageNum / 60));
        return stringJoiner.toString();
    }
}
