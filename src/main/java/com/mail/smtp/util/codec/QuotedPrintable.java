package com.mail.smtp.util.codec;

public class QuotedPrintable
{
	private static final int LINE_WIDTH = 72;

	public static String encode(byte[] data, boolean isNewLine, boolean isBinary)
	{
		int length = data.length;
		int size = (length * 3 + (length * 3 / LINE_WIDTH + 1) * 3 + 1);

		byte[] btResult = new byte[size];

		int index = 0;
		int lineLength = 0;

		byte bFirstTemp = 0;
		byte bSecondTemp = 0;

		for( int i = 0; i < length; i++ )
		{
			if( (data[i] >= 33 && data[i] <= 60) || (data[i] >= 62 && data[i] <= 126) )
			{
				btResult[index++] = data[i];
				lineLength++;
			}
			else if( data[i] == '=' )
			{
				btResult[index++] = '=';
				btResult[index++] = '3';
				btResult[index++] = 'D';
				lineLength += 3;
			}
			else if( data[i] == '\n' || data[i] == '\r' )
			{
				if( isBinary )
				{
					if( data[i] == '\n' )
					{
						btResult[index++] = '=';
						btResult[index++] = '0';
						btResult[index++] = 'A';
						lineLength += 3;
					}
					else
					{
						btResult[index++] = '=';
						btResult[index++] = '0';
						btResult[index++] = 'D';
						lineLength += 3;
					}
				}
				else
				{
					btResult[index++] = data[i];
					lineLength = 0;
				}
			}
			else if( data[i] == ' ' || data[i] == '\t' )
			{
				if( isBinary )
				{
					btResult[index++] = data[i];
					lineLength++;
				}
				else
				{
					if( (i + 1) < length && (data[i + 1] == '\r' || data[i + 1] == '\n') )
					{
						if( data[i] == ' ' )
						{
							btResult[index++] = '=';
							btResult[index++] = '2';
							btResult[index++] = '0';
							lineLength += 3;
						}
						else
						{
							btResult[index++] = '=';
							btResult[index++] = '0';
							btResult[index++] = '9';
							lineLength += 3;
						}
					}
					else
					{
						btResult[index++] = data[i];
						lineLength++;
					}
				}
			}
			else
			{
				bFirstTemp = (byte)(((int)data[i] & 0xFF) >> 4);
				bSecondTemp = (byte)(((int)data[i] & 0xFF) & 0x0F);

				if( bFirstTemp < 10 )
				{
					bFirstTemp += '0';
				}
				else
				{
					bFirstTemp += 55;
				}

				if( bSecondTemp < 10 )
				{
					bSecondTemp += '0';
				}
				else
				{
					bSecondTemp += 55;
				}

				btResult[index++] = '=';
				btResult[index++] = bFirstTemp;
				btResult[index++] = bSecondTemp;
				lineLength += 3;
			}

			if( isNewLine && lineLength > LINE_WIDTH )
			{
				btResult[index++] = '=';
				btResult[index++] = '\n';
				lineLength = 0;
			}
		}

		String encodedData = new String(btResult);

		return encodedData.trim();
	}

	public static byte[] decode(String encodedData)
	{
		byte[] data = encodedData.getBytes();
		int length = data.length;
		byte[] btTemp = new byte[length + 1];

		int pos = -1;
		int index = 0;

		byte bFirstTemp = 0;
		byte bSecondTemp = 0;

		for( int i = 0; i < length; i++ )
		{
			switch( data[i] )
			{
			case '=':
				i++;
				pos = i;
				if( i < length )
				{
					bFirstTemp = data[i];
				}
				else
				{
					i--;
					bFirstTemp = 0;
				}

				i++;

				if( i < length )
				{
					bSecondTemp = data[i];
				}
				else
				{
					i--;
					bSecondTemp = 0;
				}

				if( Character.isLetterOrDigit((char)bFirstTemp) && Character.isLetterOrDigit((char)bSecondTemp) )
				{
					if( Character.isDigit((char)bFirstTemp) )
					{
						bFirstTemp = (byte)(bFirstTemp - '0');
					}
					else
					{
						bFirstTemp = (byte)(bFirstTemp - (Character.isUpperCase((char)bFirstTemp) ? 'A' - 10 : 'a' - 10));
					}

					if( Character.isDigit((char)bSecondTemp) )
					{
						bSecondTemp = (byte)(bSecondTemp - '0');
					}
					else
					{
						bSecondTemp = (byte)(bSecondTemp - (Character.isUpperCase((char)bSecondTemp) ? 'A' - 10 : 'a' - 10));
					}

					btTemp[index++] = (byte)((int)bSecondTemp + ((int)bFirstTemp << 4));
				}
				else
				{
					i = pos;

					for( ; i < length && (data[i] == ' ') || (data[i] == '\t'); i++ ) ;

					boolean bSoftLF = false;

					if( i < length )
					{
						if( data[i] == '\r' )
						{
							if( (i + 1) < length && (data[i + 1] == '\n') )
							{
								bSoftLF = true;
								i++;
							}
						}
						else if( data[i] == '\n' )
						{
							bSoftLF = true;
						}
					}

					if( !bSoftLF )
					{
						btTemp[index++] = '=';
						i = pos - 1;
					}
				}
				break;
			default:
				btTemp[index++] = data[i];
			}
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
