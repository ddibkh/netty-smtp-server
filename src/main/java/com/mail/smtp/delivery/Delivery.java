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
                        .thenAccept((result) -> log.info("{}", result.toString())))
                .collect(Collectors.toList());

        listFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());

        log.debug("end of delivery function");
    }
}
