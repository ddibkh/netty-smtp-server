package com.mail.smtp.mta.protocol;

import com.mail.smtp.data.ResponseData;
import com.mail.smtp.data.SmtpData;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class STARTTLS
{
    public void process(ChannelHandlerContext ctx, SmtpData smtpData)
    {
        /*
            1. create a new SslHandler instance with startTls flag set to true,
            2. insert the SslHandler to the ChannelPipeline, and
            3. write a StartTLS response.
         */
        ctx.pipeline().addFirst(smtpData.getSslContext().newHandler(ctx.channel().alloc()));
        //ctx.write("220 Ready to start TLS\r\n");
        ctx.write(new ResponseData(smtpData.getRandomUID(), "220 Ready to start TLS\r\n"));
        smtpData.setSecureConnected(true);
        log.info("[{}] success convert secure connection", smtpData.getRandomUID());
    }
}
