package com.mail.smtp.mta.protocol;

import com.mail.smtp.mta.SmtpData;
import com.mail.smtp.mta.handler.SmtpDataHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Data
{
    public void process(ChannelHandlerContext ctx, SmtpData smtpData)
    {
        String msg = "354 End data with <CR><LF>.<CR><LF>";
        ctx.write(msg + "\r\n");
        ChannelPipeline cp = ctx.pipeline();
        cp.replace("basehandler", "datahandler", new SmtpDataHandler(smtpData, ctx.handler()));
    }
}
