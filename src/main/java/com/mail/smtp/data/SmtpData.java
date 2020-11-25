package com.mail.smtp.data;

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
	private UserVO mailfrom;
	@Setter @Getter
	private List<UserVO> listRcptTo = new ArrayList<>();
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

	@Setter @Getter
	private String helo;

	@Setter @Getter
	private boolean blankSender;

	public SmtpData(SslContext sslContext)
	{
		this.sslContext = sslContext;
		init();
	}

	public void init()
	{
		mailfrom = null;
		listRcptTo.clear();
		msg.setLength(0);
		subject = "";
		completeData = false;
		randomUID = CommonUtil.makeUID();
		blankSender = false;
		helo = "";
	}

	public void addReceipent(UserVO to)
	{
		listRcptTo.add(to);
	}
	
	public String getReceipents()
	{
		Optional<String> receipents =
				Optional.of(listRcptTo.stream().
						map(UserVO::getAddress).collect(Collectors.joining(",")));
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
