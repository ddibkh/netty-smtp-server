package com.mail.smtp.util.codec;

public class Base64ForPEM
{
	private static String s_data_bin2ascii="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	public static byte[] encodeBase64 ( byte[] binaryData ) {

		int binaryDataLen = binaryData.length;
		int base64DataLen = ( (binaryDataLen - 1 ) / 3 + 1 ) * 4;
		byte[] base64Data = new byte[base64DataLen];
		int l,i;
		int bin_index = 0; 
		int base_index = 0; 
		byte[] data_bin2ascii=s_data_bin2ascii.getBytes();

		for ( i= binaryDataLen; i>0; i-=3 ) {
			if ( i>=3 ) {
				l = ((0x000000FF & binaryData[bin_index]) << 16) | ((0x000000FF & binaryData[bin_index+1]) << 8) | (0x000000FF & binaryData[bin_index+2]) ;
				base64Data[base_index++] = data_bin2ascii[(l>>>18)&0x3f]; 
				base64Data[base_index++] = data_bin2ascii[(l>>>12)&0x3f] ;
				base64Data[base_index++] = data_bin2ascii[(l>>>6)&0x3f] ;
				base64Data[base_index++] = data_bin2ascii[(l)&0x3f] ;
			}
			else {
				l = (0x000000FF & binaryData[bin_index]) << 16;
				if ( i==2) l |= (0x000000FF & binaryData[bin_index+1]) << 8;
				base64Data[base_index++] = data_bin2ascii[(l>>>18)&0x3f] ;
				base64Data[base_index++] = data_bin2ascii[(l>>>12)&0x3f] ;
				if ( i==1 ) 
					base64Data[base_index++] = (byte) '=';
				else
					base64Data[base_index++] = data_bin2ascii[(l>>>6)&0x3f] ;
				base64Data[base_index++] = (byte) '=';
			}
			bin_index += 3;
		}
		return base64Data; 
	}

	public static String encodePEM ( String header , byte[] binaryData )
	{
		StringBuffer pemBuffer;
		String base64Buffer; 
		byte[] base64Byte;
		int	startIndex = 0;
		int	i;

		pemBuffer = new StringBuffer();
		pemBuffer.append ( "-----BEGIN " );
		pemBuffer.append ( header );
		pemBuffer.append ( "-----\n" );

		base64Byte = Base64ForPEM.encodeBase64( binaryData );
		if ( base64Byte == null ) return null;
		base64Buffer = new String ( base64Byte ); 
		for ( i=0;i< base64Buffer.length() / 64 ; i++ ) {
			pemBuffer.append ( base64Buffer.substring( startIndex, startIndex + 64 ) );
			pemBuffer.append ( "\n" );
			startIndex = startIndex + 64;
		}
		if ( base64Buffer.length() > startIndex ) { 
			pemBuffer.append ( base64Buffer.substring( startIndex, base64Buffer.length() ) );
			pemBuffer.append ( "\n" );
		}
		pemBuffer.append ( "-----END " );
		pemBuffer.append ( header );
		pemBuffer.append ( "-----\n" );

		return pemBuffer.toString();
	}
}
