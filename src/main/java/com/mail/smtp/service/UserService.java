package com.mail.smtp.service;

import com.mail.smtp.entity.DomainEntity;
import com.mail.smtp.entity.UserEntity;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.data.UserVO;
import com.mail.smtp.repository.DomainRepository;
import com.mail.smtp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService
{
    private final DomainRepository domainRepository;
    private final UserRepository userRepository;

    public UserVO fetchUserVO(String rawAddress) throws SmtpException
    {
        InternetAddress internetAddress;
        try
        {
            internetAddress= new InternetAddress(rawAddress);
        }
        catch( AddressException ae )
        {
            SmtpException smtpException = new SmtpException(501);
            smtpException.initCause(ae);
            throw smtpException;
        }

        String address = internetAddress.getAddress();
        String personal = internetAddress.getPersonal();
        UserVO userVO = new UserVO();
        userVO.setAddress(address);
        userVO.setAddressPerson(personal);

        int delimiterPosition = address.indexOf('@');
        String userId = address.substring(0, delimiterPosition);
        String userDomain = address.substring(delimiterPosition + 1);
        userVO.setAddressId(userId);
        userVO.setAddressDomain(userDomain);
        Optional< DomainEntity > optDomain = domainRepository.findByDomainName(userDomain);
        if( optDomain.isPresent() )
        {
            int domainIndex = optDomain.get().getIdx();
            Optional< UserEntity > optUser =
                    userRepository.findByUseridAndDomain(userId, optDomain.get());
            optUser.orElseThrow(() -> new SmtpException(550));
            int userIndex = optUser.get().getIdx();

            userVO.setLocal(true);
            userVO.setUserIndex(userIndex);
            userVO.setDomainIndex(domainIndex);
        }
        else
        {
            userVO.setLocal(false);
            userVO.setUserIndex(-1);
            userVO.setDomainIndex(-1);
        }

        return userVO;
    }
}
