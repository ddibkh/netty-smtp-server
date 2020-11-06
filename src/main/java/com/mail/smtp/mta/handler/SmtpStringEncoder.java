package com.mail.smtp.mta.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class SmtpStringEncoder extends StringEncoder
{
    public SmtpStringEncoder()
    {
        super();
    }

    public SmtpStringEncoder(Charset charset)
    {
        super(charset);
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception
    {
        String response = String.valueOf(msg);
        if( response.endsWith("\r\n") )
            response = response.substring(0, response.length() - 2);
        log.info("<<< {}", response);
        return super.acceptOutboundMessage(msg);
    }
}
