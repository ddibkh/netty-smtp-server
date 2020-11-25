package com.mail.smtp.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.FailureCallback;

public class DnslookupFailureHandler implements FailureCallback
{
    private final Logger log = LoggerFactory.getLogger("delivery");
    @Override
    public void onFailure(Throwable throwable)
    {
        log.error("fail to delivery, {}", throwable.getMessage());
    }
}
