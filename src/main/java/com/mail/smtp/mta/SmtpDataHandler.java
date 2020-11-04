/*
 * Basic SMTP server in Java using Netty
 *
 * Author: kkwang
 */

package com.mail.smtp.mta;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SmtpDataHandler extends ChannelInboundHandlerAdapter
{
	private final SmtpServerHandler baseHandler;

	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
		log.debug("smtp data handler active!! [" + toString() + "]");
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx)
	{
    	log.info("handlerAdded [" + toString() + "]");
    	//================================= DEBUG ================================= 
    	log.info("clientip : " + baseHandler.getSmtpData().getClientIP());
    	log.info("client port : " + baseHandler.getSmtpData().getClientPort());
    	log.info("mail from : " + baseHandler.getSmtpData().getMailfrom());
    	log.info("rcpt to: " + baseHandler.getSmtpData().getReceipents());
    	//================================= DEBUG =================================
    }
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
	{
    	log.info("handlerRemoved [" + toString() + "]");
    }
    
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		//logger.info("channelread for eml data receive [" + toString() + "]");
		String line = (String)msg;
		line += "\r\n";
		//logger.info("data line : " + line);
		if ( line.trim().equals(".") ) {
            ctx.writeAndFlush("250 OK\r\n");
			baseHandler.getSmtpData().setbCompleteData(true);
            log.info("received data completed");
            ChannelPipeline cp = ctx.pipeline();
            cp.replace("datahandler", "basehandler", baseHandler);
        }
        else {
            // unescape leading dot
            if ( line.startsWith("..") ) {
            	line = line.substring(1);
            }

			baseHandler.getSmtpData().addMessage(line);
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
    	cause.printStackTrace();
        ctx.close();
    }
}
