package com.mail.smtp.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.List;

//메일 배달 성공시 실행되는 callback functional
public class DeliverySuccessHandler implements SuccessCallback< List<DeliveryResult>>
{
    private final Logger log = LoggerFactory.getLogger("delivery");

    @Override
    public void onSuccess(List< DeliveryResult > deliveryResults)
    {
        deliveryResults.stream().forEach((result) -> log.info(result.toString()));
    }
}
