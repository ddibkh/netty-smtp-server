/*
 * Basic SMTP server in Java using Netty
 */

package com.mail.smtp.mta.starter;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.mta.initializer.MtaInitializer;
import com.mail.smtp.mta.ssl.SmtpSSLContext;
import com.mail.smtp.util.CommonUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.concurrent.CancellationException;

@RequiredArgsConstructor
@Slf4j
@Service
public class SmtpServer
{
	private final SmtpSSLContext smtpSSLContext;
	private final SmtpConfig smtpConfig;

	private ChannelFuture closeFuture;

	public void start() throws GeneralSecurityException, IOException
	{
		NioEventLoopGroup boss = new NioEventLoopGroup();
		NioEventLoopGroup work = new NioEventLoopGroup();
		//set starttls true
		SslContext sslContext = smtpSSLContext.sslContext(true);

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, work)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_REUSEADDR, true)
				.childHandler(new MtaInitializer(sslContext));

		try
		{
			int port = smtpConfig.getInt("smtp.port", 25);
			log.info("start smtp ... {}", CommonUtil.getLocalIP());
			closeFuture = bootstrap.bind(port).sync().channel().closeFuture();
			closeFuture.sync();
		}
		catch(InterruptedException e)
		{
			log.info("smtp server interrupted, {}", e.getMessage());
			if( log.isTraceEnabled() )
				e.printStackTrace();
		}
		catch( CancellationException ce )
		{
			log.info("smtp server cancelation exception, {}", ce.getMessage());
			if( log.isTraceEnabled() )
				ce.printStackTrace();
		}
		finally
		{
			log.trace("call smtp server finally shutdown gracefully");
			boss.shutdownGracefully();
			work.shutdownGracefully();
		}

		log.info("end of smtp server");
	}

	@PreDestroy
	public void preDestroy()
	{
		log.trace("smtp server destroy");
		Optional.ofNullable(closeFuture).ifPresent(
				channelFuture ->
				{
					try
					{
						channelFuture.channel().close().sync();
					}
					catch( InterruptedException e )
					{
						log.error("smtp server end process, close interrupt exception");
						if( log.isTraceEnabled() )
							e.printStackTrace();
					}
				}
		);
	}
}
