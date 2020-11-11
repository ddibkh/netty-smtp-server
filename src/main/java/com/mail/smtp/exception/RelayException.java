package com.mail.smtp.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class RelayException extends SmtpException
{
    private final String ip;
    public RelayException(String ip)
    {
        super(500);
        this.ip = ip;
    }

    @Override
    public String getMessage()
    {
        return "Relaying denied. not allowed IP " + ip;
    }

    public String getResponse()
    {
        return String.valueOf(super.getErrorCode()) + " 5.7.1 " + getMessage() + "\r\n";
    }
}
