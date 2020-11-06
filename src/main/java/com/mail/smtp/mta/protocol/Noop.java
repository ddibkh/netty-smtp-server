package com.mail.smtp.mta.protocol;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Noop
{
    public void process(ChannelHandlerContext ctx)
    {
        ctx.write("250 OK\r\n");
    }
}
