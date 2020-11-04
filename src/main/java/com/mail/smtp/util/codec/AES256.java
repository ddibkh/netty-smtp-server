package com.mail.smtp.util.codec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;

public class AES256 {
		
	public static byte[] encode(String str, String key, byte[] IV)
	throws Exception
	{
		byte[] textBytes = str.getBytes("UTF-8");
		
		AlgorithmParameterSpec ivSpec = new IvParameterSpec(IV);
		
		SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		Cipher cipher = null;
		
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);

		return cipher.doFinal(textBytes);
	}

	public static String decode(byte[] str, String key, byte[] IV)
	throws Exception
	{	
		AlgorithmParameterSpec ivSpec = new IvParameterSpec(IV);

		SecretKeySpec newKey = new SecretKeySpec(key.getBytes(), "AES");
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
		
		return new String(cipher.doFinal(str));
	}
}