package com.mail.smtp.mta;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Vector;

@Component
@Scope("prototype")
public class SmtpData
{
	private String clientIP;
	private int clientPort;
	private String mailfrom;
	private Vector<String> vRcptTo = new Vector<String>(10);
	private StringBuffer msg = new StringBuffer();
	private boolean bCompleteData = false;

	private String subject;

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public int getClientPort() {
		return clientPort;
	}

	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}

	public String getMailfrom() {
		return mailfrom;
	}

	public void setMailfrom(String mailfrom) {
		this.mailfrom = mailfrom;
	}

	public Vector<String> getvRcptTo() {
		return vRcptTo;
	}

	public void setvRcptTo(Vector<String> vRcptTo) {
		this.vRcptTo = vRcptTo;
	}

	public StringBuffer getMsg() {
		return msg;
	}

	public void setMsg(StringBuffer msg) {
		this.msg = msg;
	}

	public boolean isbCompleteData() {
		return bCompleteData;
	}

	public void setbCompleteData(boolean bCompleteData) {
		this.bCompleteData = bCompleteData;
	}

	public void addReceipent(String strTo) {
		vRcptTo.add(strTo);
	}
	
	public String getReceipents() {
		if( !vRcptTo.isEmpty() )
		{
			StringBuilder sb = new StringBuilder();
			Iterator<String> iter = vRcptTo.iterator();
			while( iter.hasNext() )
			{
				sb.append(iter.next());
				sb.append(',');
			}
			
			if( sb.charAt(sb.length()-1) == ',' )
				sb.deleteCharAt(sb.length()-1);
			
			return sb.toString();
		}
		
		return "";
	}
	
	public void addMessage(String strMessage) {
		msg.append(strMessage);
	}
	
	public String getMessage() {
		return msg.toString();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
