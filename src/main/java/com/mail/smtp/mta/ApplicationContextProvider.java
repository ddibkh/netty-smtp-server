package com.mail.smtp.mta;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ApplicationContextProvider implements ApplicationContextAware
{
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException
    {
        applicationContext = ctx;
    }

    public static <T> T getBean(Class<T> tClass)
    {
        return applicationContext.getBean(tClass);
    }

    public static <T> T getBean(String beanName, Class<T> tClass)
    {
        return applicationContext.getBean(beanName, tClass);
    }
}
