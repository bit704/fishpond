package edu.bit.fishpond;

import edu.bit.fishpond.DAO.DO.MessageDO;
import edu.bit.fishpond.DAO.mapper.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

public class DAOTest extends FishpondApplicationTests {

    public static final Logger testLogger = LoggerFactory.getLogger(DAOTest.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    FriendshipMapper friendshipMapper;

    @Autowired
    FriendRequestMapper friendRequestMapper;

    @Test
    public void testUserInfo() {

       testLogger.info("测试输出："+ userInfoMapper.deleteAll());
       testLogger.info("测试输出："+ userInfoMapper.insertOne(1,
               "刘睿",
                "M",
                "2000-01-01",
                "湖南",
                false,
                "2021-08-30",
                "2021-08-30",
                false));
       //testLogger.info("测试输出：", userInfoMapper.selectBatch("uid","10000000").toString());

       testLogger.info("测试输出："+ userInfoMapper.updateOne("region","'湖北'",10000000));
       testLogger.info("测试输出："+ userInfoMapper.updateOne("state","true",10000000));

       testLogger.info("测试输出："+ userInfoMapper.selectAll().toString());

       testLogger.info("测试输出："+ userInfoMapper.deleteAll());
    }

    @Test
    public void test(){
        testLogger.info("测试输出：" + userMapper.deleteAll());
        testLogger.info("测试输出：" + userMapper.resetSeq(10000000));
        testLogger.info("测试输出：" + userMapper.getLastSqValue());
        testLogger.info("测试输出：" + userMapper.insertOne("kjkjk","whoareyou","刘睿"));
        testLogger.info("测试输出：" + userMapper.selectAll().toString());
        testLogger.info("测试输出：" + userMapper.getLastSqValue());
        testLogger.info("测试输出：" + userMapper.insertOne("kjkjk","whoareyou","徐尘化"));
        testLogger.info("测试输出：" + userMapper.selectAll().toString());
        testLogger.info("测试输出：" + userMapper.getLastSqValue());
        testLogger.info("测试输出：" + userMapper.insertOne("kjkjk","whoareyou","徐尘化"));
        testLogger.info("测试输出：" + userMapper.selectAll().toString());
        testLogger.info("测试输出：" + userMapper.getLastSqValue());
        testLogger.info("测试输出：" + userMapper.deleteAll());
    }

    @Test
    public void userMock() {
        testLogger.info("测试输出：" + userMapper.deleteAll());
        testLogger.info("测试输出：" + userMapper.resetSeq(10000000));
        testLogger.info("测试输出：" + userMapper.insertOne("111","你是谁","徐尘化"));
        testLogger.info("测试输出：" + userMapper.insertOne("222","你是谁","徐尘化"));
        testLogger.info("测试输出：" + userMapper.insertOne("333","你是谁","徐尘化"));

    }

    @Test
    public void testMessage() {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
        messageMapper.insertOne(10000000,10000001,localDateTime.toString(),"","你好");
        messageMapper.insertOne(10000000,10000001,"2021-09-02 10:57:07","","你也好");
        List<MessageDO> messageDOList = messageMapper.selectByReceiverBeforeTime(10000000,"2021-09-02 10:57:07");
        System.out.println(messageDOList.toString());
        System.out.println(messageMapper.selectAll());
        System.out.println(messageMapper.selectByPartnerLatest(10000000,10000001));
        messageMapper.deleteAll();
    }

    @Test
    public void testString() {
        StringJoiner message = new StringJoiner("#");
        message.add("i love");
        message.add("you");
        System.out.println(message.toString());
    }
    @Test
    public void testUpdate() {
        userInfoMapper.deleteAll();
        userInfoMapper.insertOne(10000000,"刘睿啊",null,null,null,false,null,null,false);
        userInfoMapper.updateOne("last_offline","'2021-09-02 10:57:07'",10000000);
        System.out.println(userInfoMapper.selectAll());
        System.out.println(userInfoMapper.selectBySubName("睿"));
        userInfoMapper.deleteAll();
    }

    @Test
    public void testDelete() {
        friendRequestMapper.deleteAll();
        System.out.println(friendRequestMapper.insertOne(10000000,10000001,"2021-09-02 10:57:07",""));
        System.out.println(friendRequestMapper.deleteByPK(10000000,10000001));
    }

}
