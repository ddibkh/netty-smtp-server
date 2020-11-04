package com.mail.smtp;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.mta.SmtpSSLServer;
import com.mail.smtp.mta.SmtpServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class SmtpApplication implements CommandLineRunner
{
	private final SmtpConfig smtpConfig;
	private final SmtpSSLServer smtpSSLServer;
	private final SmtpServer smtpServer;

	public static void main(String[] args) {
		SpringApplication.run(SmtpApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception
	{
		new Thread(() -> {
			smtpServer.start(args);
		}).start();

		if( smtpConfig.getInt("smtp.use_ssl", 0).equals(1) )
		{
			new Thread(() -> {
				try
				{
					smtpSSLServer.start(args);
				}
				catch( GeneralSecurityException e )
				{
					log.error("fail to start ssl server, {}", e.getMessage());
				}
				catch( IOException e )
				{
					log.error("fail to start ssl server, {}", e.getMessage());
				}
			}).start();
		}
	}
}
