package com.mail.smtp.mta;

import com.mail.smtp.util.CommonUtil;
import io.netty.handler.ssl.SslContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class SmtpData
{
	@Setter @Getter
	private String clientIP;
	@Setter @Getter
	private int clientPort;
	@Setter @Getter
	private String mailfrom;
	@Setter @Getter
	private List<String> listRcptTo = new ArrayList<>();
	@Setter @Getter
	private StringBuffer msg = new StringBuffer();
	@Setter @Getter
	private String subject;

	@Setter @Getter
	private boolean secureConnected = false;
	@Setter @Getter
	private boolean completeData = false;
	@Setter @Getter
	private boolean authed = false;
	@Getter
	private final SslContext sslContext;

	@Getter
	private String randomUID;

	public SmtpData(SslContext sslContext)
	{
		this.sslContext = sslContext;
		init();
	}

	public void init()
	{
		mailfrom = "";
		listRcptTo.clear();
		msg.setLength(0);
		subject = "";
		completeData = false;
		randomUID = CommonUtil.makeUID();
	}

	public void addReceipent(String strTo)
	{
		listRcptTo.add(strTo);
	}
	
	public String getReceipents()
	{
		Optional<String> receipents =
				Optional.of(listRcptTo.stream().
						map(String::valueOf).collect(Collectors.joining(",")));
		return receipents.orElse("");
	}
	
	public void addMessage(String strMessage)
	{
		msg.append(strMessage);
	}
	
	public String getMessage()
	{
		return msg.toString();
	}
}
