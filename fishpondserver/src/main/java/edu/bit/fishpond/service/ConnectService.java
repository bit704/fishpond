package edu.bit.fishpond.service;

import edu.bit.fishpond.service.entity.LoginClientEntity;
import edu.bit.fishpond.service.entity.SingleIntEntity;
import edu.bit.fishpond.utils.DAOException;
import edu.bit.fishpond.utils.secureplus.RSAPrivateKey;
import edu.bit.fishpond.utils.secureplus.SecureForServerp;
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

    public boolean login(LoginClientEntity loginClientEntity, RSAPrivateKey privateKey) throws DAOException {
        int loginId = loginClientEntity.getLoginUserId();
        if (!iServiceDao.checkUserIdExist(loginId)){
            logger.warn("用户不存在：" + loginId);
            return false;
        }
        String password = loginClientEntity.getPasswordHash();
        String passwordDecrypt = SecureForServerp.decryptRSA(password, privateKey);
        boolean queryResult = iServiceDao.checkPassword(loginId, passwordDecrypt);
        if (queryResult){
            boolean queryResult2 = iServiceDao.queryOnlineStatusById(loginId);
            // 如果已经在线，则不能登录
            if (queryResult2){
                logger.warn("用户已在线");
                return false;
            }
            else {
                iServiceDao.updateOnlineStatus(loginId);
                logger.info("登录后," + loginId + "的在线状态为" + iServiceDao.queryOnlineStatusById(loginId));
            }

        }

        return queryResult;
    }

    public void offLine(SingleIntEntity singleIntEntity) throws DAOException {
        int offLineId = singleIntEntity.getUserId();
        iServiceDao.updateOnlineStatus(offLineId);
    }


}
