/*
auther : ddibkh
description : DNS resolver
reference : https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/dns
 */

package com.mail.smtp.dns;

import com.mail.smtp.dns.result.DnsResult;
import com.mail.smtp.exception.DnsException;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

public interface DnsResolver
{
    <T extends DnsResult> List< T > resolveDomainByTcp(String domainName) throws DnsException;
    <T extends DnsResult> List< T > resolveDomainByUdp(String domainName) throws DnsException;
}
