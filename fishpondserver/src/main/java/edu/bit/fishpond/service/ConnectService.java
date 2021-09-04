package edu.bit.fishpond.service;

import edu.bit.fishpond.service.entity.LoginClientEntity;
import edu.bit.fishpond.service.entity.UserIdEntity;
import edu.bit.fishpond.utils.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ConnectService")
public class ConnectService {

    private final IServiceDao serviceDao;
    private final Logger logger = LoggerFactory.getLogger(ConnectService.class);

    @Autowired
    public ConnectService(IServiceDao serviceDao){
        this.serviceDao = serviceDao;
    }

    public boolean login(LoginClientEntity loginClientEntity) throws DAOException {
        int loginId = loginClientEntity.getLoginUserId();
        String passwordHash = loginClientEntity.getPasswordHash();
        boolean queryResult = serviceDao.checkPassword(loginId, passwordHash);
        if (queryResult){
            boolean queryResult2 = serviceDao.queryOnlineStatusById(loginId);
            if (queryResult2){
                return false;
            }
            else {
                serviceDao.updateOnlineStatusById(loginId);
            }

        }

        return queryResult;
    }

    public void offLine(UserIdEntity userIdEntity) throws DAOException {
        int offLineId = userIdEntity.getUserId();
        serviceDao.updateOnlineStatusById(offLineId);
    }
}
