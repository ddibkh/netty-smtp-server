package com.mail.smtp;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.mta.starter.SmtpMSAServer;
import com.mail.smtp.mta.starter.SmtpSSLServer;
import com.mail.smtp.mta.starter.SmtpServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class SmtpApplication implements CommandLineRunner, ApplicationListener< ContextClosedEvent >
{
	private final SmtpConfig smtpConfig;
	private final SmtpSSLServer smtpSSLServer;
	private final SmtpServer smtpServer;
	private final SmtpMSAServer smtpMSAServer;

	public static void main(String[] args) {
		SpringApplication.run(SmtpApplication.class, args);
	}

	@Override
	public void run(String... args)
	{
		Thread t1 = new Thread(() -> {
			try
			{
				smtpServer.start();
			}
			catch( GeneralSecurityException | IOException e )
			{
				log.error("fail to start server, {}", e.getMessage());
			}

		});
		t1.setDaemon(true);
		t1.start();

		if( smtpConfig.getInt("smtp.use.ssl", 0).equals(1) )
		{
			Thread t2 = new Thread(() -> {
				try
				{
					smtpSSLServer.start();
				}
				catch( GeneralSecurityException | IOException e )
				{
					log.error("fail to start ssl port server, {}", e.getMessage());
				}
			});
			t2.setDaemon(true);
			t2.start();
		}

		if( smtpConfig.getInt("smtp.use.msa", 0).equals(1) )
		{
			Thread t3 = new Thread(() -> {
				try
				{
					smtpMSAServer.start();
				}
				catch( GeneralSecurityException | IOException e )
				{
					log.error("fail to start submission port server, {}", e.getMessage());
				}
			});
			t3.setDaemon(true);
			t3.start();
		}
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent contextClosedEvent)
	{
		log.trace("onApplication closed event");
	}
}
