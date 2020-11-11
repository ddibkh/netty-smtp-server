package com.mail.smtp.mta.authentication;

import com.mail.smtp.entity.UserEntity;
import com.mail.smtp.exception.AuthException;
import com.mail.smtp.util.codec.SHA256;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlainPwdAuth implements CheckAuth
{
    @Override
    public boolean checkAuth(String id, String pwd)
    {
        UserEntity user;
        try{
            user = CheckAuth.getUser(id);
        }catch( AuthException ae ) {
            log.error("fail to auth, {}", ae.getMessage());
            return false;
        }

        return user.getUserPwd().equals(pwd);
    }
}
