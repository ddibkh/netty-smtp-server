package com.mail.smtp.mta.data;

import com.mail.smtp.entity.DomainEntity;
import com.mail.smtp.entity.UserEntity;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.mta.ApplicationContextProvider;
import com.mail.smtp.repository.DomainRepository;
import com.mail.smtp.repository.UserRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Data
public class UserVO
{
    private boolean local;
    private String address;
    private String addressId;
    private String addressDomain;
    private String addressPerson;
    private int domainIndex;
    private int userIndex;

    public UserVO()
    {
        init();
    }

    public void init()
    {
        local = false;
        address = "";
        addressId = "";
        addressDomain = "";
        addressPerson = "";
        domainIndex = -1;
        userIndex = -1;
    }
}
