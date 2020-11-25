package com.mail.smtp.delivery;

import com.mail.smtp.data.MailAttribute;
import com.mail.smtp.data.UserVO;
import com.mail.smtp.entity.MailBoxEntity;
import com.mail.smtp.exception.DeliveryException;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.service.SaveMailService;
import com.mail.smtp.service.UserService;
import com.mail.smtp.util.CommonUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component("localDelivery")
@Data
@RequiredArgsConstructor
public class LocalDelivery implements Delivery
{
    private final Logger log = LoggerFactory.getLogger("delivery");
    private final SaveMailService saveMailService;
    private final UserService userService;
    private final Executor deliveryPoolExecutor;

    /*public List<DeliveryResult> delivery(List< String > to,
                                         String queuePath,
                                         MailAttribute mailAttribute)
    {
        if( to.isEmpty() )
            throw new DeliveryException("local delivery, receiver empty");

        File file = new File(queuePath);
        if( !file.exists() )
            throw new DeliveryException("local delivery, not exist queue file");

        List<DeliveryResult> results = new ArrayList<>();
        for( String toAddress : to )
        {
            DeliveryResult deliveryResult;
            deliveryResult = localDelivery(toAddress, queuePath, mailAttribute);
            results.add(deliveryResult);
        }

        log.debug("end of localDelivery function");

        return results;
    }*/

    public void delivery(List< String > to,
                                         String queuePath,
                                         MailAttribute mailAttribute)
    {
        if( to.isEmpty() )
            throw new DeliveryException("local delivery, receiver empty");

        File file = new File(queuePath);
        if( !file.exists() )
            throw new DeliveryException("local delivery, not exist queue file");

        List< CompletableFuture > listFuture = to.stream()
                .map(address -> CompletableFuture.supplyAsync(() -> localDelivery(address, queuePath, mailAttribute), deliveryPoolExecutor)
                .thenAccept((result) -> log.info("{}", result.toString())))
                .collect(Collectors.toList());

        listFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());

        log.debug("end of localDelivery function");
        return;
    }

    private DeliveryResult localDelivery(String toAddress, String emlPath, MailAttribute mailAttribute)
    {
        DeliveryResult deliveryResult = new DeliveryResult();
        deliveryResult.setEnvToAddress(toAddress);

        UserVO userVO;
        try
        {
            //수신자 정보를 얻어옴.
            userVO = userService.fetchUserVO(toAddress);

            if( !saveMailService.saveUserBox(userVO, emlPath, mailAttribute, MailBoxEntity.MBOX_NAME_INBOX) )
            {
                deliveryResult.setResult(DeliveryResult.DResult.FAILURE);
                deliveryResult.setMessage("fail to save user mail box");
                return deliveryResult;
            }
        }
        catch( SmtpException se )
        {
            deliveryResult.setResult(DeliveryResult.DResult.FAILURE);
            deliveryResult.setMessage("fail to save user mail box, can't find user");
            return deliveryResult;
        }
        catch( Exception e )
        {
            deliveryResult.setResult(DeliveryResult.DResult.FAILURE);
            deliveryResult.setMessage("fail to delivery, exception occured " + e.getMessage());
            return deliveryResult;
        }

        deliveryResult.setResult(DeliveryResult.DResult.SUCCESS);
        deliveryResult.setMessage("success to local delivery");
        return deliveryResult;
    }
}
