package edu.bit.fishpondops.service;

import edu.bit.fishpondops.DAO.mapper.UserInfoMapper;
import edu.bit.fishpondops.DAO.mapper.UserMapper;
import edu.bit.fishpondops.utils.GenerateRandomInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MockService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    GenerateRandomInfo generateRandomInfo;

    /**
     * @param num 数量
     * 生成假用户
     */
    public void mockUsers(int num) {
        for(int i=0; i<num; i++) {
            String name = generateRandomInfo.generateName();
            String sex = generateRandomInfo.generateSex();
            String birthday = generateRandomInfo.generateBirthday();
            String region = generateRandomInfo.generateRegion();
            boolean state = generateRandomInfo.generateState();
            String create_time = LocalDateTime.now().toString();
            String last_offline = create_time;
            boolean real = false;
            userMapper.insertOne("666666","666666","666666");
            int uid = userMapper.getLastSqValue();
            userInfoMapper.insertOne(uid,name,sex,birthday,region,state,create_time,last_offline,real);
        }
        return;
    }

}
