/*
 * Basic SMTP server in Java using Netty
 *
 * Author: kkwang
 */

package com.mail.smtp.mta;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Slf4j
@Component
public class SmtpListenerHandler extends ChannelOutboundHandlerAdapter {

	@Override
	public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise)
	{
		if( localAddress instanceof InetSocketAddress )
		{
			InetAddress ia = ((InetSocketAddress) localAddress).getAddress();
			String hostname = ((InetSocketAddress) localAddress).getHostName();
			log.info("[bind] " + ia.getHostAddress() + " (" + hostname + ")" + " [" + ((InetSocketAddress)localAddress).getPort() + "]");
		}
		else
			log.info("[bind] " + localAddress.toString());
		
		ctx.bind(localAddress, promise);
	}
	
	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise)
	{
		InetSocketAddress sa = ((InetSocketAddress) ctx.channel().remoteAddress());
    	String clientip = sa.getAddress().getHostAddress();
    	int port = sa.getPort();
    	log.info("closing " + clientip + ":" + port);
	}
	
	@Override
	public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise)
	{
		InetSocketAddress sa = ((InetSocketAddress) ctx.channel().remoteAddress());
    	String clientip = sa.getAddress().getHostAddress();
    	int port = sa.getPort();
    	log.info("disconnected " + clientip + ":" + port);
	}
}

