package com.mail.smtp.qmessage;

import com.mail.smtp.data.QueueData;
import com.mail.smtp.delivery.*;
import com.mail.smtp.exception.DeliveryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JmsReceiver
{
    private final Logger log = LoggerFactory.getLogger("delivery");
    @Resource(name = "localDelivery")
    private Delivery localDelivery;
    @Resource(name = "remoteDelivery")
    private Delivery remoteDelivery;
    @Resource
    private Executor deliveryPoolExecutor;

    /*@JmsListener(destination = "mta.queue")
    public void receiveQueueMessage(QueueData queueData)
    {
        String uid = Optional.ofNullable(queueData.getMailUid()).orElse("");
        MDC.put("UID", queueData.getMailAttribute().getMailUid());
        log.info("receive queue message");

        *//*
        delivery mail
        remote 수신자는 domain 별로 분리한다. (rctp to 반복 메일 발송을 위해서..)
        rept to 반복으로 하나의 session 으로 발송하는 것과 수신자별 session 으로 발송하는 것은
        각각 장단점이 있다.
        여기서는 localDelivery, RemoteDelivery 를 각각 비동기로 처리하고 결과를 출력한다.
        추후 각 수신자별로 메일 배달 처리를 비동기로 처리하도록 하는 CompletableFuture 사용을 고려해보자.
        *//*

        //local delivery
        ListenableFuture< List<DeliveryResult> > futureLocal =
                localDelivery.delivery(queueData.getToLocal(), queueData.getQueuePath(), queueData.getMailAttribute());

        futureLocal.addCallback(
            new SuccessCallback< List< DeliveryResult > >()
            {
                @Override
                public void onSuccess(List< DeliveryResult > deliveryResults)
                {
                    deliveryResults.stream().forEach((result) -> log.info("{}", result.toString()));
                }
            },
            new FailureCallback()
            {
                @Override
                public void onFailure(Throwable throwable)
                {
                    log.error("{}", throwable.getMessage());
                }
            }
        );

        //remote delivery
        ListenableFuture< List<DeliveryResult> > futureRemote =
                remoteDelivery.delivery(queueData.getToRemote(), queueData.getQueuePath(), queueData.getMailAttribute());

        futureRemote.addCallback(
            new SuccessCallback< List< DeliveryResult > >()
            {
                @Override
                public void onSuccess(List< DeliveryResult > deliveryResults)
                {
                    deliveryResults.stream().forEach((result) -> log.info(result.toString()));
                    deleteQFile(queueData.getQueuePath());
                    MDC.clear();
                }
            },
            new FailureCallback()
            {
                @Override
                public void onFailure(Throwable throwable)
                {
                    log.error("{}", throwable.getMessage());
                    deleteQFile(queueData.getQueuePath());
                    MDC.clear();
                }
            }
        );

        log.debug("end of jms receiver function");

        return;
    }*/

    @JmsListener(destination = "mta.queue")
    public void receiveQueueMessage(QueueData queueData)
    {
        String uid = Optional.ofNullable(queueData.getMailUid()).orElse("");
        MDC.put("UID", queueData.getMailAttribute().getMailUid());
        log.info("receive queue message");

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
            localDelivery.delivery(queueData.getToLocal(), queueData.getQueuePath(), queueData.getMailAttribute());
        }
        catch( DeliveryException de )
        {
            log.info("{}", de.getMessage());
        }

        //remote delivery
        try
        {
            remoteDelivery.delivery(queueData.getToRemote(), queueData.getQueuePath(), queueData.getMailAttribute());
        }
        catch( DeliveryException de )
        {
            log.info("{}", de.getMessage());
        }


        /*CompletableFuture localFuture = CompletableFuture.supplyAsync(() ->
                localDelivery.delivery(queueData.getToLocal(), queueData.getQueuePath(), queueData.getMailAttribute()), deliveryPoolExecutor)
                .thenAccept(results -> results.stream().forEach(result -> log.info("{}", result.toString())));

        //remote delivery
        CompletableFuture remoteFuture = CompletableFuture.supplyAsync(() ->
                remoteDelivery.delivery(queueData.getToRemote(), queueData.getQueuePath(), queueData.getMailAttribute()), deliveryPoolExecutor)
                .thenAccept(results -> results.stream().forEach(result -> log.info("{}", result.toString())));

        //완료 대기. (큐 파일을 삭제해야 하기 때문에 완료 처리에 대한 동기 처리가 필요하다. 부득이하게...)
        try
        {
            localFuture.join();
        }
        catch( CompletionException ce )
        {
            log.info("{}", ce.getMessage());
        }

        try
        {
            remoteFuture.join();
        }
        catch( CompletionException ce )
        {
            log.info("{}", ce.getMessage());
        }*/

        deleteQFile(queueData.getQueuePath());

        log.debug("end of jms receiver function");

        MDC.remove("UID");

        return;
    }

    private void deleteQFile(String queuePath)
    {
        File queueFile = new File(queuePath);
        if( queueFile.exists() && queueFile.delete() )
            log.info("queue file deleted [{}]", queuePath);
    }
}
