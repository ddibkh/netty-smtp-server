package com.mail.smtp.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
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

    public static String getMyPath()
    {
        Path relativePath = Paths.get("");
        String path = relativePath.toAbsolutePath().toString();
        return path;
    }

    public static boolean fileCopy(String sourPath, String destPath)
    {
        try(
                FileInputStream inputStream = new FileInputStream(sourPath);
                FileOutputStream outputStream = new FileOutputStream(destPath);
                FileChannel fcin = inputStream.getChannel();
                FileChannel fcout = outputStream.getChannel();
        )
        {
            long size = fcin.size();
            fcin.transferTo(0, size, fcout);
        }
        catch( FileNotFoundException fnfe )
        {
            log.error("fail to copy, file not found exception, {} -> {}", sourPath, destPath);
            return false;
        }
        catch( IOException ie )
        {
            log.error("fail to copy, io exception, {} -> {}", sourPath, destPath);
            return false;
        }

        return true;
    }
}
