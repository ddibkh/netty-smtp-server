package com.mail.smtp.util.codec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AES
{
	public static byte[] encode(String key, byte[] enc)
	throws Exception
	{
		Key secureKey = new SecretKeySpec(key.getBytes(), "AES");
		
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secureKey);
		
		return cipher.doFinal(enc);
	}
	
	public static byte[] decode(String key, byte[] enc)
	throws Exception
	{
		Key secureKey = new SecretKeySpec(key.getBytes(), "AES");
		
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secureKey);
		
		return cipher.doFinal(enc);
	}
}
