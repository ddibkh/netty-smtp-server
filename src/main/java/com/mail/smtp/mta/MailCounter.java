package com.mail.smtp.mta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

public class MailCounter {
    @Autowired
    private AtomicInteger counter;

    public Integer count()
    {
        Integer n;
        synchronized (counter) {
            n = counter.incrementAndGet();
        }
        return n;
    }
}
