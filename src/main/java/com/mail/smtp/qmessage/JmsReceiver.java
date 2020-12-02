package com.mail.smtp.qmessage;

import com.mail.smtp.data.QueueData;
import com.mail.smtp.delivery.Delivery;
import com.mail.smtp.delivery.IDeliverySpec;
import com.mail.smtp.exception.DeliveryException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.CompletionException;

@Component
@RequiredArgsConstructor
public class JmsReceiver
{
    private final Logger log = LoggerFactory.getLogger("delivery");
    private final IDeliverySpec localDelivery;
    private final IDeliverySpec remoteDelivery;
    private final Delivery delivery;

    /*
    이 안에서 예외를 제대로 잡지 못하면 message 가 다시 수신된다.
     */
    @JmsListener(destination = "mta.queue")
    public void receiveQueueMessage(QueueData queueData)
    {
        //MDC.put("UID", queueData.getMailAttribute().getMailUid());
        log.info("[{}] receive queue message", queueData.getMailUid());

        /*
        delivery mail
        remote 수신자는 domain 별로 분리한다. (rctp to 반복 메일 발송을 위해서..)
        rept to 반복으로 하나의 session 으로 발송하는 것과 수신자별 session 으로 발송하는 것은
        각각 장단점이 있다.
        여기서는 localDelivery, RemoteDelivery 를 각각 비동기로 처리하고 결과를 출력한다.
        추후 각 수신자별로 메일 배달 처리를 비동기로 처리하도록 하는 CompletableFuture 사용을 고려해보자.
        */

        //local delivery
        try
        {
            delivery.delivery(queueData.getToLocal(), queueData.getQueuePath(), queueData.getMailAttribute(), localDelivery);
        }
        catch( DeliveryException de )
        {
            log.info("[{}] local delivery, {}", queueData.getMailUid(), de.getMessage());
        }
        catch( CompletionException ce )
        {
            log.info("[{}] local delivery, {}", queueData.getMailUid(), ce.getCause().getMessage());
        }

        //remote delivery
        try
        {
            delivery.delivery(queueData.getToRemote(), queueData.getQueuePath(), queueData.getMailAttribute(), remoteDelivery);
        }
        catch( DeliveryException | CompletionException de )
        {
            log.info("[{}] remote delivery, {}", queueData.getMailUid(), de.getMessage());
        }

        deleteQFile(queueData.getMailUid(), queueData.getQueuePath());

        log.debug("[{}] end of jms receiver process", queueData.getMailUid());

        //MDC.remove("UID");
    }

    private void deleteQFile(String uid, String queuePath)
    {
        File queueFile = new File(queuePath);
        if( queueFile.exists() && queueFile.delete() )
            log.info("[{}] queue file deleted [{}]", uid, queuePath);
    }
}
