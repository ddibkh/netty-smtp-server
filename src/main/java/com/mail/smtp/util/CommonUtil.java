package com.mail.smtp.util;

import com.mail.smtp.mta.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.util.DigestUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

public class CommonUtil
{
    public static String getLocalIP()
    {
        try
        {
            for( Enumeration< NetworkInterface > en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); )
            {
                NetworkInterface intf = en.nextElement();
                for( Enumeration< InetAddress > ipAddr = intf.getInetAddresses(); ipAddr.hasMoreElements(); )
                {
                    InetAddress ia = ipAddr.nextElement();
                    if( ia != null && !ia.isLoopbackAddress() && !ia.isLinkLocalAddress() && ia.isSiteLocalAddress() )
                        return ia.getHostAddress();
                }
            }
        }
        catch( SocketException ex)
        {

        }

        return "";
    }

    public static String makeUID()
    {
        String ts = String.valueOf(System.currentTimeMillis());
        String rand = UUID.randomUUID().toString();
        return DigestUtils.md5DigestAsHex(( ts + rand).getBytes());
    }

    public static String getHostName()
    {
        String hostName;
        try
        {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (java.net.UnknownHostException e)
        {
            hostName = "?";
        }

        return hostName;
    }
}
