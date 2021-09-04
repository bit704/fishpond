package edu.bit.fishpondops.service;

import edu.bit.fishpondops.DAO.DO.UserInfoDO;
import edu.bit.fishpondops.DAO.mapper.UserInfoMapper;
import edu.bit.fishpondops.DAO.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserMapper userMapper;

    public List<UserInfoDO> getAllUsers() {
        return userInfoMapper.selectAll();
    }
}
