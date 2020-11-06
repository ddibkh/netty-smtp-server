/*
 * Basic SMTP server in Java using Netty
 *
 * Author: kkwang
 */

package com.mail.smtp.mta.handler;

import com.mail.smtp.mta.SmtpData;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@RequiredArgsConstructor
@Slf4j
public class SmtpDataHandler extends ChannelInboundHandlerAdapter
{
	private final SmtpData smtpData;
	private final ChannelHandler baseHandler;

	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
		log.trace("smtp data handler active!! [" + toString() + "]");
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx)
	{
    	log.trace("handlerAdded [" + toString() + "]");
    	//================================= DEBUG ================================= 
    	log.info("clientip : " + smtpData.getClientIP());
    	log.info("client port : " + smtpData.getClientPort());
    	log.info("mail from : " + smtpData.getMailfrom());
    	log.info("rcpt to: " + smtpData.getReceipents());
    	//================================= DEBUG =================================
    }
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
	{
    	log.trace("handlerRemoved [" + toString() + "]");
    }
    
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		String line = (String)msg;
		line += "\r\n";

		if ( line.trim().equals(".") ) {
			String response = "250 Message accepted for delivery";
            ctx.writeAndFlush(response + "\r\n");
			smtpData.setCompleteData(true);
            ChannelPipeline cp = ctx.pipeline();
            cp.replace("datahandler", "basehandler", baseHandler);
        }
        else {
            // unescape leading dot
            if ( line.startsWith("..") ) {
            	line = line.substring(1);
            }

			smtpData.addMessage(line);
        }
	}

	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
	{
		log.trace("smtpdatahandler channelreadcomplete [" + toString() + "]");
		ctx.flush();
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		log.error("exception occured, {}", cause.getMessage());
		if( log.isTraceEnabled() )
    		cause.printStackTrace();
        ctx.close();
		MDC.clear();
    }
}
