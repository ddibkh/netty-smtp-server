package com.mail.smtp.exception;

public class UserException extends SmtpException
{
    public UserException(int errorCode)
    {
        super(errorCode);
    }
}
