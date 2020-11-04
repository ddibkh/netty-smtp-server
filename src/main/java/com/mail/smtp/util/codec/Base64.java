package com.mail.smtp.util.codec;

public class Base64
{
	private static final int LINE_WIDTH = 72;

	private static final byte[] base64_table = {
		 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, /* 000 ~ 009 */
		 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, /* 010 ~ 019 */
		 85, 86, 87, 88, 89, 90, 97, 98, 99,100, /* 020 ~ 029 */
		101,102,103,104,105,106,107,108,109,110, /* 030 ~ 039 */
		111,112,113,114,115,116,117,118,119,120, /* 040 ~ 049 */
		121,122, 48, 49, 50, 51, 52, 53, 54, 55, /* 050 ~ 059 */
		 56, 57, 43, 47                          /* 060 ~ 063 */
	};

	private static final byte[] base64_dec_table = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 000 ~ 009 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 010 ~ 019 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 020 ~ 029 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 030 ~ 039 */
		-1, -1, -1, 62, -1, -1, -1, 63, 52, 53, /* 040 ~ 049 */
		54, 55, 56, 57, 58, 59, 60, 61, -1, -1, /* 050 ~ 059 */
		-1, -1, -1, -1, -1,  0,  1,  2,  3,  4, /* 060 ~ 069 */
		 5,  6,  7,  8,  9, 10, 11, 12, 13, 14, /* 070 ~ 079 */
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24, /* 080 ~ 089 */
		25, -1, -1, -1, -1, -1, -1, 26, 27, 28, /* 090 ~ 099 */
		29, 30, 31, 32, 33, 34, 35, 36, 37, 38, /* 100 ~ 109 */
		39, 40, 41, 42, 43, 44, 45, 46, 47, 48, /* 110 ~ 119 */
		49, 50, 51, -1, -1, -1, -1, -1, -1, -1, /* 120 ~ 129 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 130 ~ 139 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 140 ~ 149 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 150 ~ 159 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 160 ~ 169 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 170 ~ 179 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 180 ~ 189 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 190 ~ 199 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 200 ~ 209 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 210 ~ 219 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 220 ~ 229 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 230 ~ 239 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 240 ~ 249 */
		-1, -1, -1, -1, -1                      /* 250 ~ 254 */
	};

	public static String encode(byte[] data, boolean isNewLine)
	{
		int length = data.length;
		int size = (length + 3 - length % 3) * 4 / 3 + 1;

		int lineWidth = (LINE_WIDTH / 4 * 3);
		byte[] btResult = new byte[size + (size / lineWidth + 1)];

		int index = 0;
		int step = 0;
		size = 0;

		while( length > 2 )
		{
			btResult[index++] = base64_table[((int)(data[step] & 0xFF) >> 2)];
			btResult[index++] = base64_table[(((int)(data[step] & 0xFF) & 0x003) << 4) + ((int)(data[step + 1] & 0xFF) >> 4)];
			btResult[index++] = base64_table[(((int)(data[step + 1] & 0xFF) & 0x00F) << 2) + ((int)(data[step + 2] & 0xFF) >> 6)];
			btResult[index++] = base64_table[(int)(data[step + 2] & 0xFF) & 0x03F];

			size += 3;
			if( isNewLine )
			{
				if( size % lineWidth == 0 )
				{
					btResult[index++] = '\n';
				}
			}

			step += 3;
			length -= 3;
		}

		if( length != 0 )
		{
			btResult[index++] = base64_table[(int)(data[step] & 0xFF) >> 2];

			if( length > 1 )
			{
				btResult[index++] = base64_table[(((int)(data[step] & 0xFF) & 0x003) << 4) + ((int)(data[step + 1] & 0xFF) >> 4)];
				btResult[index++] = base64_table[((int)(data[step + 1] & 0xFF) & 0x00F) << 2];
				btResult[index++] = '=';
			}
			else
			{
				btResult[index++] = base64_table[((int)(data[step] & 0xFF) & 0x003) << 4];
				btResult[index++] = '=';
				btResult[index++] = '=';
			}
		}

		String encodedData = new String(btResult);

		return encodedData.trim();
	}

	public static byte[] decode(String encodedData)
	{
		byte[] data = encodedData.getBytes();
		int length = data.length;
		byte[] btTemp = new byte[length];

		int pos = -1;
		int step = 0;
		int index = 0;

		byte bTemp = 0;

		for( int i = 0; i < length; i++ )
		{
			bTemp = (byte)data[i];

			if( bTemp == '=' )
			{
				break;
			}

			if( bTemp == ' ' )
			{
				continue;
			}

			pos = base64_dec_table[bTemp];
/*
			pos = -1;
			for( int j = 0; j < base64_table.length; j++ )
			{
				if( bTemp == base64_table[j] )
				{
					pos = j;
					break;
				}
			}
*/
			if( pos == -1 )
			{
				continue;
			}

			switch( step % 4 )
			{
			case 0:
				btTemp[index] = (byte)(pos << 2);
				break;
			case 1:
				btTemp[index++] |= (byte)(pos >> 4);
				btTemp[index] = (byte)((pos & 0x00F) << 4);
				break;
			case 2:
				btTemp[index++] |= (byte)(pos >> 2);
				btTemp[index] = (byte)((pos & 0x003) << 6);
				break;
			case 3:
				btTemp[index++] |= (byte)pos;
				break;
			}

			step++;
		}

		if( (bTemp != '=') && (bTemp = (byte)((step - 1) % 4)) != 3 )
		{
			index -= bTemp;
			btTemp[index] = 0;
		}

		byte[] btReturn = new byte[index];

		System.arraycopy(btTemp, 0, btReturn, 0, index);
/*
		for( int i = 0; i < index; i++ )
		{
			btReturn[i] = btTemp[i];
		}
*/
		return btReturn;
	}
}

