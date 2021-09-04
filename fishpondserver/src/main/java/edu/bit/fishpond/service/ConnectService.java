package edu.bit.fishpond.service;

import edu.bit.fishpond.service.entity.LoginClientEntity;
import edu.bit.fishpond.service.entity.UserIdEntity;
import edu.bit.fishpond.utils.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("ConnectService")
public class ConnectService {

    private final IServiceDao iServiceDao;
    private final Logger logger = LoggerFactory.getLogger(ConnectService.class);

    @Autowired
    public ConnectService(@Qualifier("IServiceDaoImpl") IServiceDao iServiceDao){
        this.iServiceDao = iServiceDao;
    }

    public boolean login(LoginClientEntity loginClientEntity) throws DAOException {
        int loginId = loginClientEntity.getLoginUserId();
        String passwordHash = loginClientEntity.getPasswordHash();
        boolean queryResult = iServiceDao.checkPassword(loginId, passwordHash);
        if (queryResult){
            boolean queryResult2 = iServiceDao.queryOnlineStatusById(loginId);
            // 如果已经在线，则不能登录
            if (queryResult2){
                logger.warn("用户已在线");
                return false;
            }
            else {
                iServiceDao.updateOnlineStatusById(loginId);
                logger.info("登录后," + loginId + "的在线状态为" + iServiceDao.queryOnlineStatusById(loginId));
            }

        }

        return queryResult;
    }

    public void offLine(UserIdEntity userIdEntity) throws DAOException {
        int offLineId = userIdEntity.getUserId();
        iServiceDao.updateOnlineStatusById(offLineId);
    }
}
