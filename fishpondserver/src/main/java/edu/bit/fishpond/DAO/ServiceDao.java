package edu.bit.fishpond.DAO;

import edu.bit.fishpond.service.IServiceDao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ServiceDao implements IServiceDao {
    @Override
    public void clearDAO() {

    }

    @Override
    public int recordNewUser(String userName, String password, String securityQuestion, String answer) {
        return 20182906;
    }

    @Override
    public int recordNewGroup(String groupName, int creatorId, String createTime) {
        return 0;
    }

    @Override
    public boolean queryOnlineStatusById(int userId) {
        return false;
    }

    @Override
    public void updateOnlineStatus(int userId) {

    }

    @Override
    public void recordNewFriendRequest(int applierId, int recipientId, String explain, String sendTime) {

    }

    @Override
    public boolean checkPassword(int userId, String passwordHash) {
        return true;
    }

    @Override
    public List<String> getUnreadMessage(int recipientId) {
        List<String> list = new ArrayList<>();
        list.add("20181575#11111111#A#2021-9-21 21:55:30#unread1");
        list.add("20181575#11111111#A#2021-9-21 21:56:30#unread2");
        list.add("20181575#11111111#A#2021-9-21 21:57:30#unread3");
        list.add("20173488#11111111#A#2021-9-21 21:53:22#unread4");
        list.add("20173488#11111111#A#2021-9-21 21:59:22#unread5");
        return list;
    }

    @Override
    public List<String> queryAllMessage(int userId) {
        List<String> list = new ArrayList<>();
        list.add("20181575#11111111#A#2021-9-21#cccc");
        list.add("11111111#20181575#A#2021-9-21#aaa");
        list.add("20173488#11111111#A#2021-9-21#cao");
        list.add("20181575#11111111#A#2021-9-21#cao");
        return list;
    }

    @Override
    public List<String> queryAllMessageBetween(int userId1, int userId2) {
        List<String> list = new ArrayList<>();
        list.add("20182906#11111111#A#2021-9-21 15:25:00#?");
        list.add("11111111#20182906#A#2021-9-21 15:26:37#hello");
        list.add("20182906#11111111#A#2021-9-21 15:26:49#你谁啊");
        list.add("20182906#11111111#A#2021-9-21 15:26:58#为什么在我的好友列表里??????????????????????????????????????????");
        return list;
    }

    @Override
    public List<String> queryFriendRequestList(int recipientId) {
        List<String> list = new ArrayList<>();
        list.add("20182906#2021-9-4 10:58:49#我是刘睿");
        list.add("12345678#2021-9-4 11:00:20#你好");
        return list;
    }

    @Override
    public void recordNewFriendship(int userId1, int userId2, String beFriendTime) {
    }

    @Override
    public void recordSystemMessage(int userId, String sendTime, String messageType, String content) {

    }

    @Override
    public void deleteFriendRequest(int applierId, int recipientId) {

    }

    @Override
    public List<Integer> queryFriendList(int senderId) {
        List<Integer> res = new ArrayList<>();
        res.add(20181575);
        res.add(20173488);
        return res;
    }

    @Override
    public String queryUserInfoById(int id) {
        switch (id){
            case 20181575:
                return "20181575#xch#男#无#无#无";
            case 20173488:
                return "20173488#mhn#男#无#无#无";
            case 11111111:
                return "11111111#测试用户#男#无#无#无";
            case 20182906:
                return "20182906#lr#男###";
            case 11111123:
                return "11111123#用户#女###";
            case 12345678:
                return "12345678#测试用户2#女#2021-9-4#中国北京#2021-9-4";
            default:
                return "";
        }
    }

    @Override
    public List<String> queryUserInfoByName(String nameSubString) {
        List<String> res = new ArrayList<>();
        if (nameSubString.equals("1")){
            res.add("11111123#1111123#无#无#无#无");
            res.add("11111123#x1111#无#无#无#无");
            res.add("11111123#lala1111#无#无#无#无");
            res.add("11111123#cd1111we#无#无#无#无");
        }
        else {
            res.add("22222222#kkkkk2#无#无#无#无");
            res.add("21122112#22345678#无#无#无#无");
        }
        return res;
    }

    @Override
    public void recordMessage(int senderId, int recipientId, String messageType, String sendTime, String messageContent) {

    }

    @Override
    public List<String> queryLatestMessageList(int userId) {
        List<String> list = new ArrayList<>();
        list.add("11111111#20181575#A#2021-9-21 15:24:30#aaa");
        list.add("20173488#11111111#A#2021-9-21 16:17:22#bbbbb");
        return list;
    }

    @Override
    public void recordNewMember(int groupId, int userId, int invitorId, String inTime) {

    }

    @Override
    public List<String> queryGroupList(int userId) {
        return null;
    }

    @Override
    public void deleteUser(int userId) {

    }

    @Override
    public boolean checkFriendRequestExist(int applierId, int recipientId) {
        return false;
    }

    @Override
    public List<Integer> queryGroupMemberList(int groupId) {
        return null;
    }

    @Override
    public void deleteMessage(int messageId) {

    }

    @Override
    public boolean checkUserIdExist(int userId) {
        return false;
    }

    @Override
    public boolean checkGroupIdExist(int groupId) {
        return false;
    }
}
