/*
 * Basic SMTP server in Java using Netty
 *
 * Author: Maarten Oelering
 * Modifier : kkwang
 */

package com.mail.smtp.mta.starter;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.mta.initializer.MtaMsaInitializer;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RequiredArgsConstructor
@Slf4j
@Service
public class SmtpMSAServer
{
	private final SmtpSSLContext smtpSSLContext;
	private final SmtpConfig smtpConfig;

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
				.childHandler(new MtaMsaInitializer(sslContext));

		try
		{
			int port = smtpConfig.getInt("smtp.msa.port", 587);
			ChannelFuture cf = bootstrap.bind(CommonUtil.getLocalIP(), port).sync();
			cf.channel().closeFuture().sync();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			boss.shutdownGracefully();
			work.shutdownGracefully();
		}

		log.info("end of smtp msa server");
	}
}
