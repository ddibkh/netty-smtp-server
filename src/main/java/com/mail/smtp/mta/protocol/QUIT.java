package com.mail.smtp.mta.protocol;

import com.mail.smtp.data.ResponseData;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.util.CommonUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class QUIT
{
    public void process(ChannelHandlerContext ctx, SmtpData smtpData)
    {
        //ChannelFuture future = ctx.write("221 " + CommonUtil.getHostName() + " closing connection\r\n");
        ChannelFuture future = ctx.write(new ResponseData(smtpData.getRandomUID(),
                "221 " + CommonUtil.getHostName() + " closing connection\r\n"));
        future.addListener(ChannelFutureListener.CLOSE);
    }
}
