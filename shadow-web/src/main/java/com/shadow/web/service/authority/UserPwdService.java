package com.shadow.web.service.authority;

import com.shadow.web.model.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * @Auther: 10413
 * @Date: 2021-04-10 16:00
 * @Description:
 */
@Service
public class UserPwdService {
    private static Logger log = LoggerFactory.getLogger(UserPwdService.class);


    /**
     * @param password 需要加密的明文
     * @return BCrypt 加密
     * @auther wangzhendong
     * @date 2018/12/21 18:21
     */
    public Result<String> encryptPassword(String password) {
        try {
            return Result.returnSuccess(BCrypt.hashpw(password, BCrypt.gensalt()));
        } catch (Exception e) {
            log.error("encryptPassword error: {}", e);
            return Result.returnError("encryptPassword error");
        }
    }
}
