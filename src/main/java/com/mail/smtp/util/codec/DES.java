package com.mail.smtp.util.codec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DES
{
	public static byte[] encode(String plainedData, String encKey)
	{
		byte[] ciphertext = null;

		try
		{
			DESKeySpec keySpec;
			SecretKeyFactory keyFac;
			SecretKey secretKey;

			keySpec = new DESKeySpec(encKey.getBytes());
			keyFac = SecretKeyFactory.getInstance("DES");
			secretKey = keyFac.generateSecret(keySpec);

			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			ciphertext = cipher.doFinal(plainedData.getBytes());
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return ciphertext;
	}
	
	public static byte[] decode(byte[] encodedData, String encKey)
	{
		byte[] descrypt = null;

		try
		{
			DESKeySpec keySpec;
			SecretKeyFactory keyFac;
			SecretKey secretKey;

			keySpec = new DESKeySpec(encKey.getBytes());
			keyFac = SecretKeyFactory.getInstance("DES");
			secretKey = keyFac.generateSecret(keySpec);

			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			descrypt = cipher.doFinal(encodedData);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return descrypt;
	}

	public static byte[] decode(String encodedData, String encKey)
	{
		byte[] descrypt = null;

		try
		{
			DESKeySpec keySpec;
			SecretKeyFactory keyFac;
			SecretKey secretKey;

			keySpec = new DESKeySpec(encKey.getBytes());
			keyFac = SecretKeyFactory.getInstance("DES");
			secretKey = keyFac.generateSecret(keySpec);

			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			descrypt = cipher.doFinal(encodedData.getBytes());
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return descrypt;
	}
}
