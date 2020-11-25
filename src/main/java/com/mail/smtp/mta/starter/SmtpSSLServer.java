/*
 * Basic SMTP server in Java using Netty
 */

package com.mail.smtp.mta.starter;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.mta.initializer.MtaSSLInitializer;
import com.mail.smtp.mta.ssl.SmtpSSLContext;
import com.mail.smtp.util.CommonUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class SmtpSSLServer
{
	private final SmtpSSLContext smtpSSLContext;
	private final SmtpConfig smtpConfig;
	private ChannelFuture closeFuture;

	public void start() throws GeneralSecurityException, IOException
	{
		NioEventLoopGroup boss = new NioEventLoopGroup();
		NioEventLoopGroup work = new NioEventLoopGroup();
		SslContext sslContext = smtpSSLContext.sslContext(false);

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, work)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_REUSEADDR, true)
				.childHandler(new MtaSSLInitializer(sslContext));

		try
		{
			int port = smtpConfig.getInt("smtp.ssl.port", 465);
			log.info("start smtp ssl ... {}", CommonUtil.getLocalIP());
			closeFuture = bootstrap.bind(port).sync().channel().closeFuture();
			closeFuture.sync();
		}
		catch(Exception e)
		{
			log.error("smtp ssl server, exception occured, {}", e.getMessage());
			if( log.isTraceEnabled() )
				e.printStackTrace();
		}
		finally
		{
			log.trace("call smtp ssl server finally shutdown gracefully");
			boss.shutdownGracefully();
			work.shutdownGracefully();
		}

		log.info("end of ssl smtp server");
	}

	@PreDestroy
	public void preDestroy()
	{
		log.trace("smtp ssl server destroy");
		Optional.ofNullable(closeFuture).ifPresent(channelFuture -> channelFuture.channel().close());
	}
}
