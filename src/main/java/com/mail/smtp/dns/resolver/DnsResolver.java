/*
auther : ddibkh
description : DNS resolver
reference : https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/dns
 */

package com.mail.smtp.dns.resolver;

import com.mail.smtp.dns.result.DnsResult;
import com.mail.smtp.exception.DnsException;

import java.util.Random;

public interface DnsResolver
{
    static short getRandomId()
    {
        return (short)new Random().nextInt(1 << 15);
    }

    <T extends DnsResult > T
    resolveDomainByTcp(String domainName, RequestType requestType) throws DnsException;
    <T extends DnsResult > T
    resolveDomainByUdp(String domainName, RequestType requestType) throws DnsException;
}
