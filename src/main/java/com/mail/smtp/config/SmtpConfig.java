package com.mail.smtp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@PropertySource(value = {
        "file:config/smtp.properties",
}, ignoreResourceNotFound = true)
@Slf4j
public class SmtpConfig
{
    private final Environment environment;

    @PostConstruct
    void Init()
    {

    }

    public Integer getInt(String key, Integer value)
    {
        return environment.getProperty(key, Integer.class, value);
    }

    public Long getLong(String key, Long value)
    {
        return environment.getProperty(key, Long.class, value);
    }

    public String getString(String key, String value)
    {
        //설정항목이 없는 경우 value 가 리턴된다.
        String value2 = environment.getProperty(key, value);
        return value2.equals("") ? value : value2;
    }
}
