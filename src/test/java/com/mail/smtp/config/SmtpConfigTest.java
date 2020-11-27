package com.mail.smtp.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest( classes = {
        SmtpConfig.class
})
class SmtpConfigTest
{
    @Autowired
    private SmtpConfig smtpConfig;

    @Test
    void defaultValue()
    {
        String dns = smtpConfig.getString("smtp.dns.ip", "8.8.8.8");
        assertTrue(dns.equals("8.8.8.8"));
    }
}