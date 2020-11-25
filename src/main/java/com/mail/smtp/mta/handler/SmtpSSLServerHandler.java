package com.mail.smtp.mta.handler;
/*
 * Basic SMTP server in Java using Netty
 *
 * Author: Maarten Oelering
 * Modifier: kkwang
 */

import com.mail.smtp.data.SmtpData;
import com.mail.smtp.util.CommonUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.net.InetSocketAddress;

@Slf4j
public class SmtpSSLServerHandler extends SmtpServerHandler
{
	public SmtpSSLServerHandler(SmtpData sd)
	{
        super(sd);
	}
	
	public SmtpData getSmtpData()
	{
		return super.getSmtpData();
	}

    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener< Future< ? super Channel > >()
                {
                    @Override
                    public void operationComplete(Future< ? super Channel > future) throws Exception
                    {
                        InetSocketAddress sa = ((InetSocketAddress) ctx.channel().remoteAddress());
                        String clientip = sa.getAddress().getHostAddress();
                        int port = sa.getPort();

                        MDC.put("IP", clientip);
                        MDC.put("UID", smtpData.getRandomUID());

                        getSmtpData().setClientIP(clientip);
                        getSmtpData().setClientPort(port);
                        log.info("ssl connected " + clientip + ":" + port);
                        ctx.writeAndFlush("220 " + CommonUtil.getHostName() + " ESMTP\r\n");
                        smtpData.setSecureConnected(true);
                    }
                }
        );
    }
}

