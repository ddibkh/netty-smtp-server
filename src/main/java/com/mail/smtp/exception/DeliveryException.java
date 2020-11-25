package com.mail.smtp.exception;

import com.mail.smtp.delivery.DeliveryResult;

public class DeliveryException extends RuntimeException
{
    public DeliveryException(String message)
    {
        super(message);
    }
}
