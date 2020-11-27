package com.mail.smtp.delivery;

import com.mail.smtp.data.MailAttribute;
import com.mail.smtp.dns.DnsResolver;
import com.mail.smtp.dns.result.MXResult;
import com.mail.smtp.exception.DeliveryException;
import com.mail.smtp.exception.DnsException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("remoteDelivery")
@Data
@RequiredArgsConstructor
public class RemoteDelivery implements IDeliverySpec
{
    private final Logger log = LoggerFactory.getLogger("delivery");
    private final Executor deliveryPoolExecutor;
    private final DnsResolver dnsResolverMX;
    private final SendMail sendMail;

    @Override
    public DeliveryResult delivery(String toAddress, String emlPath, MailAttribute mailAttribute)
    {
        DeliveryResult deliveryResult = new DeliveryResult();
        deliveryResult.setEnvToAddress(toAddress);

        InternetAddress ia = null;

        try
        {
            ia = new InternetAddress(toAddress);
        }
        catch( AddressException addressException )
        {
            deliveryResult.setResult(DeliveryResult.DResult.FAILURE);
            deliveryResult.setMessage(addressException.getMessage());
            addressException.printStackTrace();
        }

        /*
        메일 수신시 address 유효성 체크를 거쳤기 때문에 예외 처리는 불필요 하다고 봄.
         */
        String domainName = toAddress.substring(toAddress.indexOf('@') + 1);
        boolean bSend = false;
        List<String> errList = new ArrayList<>();
        try
        {
            /*
            메일 발송을 위한 수신 도메인의 MX 레코드를 구해야 한다.
             */
            List<MXResult> dnsList = dnsResolverMX.resolveDomainByTcp(domainName);
            dnsList.stream().forEach(mx -> log.info("mx info : {}", mx.toString()));
            for( MXResult record : dnsList )
            {
                log.info("try to send MX host : {}", record.getRecord());
                Address[] addresses = new Address[1];
                addresses[0] = ia;
                try
                {
                    sendMail.send(mailAttribute.getEnvFrom(),
                            addresses, record.getRecord(), emlPath);
                    bSend = true;
                    break;
                }
                catch( DeliveryException de )
                {
                    errList.add("MX { " + record.getRecord() + " }, " + de.getMessage());
                }
            }
        }
        catch( DnsException de )
        {
            log.error("fail to MX record resolved, {}", de.getMessage());
            errList.add(String.format("failed to resolve mx recoed, {}", domainName));
        }
        finally
        {
            if( bSend )
            {
                deliveryResult.setResult(DeliveryResult.DResult.SUCCESS);
                deliveryResult.setMessage("success to message delivery");
            }
            else
            {
                deliveryResult.setResult(DeliveryResult.DResult.FAILURE);
                deliveryResult.setMessage(
                        errList.stream().collect(Collectors.joining(" | "))
                );
            }
        }

        return deliveryResult;
    }
}
