package com.mail.smtp.util.codec;

public class Cipher
{
	public static String encode(String plainedData, String encKey)
	{
		if( plainedData == null || plainedData.equals("") )
		{
			return "";
		}

		if( encKey == null || encKey.equals("") )
		{
			return plainedData;
		}

		byte[] btPlained = plainedData.getBytes();
		int length = btPlained.length;

		byte[] btTemp = new byte[(length / 2 * 3) + (length % 2)];
		int index = 0;
		byte bTemp;

		for( int i = 0; i < length / 2; i++ )
		{
			bTemp = (byte)(btPlained[2 * i] + btPlained[2 * i + 1]);

			btTemp[index++] = (byte)(btPlained[2 * i] ^ bTemp);
			btTemp[index++] = (byte)(btPlained[2 * i + 1] ^ bTemp);
			btTemp[index++] = (byte)bTemp;
		}

		if( length % 2 == 1 )
		{
			btTemp[index++] = (byte)btPlained[length - 1];
		}

		byte[] btKey = encKey.getBytes();
		int keyLength = btKey.length;

		length = btTemp.length;

		for( int i = 0; i < length; i++ )
		{
			btTemp[i] ^= btKey[i % keyLength];
		}

		return Base64.encode(btTemp, false);
	}

	public static String decode(String encodedData, String encKey)
	{
		if( encodedData == null || encodedData.equals("") )
		{
			return "";
		}

		if( encKey == null || encKey.equals("") )
		{
			return encodedData;
		}

		byte[] btCiphered = Base64.decode(encodedData);
		int length = btCiphered.length;

		byte[] btKey = encKey.getBytes();
		int keyLength = btKey.length;

		for( int i = 0; i < length; i++ )
		{
/*
			if( btCiphered[i] == 0 )
			{
				continue;
			}
*/		
			btCiphered[i] ^= btKey[i % keyLength];
		}

		byte[] btTemp = new byte[length];
		int index = 0;
		byte bTemp;

		for( int i = 0; i < length / 3; i++ )
		{
			bTemp = btCiphered[3 * i + 2];
			
			btTemp[index++] = (byte)(btCiphered[3 * i] ^ bTemp);
			btTemp[index++] = (byte)(btCiphered[3 * i + 1] ^ bTemp);
		}

		if( length % 3 == 1 )
		{
			btTemp[index++] = (byte)btCiphered[length - 1];
		}
		else if( length % 3 == 2 )
		{
			btTemp[index++] = (byte)btCiphered[length - 2];
			btTemp[index++] = (byte)btCiphered[length - 1];
		}

		String plainedData = "";
		for( int i = 0; i < length; i++ )
		{
			if( btTemp[i] == 0 )
			{
				byte[] btPlained = new byte[i];
				for( int j = 0; j < i; j++ )
				{
					btPlained[j] = btTemp[j];
				}

				plainedData = new String(btPlained);
				break;
			}
		}

		return plainedData;
	}
}
