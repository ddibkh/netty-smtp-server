package com.mail.smtp.delivery;

import com.mail.smtp.exception.DeliveryException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.FailureCallback;

//메일 배달 실패시 실행되는 callback functional
public class DeliveryFailureHandler implements FailureCallback
{
    private final Logger log = LoggerFactory.getLogger("delivery");

    @Override
    public void onFailure(Throwable throwable)
    {
        log.error("delivery failure : {}", throwable.getMessage());
    }
}
