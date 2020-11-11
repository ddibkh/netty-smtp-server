package com.mail.smtp.mta.authentication;

import com.mail.smtp.entity.DomainEntity;
import com.mail.smtp.entity.UserEntity;
import com.mail.smtp.exception.AuthException;
import com.mail.smtp.mta.ApplicationContextProvider;
import com.mail.smtp.repository.DomainRepository;
import com.mail.smtp.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Predicate;

@FunctionalInterface
public interface CheckAuth
{
    static UserEntity getUser(String id)
    {
        Optional.ofNullable(id).orElseThrow(() -> new AuthException("in CheckAuth, id null"));
        UserRepository userRepository = ApplicationContextProvider.getBean(UserRepository.class);

        //check smtp id or address
        int nIndex = id.indexOf('@');
        Optional<UserEntity> optUser;
        if( nIndex > 0 )
        {
            String userId = id.substring(0, nIndex);
            String userDomain = id.substring(nIndex + 1);
            DomainRepository domainRepository = ApplicationContextProvider.getBean(DomainRepository.class);
            Optional<DomainEntity> optDomain = domainRepository.findByDomainName(userDomain);
            optDomain.orElseThrow(() -> new AuthException("in CheckAuth, not exist domain"));
            optUser = userRepository.findByUseridAndDomain(userId, optDomain.get());

        }
        else
        {
            optUser = userRepository.findByUserid(id);
        }

        optUser.orElseThrow(() -> new AuthException("not exist user info"));
        return optUser.get();
    }

    boolean checkAuth(String id, String pwd);
}
