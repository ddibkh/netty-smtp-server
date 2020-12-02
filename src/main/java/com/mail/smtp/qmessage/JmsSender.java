package com.mail.smtp.qmessage;

import com.mail.smtp.data.QueueData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JmsSender
{
    private final JmsTemplate jmsTemplate;

    public void sendMessageQueue(QueueData queueData)
    {
        String uid = Optional.ofNullable(queueData.getMailUid()).orElse("");
        log.trace("[{}] send queue message, {}", uid, queueData.toString());
        jmsTemplate.convertAndSend("mta.queue", queueData);
    }
}
