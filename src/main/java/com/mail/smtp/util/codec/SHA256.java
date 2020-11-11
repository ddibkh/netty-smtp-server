package com.mail.smtp.util.codec;

import java.security.MessageDigest;

public class SHA256
{
	public static String encode(String plainedData)
	{
		StringBuilder sb = new StringBuilder();

		try
		{
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			sha.update(plainedData.getBytes());

			byte[] shaBytes = sha.digest();

			for( byte shaByte : shaBytes )
			{
				sb.append(Integer.toString(( shaByte & 0xff ) + 0x100, 16).substring(1));
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return sb.toString();
	}
}
