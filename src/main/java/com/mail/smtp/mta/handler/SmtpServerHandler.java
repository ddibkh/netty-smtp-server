package com.mail.smtp.mta.handler;
/*
 * Basic SMTP server in Java using Netty
 *
 * Author: Maarten Oelering
 * Modifier: kkwang
 */

import com.mail.smtp.data.ResponseData;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.mta.ApplicationContextProvider;
import com.mail.smtp.mta.protocol.ProtocolService;
import com.mail.smtp.util.CommonUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
@ChannelHandler.Sharable
public class SmtpServerHandler extends ChannelDuplexHandler
{
    protected SmtpData smtpData;

	public SmtpServerHandler(SmtpData sd)
	{
		this.smtpData = sd;
	}
	
	public SmtpData getSmtpData()
	{
		return smtpData;
	}

    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        InetSocketAddress sa = ((InetSocketAddress) ctx.channel().remoteAddress());
        String clientip = sa.getAddress().getHostAddress();

        int port = sa.getPort();
        smtpData.setClientIP(clientip);
        smtpData.setClientPort(port);

        log.info("[{}] connected {}:{}", smtpData.getRandomUID(), clientip, port);
        ctx.writeAndFlush(new ResponseData(smtpData.getRandomUID(),
                "220 " + CommonUtil.getHostName() + " ESMTP\r\n"));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        String line = (String)msg;
        log.info("[{}] >>> {}", smtpData.getRandomUID(), line);
        ProtocolService protocol = ApplicationContextProvider.getBean(ProtocolService.class);
        protocol.process(ctx, line, smtpData);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx)
    {
    	ctx.close();
    	log.info("[{}] closed {}:{}", smtpData.getRandomUID(), smtpData.getClientIP() ,smtpData.getClientPort());
        //MDC.clear();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
    {
        log.trace("[{}] handlerAdded [{}", smtpData.getRandomUID(), toString());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
    {
        log.trace("[{}] handlerRemoved [{}]", smtpData.getRandomUID(), toString());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
    {
        log.trace("[{}] channelRegistered [{}]", smtpData.getRandomUID(), toString());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)
    {
        log.trace("[{}] channelUnRegistered [{}]", smtpData.getRandomUID(), toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
    {
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
    {
        log.trace("[{}] userEventTriggered [{}]", smtpData.getRandomUID(), toString());
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx)
    {
        log.trace("[{}] channelWritabilityChanged [{}]", smtpData.getRandomUID(), toString());
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        if( cause instanceof ReadTimeoutException )
        {
            log.error("[{}] exception occured, read timed out", smtpData.getRandomUID());
            ctx.close();
        }
        else if( cause instanceof WriteTimeoutException )
        {
            log.error("[{}] exceptions occured, write timed out", smtpData.getRandomUID());
            ctx.close();
        }
        else if( cause instanceof SmtpException )
        {
            log.error("[{}] exception occured, error code : {}, message : {}", smtpData.getRandomUID(),
                    ( (SmtpException) cause ).getErrorCode(), cause.getMessage());
            //ctx.writeAndFlush(( (SmtpException) cause ).getResponse());
            ctx.writeAndFlush(new ResponseData(smtpData.getRandomUID(), ( (SmtpException) cause ).getResponse()));
        }
        else
        {
            log.error("[{}] exception occured, {}", smtpData.getRandomUID(), cause.getMessage());
            ctx.close();
        }

    	if( log.isTraceEnabled() )
    	    cause.printStackTrace();

        //MDC.clear();
    }
}

