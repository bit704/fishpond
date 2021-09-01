package edu.bit.fishpond;

import edu.bit.fishpond.DAO.mapper.UserInfoMapper;
import edu.bit.fishpond.DAO.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DAOTest extends FishpondApplicationTests {

    public static final Logger testLogger = LoggerFactory.getLogger(DAOTest.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserInfoMapper userInfoMapper;

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
        testLogger.info("测试输出：" + userMapper.resetSeq(10000000));
        testLogger.info("测试输出：" + userMapper.insertOne("kjkjk","whoareyou","刘睿"));
        testLogger.info("测试输出：" + userMapper.selectAll().toString());
        testLogger.info("测试输出：" + userMapper.deleteAll());
    }

}
