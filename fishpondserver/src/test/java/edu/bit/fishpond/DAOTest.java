package edu.bit.fishpond;

import edu.bit.fishpond.DAO.mapper.HumanMapper;
import edu.bit.fishpond.DAO.mapper.UserInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DAOTest extends FishpondApplicationTests {

    @Autowired
    HumanMapper humanMapper;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Test
    public void testMapper() {
        System.out.println(humanMapper.selectAll().toString());
    }

    @Test
    public void testUserInfo() {

        System.out.println(userInfoMapper.resetSeq());
        System.out.println(userInfoMapper.insertOne("刘睿",
                "M",
                "2000-01-01",
                "湖南",
                false,
                "2021-08-30",
                "2021-08-30",
                false));
        System.out.println(userInfoMapper.selectAll().toString());
        //System.out.println(userInfoMapper.updateOne("region","湖北",1));
        //System.out.println(userInfoMapper.updateOne("state","true",1));
        //System.out.println(userInfoMapper.selectOne(1).toString());
        System.out.println(userInfoMapper.deleteAll());
    }
}
