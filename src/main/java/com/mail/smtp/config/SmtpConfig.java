package com.mail.smtp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLOutput;

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
        log.info("smtp.port : {}", environment.getProperty("smtp.port", Integer.class, 25));
        log.info("install path : {}", environment.getProperty("install_path", ""));
        System.out.println("smtp.port: " + environment.getProperty("smtp.port", Integer.class, 25));
        System.out.println("install_path : " + environment.getProperty("install_path", ""));
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
        return environment.getProperty(key, value);
    }
}
