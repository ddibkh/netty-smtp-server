package com.mail.smtp.mta.handler;

import com.mail.smtp.mta.SmtpData;
import io.netty.channel.ChannelHandler;
import io.netty.handler.ssl.SslContext;

public class SmtpMsaServerHandler extends SmtpServerHandler
{
    public SmtpMsaServerHandler(SmtpData smtpData)
    {
        super(smtpData);
    }
}
