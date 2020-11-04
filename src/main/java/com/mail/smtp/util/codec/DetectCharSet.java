package com.mail.smtp.util.codec;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class DetectCharSet
{
	public static String LocalString(String val)
	{
		if( val == null )
		{
			return null;
		}
		else
		{
			byte[] b;
			
			try
			{
				b = val.getBytes("8859_1");
				
				CharsetDecoder convert = Charset.forName("UTF-8").newDecoder();
				
				try
				{
					CharBuffer r = convert.decode(ByteBuffer.wrap(b));
					return r.toString();
				}
				catch( CharacterCodingException e )
				{
					return new String(b, "EUC-KR");
				}
			}
			catch( UnsupportedEncodingException ue )
			{
				ue.printStackTrace();
			}
		}
		
		return null;
	}
}
