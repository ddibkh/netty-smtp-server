package com.mail.smtp.delivery;

import com.mail.smtp.data.MailAttribute;
import com.mail.smtp.data.UserVO;
import com.mail.smtp.entity.MailBoxEntity;
import com.mail.smtp.exception.DeliveryException;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.service.SaveMailService;
import com.mail.smtp.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component("localDelivery")
@Data
@RequiredArgsConstructor
public class LocalDelivery implements IDeliverySpec
{
    private final Logger log = LoggerFactory.getLogger("delivery");
    private final SaveMailService saveMailService;
    private final UserService userService;
    private final Executor deliveryPoolExecutor;

    @Override
    public DeliveryResult delivery(String toAddress, String emlPath, MailAttribute mailAttribute)
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
