package com.mail.smtp.service;

import com.mail.smtp.config.SmtpConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest( classes = {
        DnsService.class,
        SmtpConfig.class
} )
class DnsServiceTest
{
    @Autowired
    private DnsService dnsService;

    @Test
    void resolveMxRecordByTcp()
    {
        try
        {
            dnsService.resolveMxRecordByTcp("kakao.com");
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}