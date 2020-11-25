package com.mail.smtp.mta.protocol;

import com.mail.smtp.data.SmtpData;
import io.netty.channel.ChannelHandlerContext;

public interface IProtocol
{
    void process(ChannelHandlerContext ctx, String message, SmtpData smtpData);
}
