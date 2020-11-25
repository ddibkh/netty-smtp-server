package com.mail.smtp.converter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class RegisterMessageConverter
{
    /*
    spring boot 에서는 MessageConverter 를 Bean 으로 등록하면 자동으로 JmsTemplate 에 적용된다.
    JmsMessagingTemplate can be injected in a similar manner.
    If a DestinationResolver or a MessageConverter bean is defined,
    it is associated automatically to the auto-configured JmsTemplate.
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter()
    {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
