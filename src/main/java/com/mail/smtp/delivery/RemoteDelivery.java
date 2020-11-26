package com.mail.smtp.delivery;

import com.mail.smtp.data.MailAttribute;
import com.mail.smtp.data.UserVO;
import com.mail.smtp.dns.DnsResolver;
import com.mail.smtp.dns.result.DnsResult;
import com.mail.smtp.dns.result.MXResult;
import com.mail.smtp.entity.MailBoxEntity;
import com.mail.smtp.exception.DeliveryException;
import com.mail.smtp.exception.DnsException;
import com.mail.smtp.exception.SmtpException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("remoteDelivery")
@Data
public class RemoteDelivery implements Delivery
{
    private final Logger log = LoggerFactory.getLogger("delivery");
    @Resource
    private Executor deliveryPoolExecutor;
    @Resource(name = "dnsResolverMX")
    private DnsResolver dnsResolverMX;

    public void delivery(List< String > to,
                                         String queuePath,
                                         MailAttribute mailAttribute)
    {
        if( to.isEmpty() )
            throw new DeliveryException("remote delivery, receiver empty");

        File file = new File(queuePath);
        if( !file.exists() )
            throw new DeliveryException("remote delivery, not exist queue file");

        Map<String, List<String>> domainGroup =
                to.stream().collect(Collectors.groupingBy(address -> {
                    int index = address.indexOf('@');
                    return address.substring(index + 1);
                }));

        List< CompletableFuture > listFuture = domainGroup.entrySet().stream()
                .map(entry -> CompletableFuture.supplyAsync(() -> remoteDeliveryByDomain(entry.getKey(), entry.getValue(), queuePath, mailAttribute), deliveryPoolExecutor)
                .thenAccept((result) -> log.info("{}", result.toString())))
                .collect(Collectors.toList());

        listFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());

        log.debug("end of remoteDelivery function");
        return;
    }

    private DeliveryResult remoteDeliveryByDomain(String domainName, List<String> toList, String emlPath, MailAttribute mailAttribute)
    {
        DeliveryResult deliveryResult = new DeliveryResult();
        String receivers = toList.stream().collect(Collectors.joining(","));
        deliveryResult.setEnvToAddress(receivers);

        List< MXResult > dnsList = dnsResolverMX.resolveDomainByTcp(domainName);
        try
        {
            dnsList = dnsResolverMX.resolveDomainByTcp(domainName);
            for( MXResult record : dnsList )
            {
                /*
                메일 발송
                 */
            }
        }
        catch( DnsException de )
        {
            log.error("fail to MX record resolved, {}", de.getMessage());
            deliveryResult.setResult(DeliveryResult.DResult.FAILURE);
            deliveryResult.setMessage(String.format("failed to resolve mx recoed, {}", domainName));
        }

        return deliveryResult;
    }
}
