package com.mail.smtp.util;

import org.apache.commons.net.util.SubnetUtils;
import org.springframework.util.DigestUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class CommonUtil
{
    public static String getLocalIP() throws UnknownHostException
    {
        return InetAddress.getLocalHost().getHostAddress();
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

    public static String makeUID()
    {
        String ts = String.valueOf(System.currentTimeMillis());
        String rand = UUID.randomUUID().toString();
        return DigestUtils.md5DigestAsHex(( ts + rand).getBytes());
    }

    public static boolean isSubnetRange(String subnet, String ip)
    {
        SubnetUtils subnetUtils = new SubnetUtils(subnet);
        //include network, broadcast ip
        subnetUtils.setInclusiveHostCount(true);
        return subnetUtils.getInfo().isInRange(ip);
    }
}
