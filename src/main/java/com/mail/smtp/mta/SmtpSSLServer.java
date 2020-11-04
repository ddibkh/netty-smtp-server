/*
 * Basic SMTP server in Java using Netty
 *
 * Author: Maarten Oelering
 * Modifier : kkwang
 */

package com.mail.smtp.mta;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.mta.ssl.SSLServerInitializer;
import com.mail.smtp.mta.ssl.SmtpSSLContext;
import com.mail.smtp.util.CommonUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RequiredArgsConstructor
@Slf4j
@Service
public class SmtpSSLServer
{
	private final SmtpSSLContext smtpSSLContext;
	private final SmtpConfig smtpConfig;

	public void start(String[] args) throws GeneralSecurityException, IOException
	{
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SmtpInitialize.class);
		EventLoopGroup boss = ctx.getBean("bossGroup", EventLoopGroup.class);
		EventLoopGroup work = ctx.getBean("workGroup", EventLoopGroup.class);
		SslContext sslContext = smtpSSLContext.sslContext();

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, work)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_REUSEADDR, true)
				//.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new SSLServerInitializer(sslContext));

		try
		{
			int port = smtpConfig.getInt("smtp.ssl.port", 465);
			ChannelFuture cf = bootstrap.bind(CommonUtil.getLocalIP(), port).sync();
			cf.channel().closeFuture().sync();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		ctx.close();

		log.info("end of ssl smtp server");
	}
}
