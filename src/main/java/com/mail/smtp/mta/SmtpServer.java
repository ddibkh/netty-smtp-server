/*
 * Basic SMTP server in Java using Netty
 *
 * Author: Maarten Oelering
 * Modifier : kkwang
 */

package com.mail.smtp.mta;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.util.CommonUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@RequiredArgsConstructor
@Slf4j
@Service
public class SmtpServer
{
	private final SmtpConfig smtpConfig;

	public void start(String[] args)
	{
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SmtpInitialize.class);
		ServerBootstrap sbt = ctx.getBean("bootstrap", ServerBootstrap.class);

		try
		{
			int port = smtpConfig.getInt("smtp.port", 25);
			ChannelFuture cf = sbt.bind(CommonUtil.getLocalIP(), port).sync();
			cf.channel().closeFuture().sync();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		ctx.close();

		log.info("end of smtp server");
	}
}
