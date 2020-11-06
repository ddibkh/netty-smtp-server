package com.mail.smtp.mta.protocol;

import com.mail.smtp.mta.SmtpData;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Auth
{
    public void process(ChannelHandlerContext ctx, SmtpData smtpData)
    {
        return;
    }
}
