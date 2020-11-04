package com.mail.smtp.util.codec;

import java.security.MessageDigest;

public class SHA256
{
	public static String encode(String plainedData)
	{
		StringBuffer sb = new StringBuffer();

		try
		{
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			sha.update(plainedData.getBytes());

			byte[] shaBytes = sha.digest();
			int length = shaBytes.length;

			for( int i = 0; i < length; i++ )
			{
				sb.append(Integer.toString((shaBytes[i] & 0xff) + 0x100, 16).substring(1));
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return sb.toString();
	}
}
