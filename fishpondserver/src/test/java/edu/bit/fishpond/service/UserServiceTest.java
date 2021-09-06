package edu.bit.fishpond.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import edu.bit.fishpond.DAO.ServiceDao;
import edu.bit.fishpond.service.entity.*;
import edu.bit.fishpond.utils.DAOException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * UserService单元测试类，与通信层、DAO层隔离测试，仅测试Service功能
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private ServiceDao serviceDao;

    /**
     * register注册功能service层测试
     * @throws DAOException 数据层操作异常
     */
    @Test
    public void registerHandlerTest() throws DAOException {
        RegisterClientEntity entity = new RegisterClientEntity();
        entity.setUserName("xch");
        entity.setPassword("123");
        entity.setSecurityQuestion("?");
        entity.setAnswer("y");
        List<ServerMessage> serverMessageList;

        //测试当返回的ID为20181575时
        Mockito.when(serviceDao.recordNewUser(
                entity.getUserName(),entity.getPassword(), entity.getSecurityQuestion(), entity.getAnswer()
        )).thenReturn(20181575);
        serverMessageList = userService.registerHandler(entity);
        for (ServerMessage serverMessage: serverMessageList) {
            Assert.assertEquals(0,serverMessage.getTargetId());
            Assert.assertEquals("RegisterResult|{\"userId\":20181575}",serverMessage.getMessage());
        }

    }

    @Test
    public void sendFriendRequestHandlerTest1() throws DAOException {
        FriendRequestClientEntity entity = new FriendRequestClientEntity();
        entity.setApplierId(12345678);
        entity.setRecipientId(20181575);
        entity.setExplain("");
        List<ServerMessage> serverMessageList;

        //当接收者在线时
        Mockito.when(serviceDao.queryOnlineStatusById(20181575)).thenReturn(true);
        serverMessageList = userService.sendFriendRequestHandler(entity);
        //应发送一条消息
        Assert.assertEquals(1, serverMessageList.size());
        for (ServerMessage serverMessage: serverMessageList) {
            //消息目标相同
            Assert.assertEquals(20181575,serverMessage.getTargetId());
            String message = serverMessage.getMessage();
            String[] splitArray = message.split("\\|",2);
            //消息可拆解
            Assert.assertEquals(2, splitArray.length);
            String messageHead = splitArray[0];
            String messageBody = splitArray[1];
            //消息头可识别
            Assert.assertEquals("NewFriendRequest",messageHead);
            FriendRequestServerEntity serverEntity = JSONObject.parseObject(messageBody, FriendRequestServerEntity.class);
            //消息体的内容正确
            Assert.assertEquals(12345678, serverEntity.getApplierId());
        }

        //验证测试内接口内的recordFriendRequest方法被调用
        ArgumentCaptor<Integer> integerArgumentCaptor1 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> integerArgumentCaptor2 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> stringArgumentCaptor1 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(serviceDao).recordNewFriendRequest(
                integerArgumentCaptor1.capture(), integerArgumentCaptor2.capture(),
                stringArgumentCaptor1.capture(), Mockito.anyString()
        );
        //验证接口内的recordFriendRequest方法参数正确
        Assert.assertEquals(Integer.valueOf(entity.getApplierId()), integerArgumentCaptor1.getValue());
        Assert.assertEquals(Integer.valueOf(entity.getRecipientId()), integerArgumentCaptor2.getValue());
        Assert.assertEquals(entity.getExplain(), stringArgumentCaptor1.getValue());

    }

    @Test
    public void sendFriendRequestHandlerTest2() throws DAOException {
        FriendRequestClientEntity entity = new FriendRequestClientEntity();
        entity.setApplierId(12345678);
        entity.setRecipientId(20181575);
        entity.setExplain("");
        List<ServerMessage> serverMessageList;

        //当接收者不在线时
        Mockito.when(serviceDao.queryOnlineStatusById(20181575)).thenReturn(false);
        serverMessageList = userService.sendFriendRequestHandler(entity);
        //验证此时不应该发送消息
        Assert.assertEquals(0, serverMessageList.size());

        //验证测试内接口内的recordFriendRequest方法被调用
        ArgumentCaptor<Integer> integerArgumentCaptor1 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> integerArgumentCaptor2 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> stringArgumentCaptor1 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(serviceDao).recordNewFriendRequest(
                integerArgumentCaptor1.capture(), integerArgumentCaptor2.capture(),
                stringArgumentCaptor1.capture(), Mockito.anyString()
        );
        //验证接口内的recordFriendRequest方法参数正确
        Assert.assertEquals(Integer.valueOf(entity.getApplierId()), integerArgumentCaptor1.getValue());
        Assert.assertEquals(Integer.valueOf(entity.getRecipientId()), integerArgumentCaptor2.getValue());
        Assert.assertEquals(entity.getExplain(), stringArgumentCaptor1.getValue());
    }

    @Ignore
    @Test
    public void sendMessageHandlerTest1() throws DAOException {
        MessageEntity entity = new MessageEntity();
        entity.setSenderId(11111111);
        entity.setRecipientId(20181575);
        entity.setMessageType("A");
        entity.setMessageContent("hello");
        List<ServerMessage> serverMessageList;

        //当接收者在线时
        Mockito.when(serviceDao.queryOnlineStatusById(20181575)).thenReturn(true);
        serverMessageList = userService.sendMessageHandler(entity);
        //验证发送一条消息
        Assert.assertEquals(1, serverMessageList.size());

        ServerMessage serverMessage = serverMessageList.get(0);
        //验证消息发送目标
        Assert.assertEquals(20181575, serverMessage.getTargetId());
        String message = serverMessage.getMessage();
        String[] splitArray = message.split("\\|",2);
        //消息可拆解
        Assert.assertEquals(2, splitArray.length);
        String messageHead = splitArray[0];
        String messageBody = splitArray[1];
        //消息头可识别
        Assert.assertEquals("NewMessage",messageHead);
        MessageEntity serverEntity = JSONObject.parseObject(messageBody, MessageEntity.class);
        //消息体内容正确
        Assert.assertEquals(11111111, serverEntity.getSenderId());
        Assert.assertEquals(20181575, serverEntity.getRecipientId());
        Assert.assertEquals("A",serverEntity.getMessageType());
        Assert.assertEquals("hello",serverEntity.getMessageContent());

        //验证测试内接口内的recordMessage方法被调用
        ArgumentCaptor<Integer> integerArgumentCaptor1 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> integerArgumentCaptor2 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> stringArgumentCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> stringArgumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(serviceDao).recordMessage(
                integerArgumentCaptor1.capture(),integerArgumentCaptor2.capture(),
                stringArgumentCaptor1.capture(), Mockito.anyString(),
                stringArgumentCaptor2.capture()
        );
        //验证接口内的recordMessage方法参数正确
        Assert.assertEquals(Integer.valueOf(entity.getSenderId()), integerArgumentCaptor1.getValue());
        Assert.assertEquals(Integer.valueOf(entity.getRecipientId()), integerArgumentCaptor2.getValue());
        Assert.assertEquals(entity.getMessageType(), stringArgumentCaptor1.getValue());
        Assert.assertEquals(entity.getMessageContent(), stringArgumentCaptor2.getValue());
    }

    @Ignore
    @Test
    public void sendMessageHandlerTest2() throws DAOException {
        MessageEntity entity = new MessageEntity();
        entity.setSenderId(11111111);
        entity.setRecipientId(20181575);
        entity.setMessageType("A");
        entity.setMessageContent("hello");
        List<ServerMessage> serverMessageList;

        //测试当接收者不在线时
        Mockito.when(serviceDao.queryOnlineStatusById(20181575)).thenReturn(false);
        serverMessageList = userService.sendMessageHandler(entity);
        //验证此时不应该发送消息
        Assert.assertEquals(0, serverMessageList.size());

        //验证测试内接口内的recordMessage方法被调用
        ArgumentCaptor<Integer> integerArgumentCaptor1 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> integerArgumentCaptor2 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> stringArgumentCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> stringArgumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(serviceDao).recordMessage(
                integerArgumentCaptor1.capture(),integerArgumentCaptor2.capture(),
                stringArgumentCaptor1.capture(), Mockito.anyString(),
                stringArgumentCaptor2.capture()
        );
        //验证接口内的recordMessage方法参数正确
        Assert.assertEquals(Integer.valueOf(entity.getSenderId()), integerArgumentCaptor1.getValue());
        Assert.assertEquals(Integer.valueOf(entity.getRecipientId()), integerArgumentCaptor2.getValue());
        Assert.assertEquals(entity.getMessageType(), stringArgumentCaptor1.getValue());
        Assert.assertEquals(entity.getMessageContent(), stringArgumentCaptor2.getValue());
    }

    @Test
    public void searchUserHandlerTest1(){
        SearchUserClientEntity entity = new SearchUserClientEntity();
        List<ServerMessage> serverMessageList;

        //设置搜索输入为123456且未搜索到结果的情况
        entity.setSearchInput("123456");
        Mockito.when(serviceDao.queryUserInfoByName(Mockito.anyString())).thenReturn(new ArrayList<>());
        Mockito.when(serviceDao.queryUserInfoById(Mockito.anyInt())).thenReturn("");

        serverMessageList = userService.searchUserHandler(entity);
        //验证发送一条消息
        Assert.assertEquals(1, serverMessageList.size());
        for (ServerMessage serverMessage : serverMessageList) {
            Assert.assertEquals(0, serverMessage.getTargetId());
            String message = serverMessage.getMessage();
            String[] splitArray = message.split("\\|",2);
            //消息可拆解
            Assert.assertEquals(2, splitArray.length);
            String messageHead = splitArray[0];
            String messageBody = splitArray[1];
            //消息头可识别
            Assert.assertEquals("SearchUserResult",messageHead);

            //消息体内容正确
            List<UserInfoServerEntity> serverEntities = JSONArray.parseArray(messageBody, UserInfoServerEntity.class);
            Assert.assertNotNull(serverEntities);
        }
        //验证此种情况下queryUserInfoById被调用一次
        Mockito.verify(serviceDao).queryUserInfoById(Mockito.anyInt());
        //验证此种情况下queryUserInfoByName被调用一次
        Mockito.verify(serviceDao).queryUserInfoByName(Mockito.anyString());



    }

    @Test
    public void searchUserHandlerTest2(){
        SearchUserClientEntity entity = new SearchUserClientEntity();
        List<ServerMessage> serverMessageList;

        //设置搜索输入为20181575且搜索到结果的情况
        entity.setSearchInput("20181575");
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("11223344#1120181575#男#无#中国#2021-9-1");
        arrayList.add("11223345#1220181575#女#无#中国#2021-9-2");
        Mockito.when(serviceDao.queryUserInfoByName(Mockito.anyString())).thenReturn(arrayList);
        Mockito.when(serviceDao.queryUserInfoById(Mockito.anyInt())).thenReturn("xch#?#?#?#2021-8-30");

        serverMessageList = userService.searchUserHandler(entity);
        //验证发送一条消息
        Assert.assertEquals(1, serverMessageList.size());
        ServerMessage serverMessage = serverMessageList.get(0);
        //验证消息发送目标
        Assert.assertEquals(0, serverMessage.getTargetId());
        String message = serverMessage.getMessage();
        String[] splitArray = message.split("\\|",2);
        //消息可拆解
        Assert.assertEquals(2, splitArray.length);
        String messageHead = splitArray[0];
        String messageBody = splitArray[1];
        //消息头可识别
        Assert.assertEquals("SearchUserResult",messageHead);
        //消息体内容正确
        List<UserInfoServerEntity> serverEntities = JSONArray.parseArray(messageBody, UserInfoServerEntity.class);
        Assert.assertEquals(11223344,serverEntities.get(0).getUserId());
        Assert.assertEquals(11223345,serverEntities.get(1).getUserId());
        //验证此种情况下queryUserInfoById被调用一次
        Mockito.verify(serviceDao).queryUserInfoById(Mockito.anyInt());
        //验证此种情况下queryUserInfoByName被调用一次
        Mockito.verify(serviceDao).queryUserInfoByName(Mockito.anyString());
    }

    @Test
    public void searchUserHandlerTest3(){
        SearchUserClientEntity entity = new SearchUserClientEntity();
        List<ServerMessage> serverMessageList;

        //配置通信层输入
        entity.setSearchInput("xch");
        ArrayList<String> arrayList = new ArrayList<>();

        //配置DAO层输入
        arrayList.add("11223344#xch#男#无#中国#2021-9-1");
        arrayList.add("11223345#axchbcd#男#无#中国#2021-9-1");
        Mockito.when(serviceDao.queryUserInfoByName(Mockito.anyString())).thenReturn(arrayList);

        serverMessageList = userService.searchUserHandler(entity);
        //验证要发送的消息条数为1
        Assert.assertEquals(1, serverMessageList.size());

        ServerMessage serverMessage = serverMessageList.get(0);
        //验证消息发送目标
        Assert.assertEquals(0, serverMessage.getTargetId());
        String message = serverMessage.getMessage();

        //消息可拆解
        String[] splitArray = message.split("\\|",2);
        Assert.assertEquals(2, splitArray.length);
        String messageHead = splitArray[0];
        String messageBody = splitArray[1];
        //消息头可识别
        Assert.assertEquals("SearchUserResult",messageHead);
        //消息体内容正确
        List<UserInfoServerEntity> serverEntities = JSONArray.parseArray(messageBody, UserInfoServerEntity.class);
        Assert.assertEquals(11223344,serverEntities.get(0).getUserId());
        Assert.assertEquals(11223345,serverEntities.get(1).getUserId());

        //验证此种情况下queryUserInfoById不被调用
        Mockito.verify(serviceDao, Mockito.never()).queryUserInfoById(Mockito.anyInt());
        //验证此种情况下queryUserInfoByName被调用一次
        Mockito.verify(serviceDao).queryUserInfoByName(Mockito.anyString());
    }

    @Test
    public void getFriendRequestFeedbackHandlerTest1() throws DAOException {
        FriendRequestFeedbackClientEntity entity = new FriendRequestFeedbackClientEntity();
        List<ServerMessage> serverMessageList;

        //配置通信层输入，接受邀请
        entity.setSenderId(20181234);
        entity.setRecipientId(20204321);
        entity.setResult(true);

        //配置DAO层输入,两人都在线的情况
        Mockito.when(serviceDao.queryOnlineStatusById(entity.getSenderId())).thenReturn(true);
        Mockito.when(serviceDao.queryOnlineStatusById(entity.getRecipientId())).thenReturn(true);
        Mockito.when(serviceDao.queryUserInfoById(entity.getSenderId())).thenReturn("20181234#uzi#男#无#无#2021-9-3");
        Mockito.when(serviceDao.queryUserInfoById(entity.getRecipientId())).thenReturn("20204321#faker#男#无#无#2021-9-2");

        serverMessageList = userService.getFriendRequestFeedbackHandler(entity);

        //验证要发送的消息条数为2
        Assert.assertEquals(2, serverMessageList.size());
        ServerMessage serverMessage1 = serverMessageList.get(0);
        ServerMessage serverMessage2 = serverMessageList.get(1);

        //验证消息发送目标
        Assert.assertEquals(20181234, serverMessage1.getTargetId());
        Assert.assertEquals(0, serverMessage2.getTargetId());

        String message1 = serverMessage1.getMessage();
        String message2 = serverMessage2.getMessage();

        //消息可拆解
        String[] splitArray1 = message1.split("\\|",2);
        String[] splitArray2 = message2.split("\\|",2);
        Assert.assertEquals(2, splitArray1.length);
        Assert.assertEquals(2, splitArray2.length);
        String messageHead1 = splitArray1[0];
        String messageBody1 = splitArray1[1];
        String messageHead2 = splitArray2[0];
        String messageBody2 = splitArray2[1];
        //消息头可识别
        Assert.assertEquals("NewFriend",messageHead1);
        Assert.assertEquals("NewFriend",messageHead2);
        //消息体内容正确
        UserInfoServerEntity userInfo1 = JSONObject.parseObject(messageBody1, UserInfoServerEntity.class);
        UserInfoServerEntity userInfo2 = JSONObject.parseObject(messageBody2, UserInfoServerEntity.class);
        Assert.assertEquals(20204321, userInfo1.getUserId());
        Assert.assertEquals(20181234, userInfo2.getUserId());

        //验证此时queryUserInfoById
        Mockito.verify(serviceDao, Mockito.times(2)).queryUserInfoById(Mockito.anyInt());
        Mockito.verify(serviceDao, Mockito.never()).recordSystemMessage(
                Mockito.anyInt(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString()
        );
        Mockito.verify(serviceDao).recordNewFriendship(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    public void getFriendRequestFeedbackHandlerTest2() throws DAOException {
        FriendRequestFeedbackClientEntity entity = new FriendRequestFeedbackClientEntity();
        List<ServerMessage> serverMessageList;

        //配置通信层输入，拒绝邀请
        entity.setSenderId(20181234);
        entity.setRecipientId(20204321);
        entity.setResult(false);

        //配置DAO层输入,两人都在线的情况
        Mockito.when(serviceDao.queryOnlineStatusById(entity.getSenderId())).thenReturn(true);
        Mockito.when(serviceDao.queryOnlineStatusById(entity.getRecipientId())).thenReturn(true);
        Mockito.when(serviceDao.queryUserInfoById(entity.getSenderId())).thenReturn("20181234#uzi#男#无#无#2021-9-3");
        Mockito.when(serviceDao.queryUserInfoById(entity.getRecipientId())).thenReturn("20204321#faker#男#无#无#2021-9-2");

        serverMessageList = userService.getFriendRequestFeedbackHandler(entity);

        //验证要发送的消息条数为1
        Assert.assertEquals(1, serverMessageList.size());
        ServerMessage serverMessage = serverMessageList.get(0);

        //验证消息发送目标
        Assert.assertEquals(20181234, serverMessage.getTargetId());

        String message = serverMessage.getMessage();

        //消息可拆解
        String[] splitArray1 = message.split("\\|",2);
        Assert.assertEquals(2, splitArray1.length);
        String messageHead = splitArray1[0];
        String messageBody = splitArray1[1];
        //消息头可识别
        Assert.assertEquals("SystemMessage",messageHead);
        //消息体内容正确
        SystemMessageEntity systemMessageEntity = JSONObject.parseObject(messageBody, SystemMessageEntity.class);
        Assert.assertEquals(20181234, systemMessageEntity.getUserId());

        //验证此时queryUserInfoById
        Mockito.verify(serviceDao, Mockito.never()).queryUserInfoById(Mockito.anyInt());
        Mockito.verify(serviceDao, Mockito.never()).recordSystemMessage(
                Mockito.anyInt(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString()
        );
        Mockito.verify(serviceDao, Mockito.never()).recordNewFriendship(
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()
        );
    }

    @Test
    public void getFriendRequestFeedbackHandlerTest3() throws DAOException {
        FriendRequestFeedbackClientEntity entity = new FriendRequestFeedbackClientEntity();
        List<ServerMessage> serverMessageList;

        //配置通信层输入，拒绝邀请
        entity.setSenderId(20181234);
        entity.setRecipientId(20204321);
        entity.setResult(false);

        //配置DAO层输入,两人都在线的情况
        Mockito.when(serviceDao.queryOnlineStatusById(entity.getSenderId())).thenReturn(false);
        Mockito.when(serviceDao.queryOnlineStatusById(entity.getRecipientId())).thenReturn(true);
        Mockito.when(serviceDao.queryUserInfoById(entity.getSenderId())).thenReturn("20181234#uzi#男#无#无#2021-9-3");
        Mockito.when(serviceDao.queryUserInfoById(entity.getRecipientId())).thenReturn("20204321#faker#男#无#无#2021-9-2");

        serverMessageList = userService.getFriendRequestFeedbackHandler(entity);

        //验证要发送的消息条数为0
        Assert.assertEquals(0, serverMessageList.size());

        //验证此时queryUserInfoById
        Mockito.verify(serviceDao, Mockito.never()).queryUserInfoById(Mockito.anyInt());
        Mockito.verify(serviceDao).recordSystemMessage(
                Mockito.anyInt(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString()
        );
        Mockito.verify(serviceDao, Mockito.never()).recordNewFriendship(
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()
        );
    }

    @Test
    public void getFriendRequestFeedbackHandlerTest4() throws DAOException {
        FriendRequestFeedbackClientEntity entity = new FriendRequestFeedbackClientEntity();
        List<ServerMessage> serverMessageList;

        //配置通信层输入，拒绝邀请
        entity.setSenderId(20181234);
        entity.setRecipientId(20204321);
        entity.setResult(true);

        //配置DAO层输入,两人都在线的情况
        Mockito.when(serviceDao.queryOnlineStatusById(entity.getSenderId())).thenReturn(false);
        Mockito.when(serviceDao.queryOnlineStatusById(entity.getRecipientId())).thenReturn(true);
        Mockito.when(serviceDao.queryUserInfoById(entity.getSenderId())).thenReturn("20181234#uzi#男#无#无#2021-9-3");
        Mockito.when(serviceDao.queryUserInfoById(entity.getRecipientId())).thenReturn("20204321#faker#男#无#无#2021-9-2");

        serverMessageList = userService.getFriendRequestFeedbackHandler(entity);

        //验证要发送的消息条数为1
        Assert.assertEquals(1, serverMessageList.size());

        ServerMessage serverMessage1 = serverMessageList.get(0);

        //验证消息发送目标
        Assert.assertEquals(0, serverMessage1.getTargetId());

        String message1 = serverMessage1.getMessage();

        //消息可拆解
        String[] splitArray1 = message1.split("\\|",2);
        Assert.assertEquals(2, splitArray1.length);
        String messageHead1 = splitArray1[0];
        String messageBody1 = splitArray1[1];
        //消息头可识别
        Assert.assertEquals("NewFriend",messageHead1);
        //消息体内容正确
        UserInfoServerEntity userInfo1 = JSONObject.parseObject(messageBody1, UserInfoServerEntity.class);
        Assert.assertEquals(20181234, userInfo1.getUserId());

        //验证此时queryUserInfoById
        Mockito.verify(serviceDao).queryUserInfoById(Mockito.anyInt());
        Mockito.verify(serviceDao, Mockito.never()).recordSystemMessage(
                Mockito.anyInt(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString()
        );
        Mockito.verify(serviceDao).recordNewFriendship(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    public void getUnreadFriendRequestHandlerTest1() {
        List<ServerMessage> serverMessageList;

        //配置通信层输入
        SingleIntEntity entity = new SingleIntEntity();
        entity.setUserId(11111111);

        //配置数据层输入
        List<String> list = new ArrayList<>();
        list.add("20182906#2021-9-4 10:58:49#我是刘睿");
        list.add("12345678#2021-9-4 11:00:20#你好");
        Mockito.when(serviceDao.queryFriendRequestList(entity.getUserId())).thenReturn(list);
        Mockito.when(serviceDao.queryUserInfoById(12345678)).
                thenReturn("12345678#测试用户2#女#2021-9-4#中国北京#2021-9-4");
        Mockito.when(serviceDao.queryUserInfoById(20182906)).thenReturn("20182906#lr#男###");

        serverMessageList = userService.getUnreadFriendRequestHandler(entity);

        //验证要发送的消息条数为1
        Assert.assertEquals(1, serverMessageList.size());

        ServerMessage serverMessage = serverMessageList.get(0);

        //验证消息发送目标
        Assert.assertEquals(0, serverMessage.getTargetId());

        String message = serverMessage.getMessage();

        //消息可拆解
        String[] splitArray = message.split("\\|",2);
        Assert.assertEquals(2, splitArray.length);
        String messageHead = splitArray[0];
        String messageBody = splitArray[1];
        //消息头可识别
        Assert.assertEquals("UnreadFriendRequest",messageHead);
        //消息体内容正确
        List<FriendRequestServerEntity> serverEntityList =
                JSONArray.parseArray(messageBody, FriendRequestServerEntity.class);
        Assert.assertEquals("lr" , serverEntityList.get(0).getApplierName());
        Assert.assertEquals(20182906, serverEntityList.get(0).getApplierId());
        Assert.assertEquals("我是刘睿", serverEntityList.get(0).getExplain());
        Assert.assertEquals("测试用户2", serverEntityList.get(1).getApplierName());
        Assert.assertEquals(12345678, serverEntityList.get(1).getApplierId());
        Assert.assertEquals("你好", serverEntityList.get(1).getExplain());

        Mockito.verify(serviceDao, Mockito.times(2)).queryUserInfoById(Mockito.anyInt());
    }
}