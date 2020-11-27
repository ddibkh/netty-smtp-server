package com.mail.smtp.exception;

public class DeliveryException extends RuntimeException
{
    public DeliveryException(String message)
    {
        super(message);
    }
}
