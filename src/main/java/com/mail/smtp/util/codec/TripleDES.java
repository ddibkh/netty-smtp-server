package com.mail.smtp.util.codec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class TripleDES
{
    public static byte[] encode(String key, byte[] enc)
    {
		byte[] result = null;
		
        try 
        {
            int keyLength = key.length();
            if( keyLength < 24 )
            {
            	for( int i = 0; i < (24 - keyLength); i++)
            	{
            		key += "d";
            	}
            }
            
            SecretKeyFactory keyFac = SecretKeyFactory.getInstance("DESede");
            DESedeKeySpec keySpec = new DESedeKeySpec(key.getBytes());
 
            SecretKey secretKey = keyFac.generateSecret(keySpec);
            
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            result = cipher.doFinal(enc);
        } 
        catch( Exception e )
        { 
			System.out.println(e.toString()); 
        }
        
        return result;
    }
    
    public static byte[] decode(String key, byte[] enc)
    {
        byte[] result = null;
        
        try 
        {
            int keyLength = key.length();
            if( keyLength < 24 )
            {
            	for( int i = 0; i < (24 - keyLength); i++)
            	{
            		key += "d";
            	}
            }
            
            SecretKeyFactory keyFac = SecretKeyFactory.getInstance("DESede");
            DESedeKeySpec keySpec = new DESedeKeySpec(key.getBytes());
            
            SecretKey secretKey = keyFac.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            result =  cipher.doFinal(enc);
        } 
        catch( Exception e )
        { 
			System.out.println(e.toString());
        }
        
        return result;
    }
}
