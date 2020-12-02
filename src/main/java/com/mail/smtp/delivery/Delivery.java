package com.mail.smtp.delivery;

import com.mail.smtp.data.MailAttribute;
import com.mail.smtp.exception.DeliveryException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component("delivery")
@RequiredArgsConstructor
public class Delivery
{
    private final Logger log = LoggerFactory.getLogger("delivery");
    private final Executor deliveryPoolExecutor;

    public void delivery(List< String > to,
                  String queuePath,
                  MailAttribute mailAttribute,
                  IDeliverySpec spec) throws DeliveryException
    {
        if( to.isEmpty() )
            throw new DeliveryException("receiver empty");

        File file = new File(queuePath);
        if( !file.exists() )
            throw new DeliveryException("not exist queue file");

        List< CompletableFuture<Void> > listFuture = to.stream()
                .map(address -> CompletableFuture.supplyAsync(() -> spec.delivery(address, queuePath, mailAttribute), deliveryPoolExecutor)
                        .exceptionally(throwable -> {
                            log.error("[{}] fail to delivery exception occured, {}", mailAttribute.getMailUid(), throwable.getMessage());
                            DeliveryResult deliveryResult = new DeliveryResult();
                            deliveryResult.setResult(DeliveryResult.DResult.FAILURE);
                            deliveryResult.setEnvToAddress(address);
                            deliveryResult.setMessage(throwable.getMessage());
                            return deliveryResult;
                        })
                        .thenAccept((result) -> log.info("[{}] {}", mailAttribute.getMailUid(), result.toString())))
                .collect(Collectors.toList());

        listFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());

        if( spec instanceof LocalDelivery )
            log.debug("[{}] end of local delivery", mailAttribute.getMailUid());
        else
            log.debug("[{}] end of remote delivery", mailAttribute.getMailUid());
    }
}
