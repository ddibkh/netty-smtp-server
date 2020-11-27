/*
auther : ddibkh
description : DNS resolver
reference : https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/dns
 */

package com.mail.smtp.dns;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.dns.result.MXResult;
import com.mail.smtp.exception.DnsException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.handler.codec.dns.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.mail.smtp.dns.handler.DnsResponseHandler.MX_RECORD_RESULT;

@Component("dnsResolverMX")
@RequiredArgsConstructor
@Lazy
public class DnsResolverMX implements DnsResolver
{
    private final Logger log = LoggerFactory.getLogger("delivery");
    private final SmtpConfig smtpConfig;
    private final Bootstrap tcpMxBootstrap;
    private final Bootstrap udpMxBootstrap;

    public List< MXResult > resolveDomainByTcp(String domainName) throws DnsException
    {
        String dnsIp = smtpConfig.getString("smtp.dns.ip", "8.8.8.8");
        Integer dnsTimeout = smtpConfig.getInt("smtp.dns.timeout", 30);

        short randomID = (short)new Random().nextInt(1 << 15);

        final Channel ch;
        try
        {
            ch = tcpMxBootstrap.connect(dnsIp, 53).sync().channel();
        }
        catch( Throwable cte )
        {
            log.error("fail to connect dns server, {}", cte.getMessage());
            throw new DnsException(
                    String.format("fail to connect dns server, %s", cte.getMessage()));
        }

        DnsQuery query = new DefaultDnsQuery(randomID, DnsOpCode.QUERY)
                .setRecord(DnsSection.QUESTION, new DefaultDnsQuestion(domainName, DnsRecordType.MX))
                .setRecursionDesired(true);

        try
        {
            ch.writeAndFlush(query).sync().addListener(
                    future ->
                    {
                        if( !future.isSuccess() )
                            throw new DnsException("fail send query message");
                        else if( future.isCancelled() )
                            throw new DnsException("operation cancelled");
                    }
            );

            boolean bSuccess = ch.closeFuture().await(dnsTimeout, TimeUnit.SECONDS);

            //timeout occured
            if( !bSuccess )
            {
                log.error("fail to resolve domain by TCP, timed out, domain : {}, dns : {}", domainName, dnsIp);
                ch.close().sync();
                throw new DnsException(String.format(
                        "fail to resolve domain by TCP, timed out, domain : %s, dns : %s", domainName, dnsIp));
            }
        }
        catch( InterruptedException ie )
        {
            log.error("fail to resolve MX, interrupted exception");
            throw new DnsException("fail to resolve MX, interrupted exception");
        }

        return ch.attr(MX_RECORD_RESULT).get();
    }

    public List< MXResult > resolveDomainByUdp(String domainName) throws DnsException
    {
        String dnsIp = smtpConfig.getString("smtp.dns.ip", "8.8.8.8");
        Integer dnsTimeout = smtpConfig.getInt("smtp.dns.timeout", 10);

        short randomID = (short)new Random().nextInt(1 << 15);

        InetSocketAddress addr = new InetSocketAddress(dnsIp, 53);

        final Channel ch;
        try
        {
            ch = udpMxBootstrap.bind(0).sync().channel();

            DnsQuery query = new DatagramDnsQuery(null, addr, randomID)
                    .setRecord(DnsSection.QUESTION, new DefaultDnsQuestion(domainName, DnsRecordType.MX))
                    .setRecursionDesired(true);

            ch.writeAndFlush(query).sync().addListener(
                    future ->
                    {
                        if( !future.isSuccess() )
                            throw new DnsException("fail send query message");
                        else if( future.isCancelled() )
                            throw new DnsException("operation cancelled");
                    }
            );

            boolean bSuccess = ch.closeFuture().await(dnsTimeout, TimeUnit.SECONDS);
            if( !bSuccess )
            {
                log.error("fail to resolve domain by UDP, timed out, domain : {}, dns : {}", domainName, dnsIp);
                ch.close().sync();
                throw new DnsException(String.format(
                        "fail to resolve domain by UDP, timed out, domain : %s, dns : %s", domainName, dnsIp));
            }
        }
        catch( InterruptedException ie )
        {
            log.error("fail to resolve MX, interrupted exception");
            throw new DnsException("fail to resolve MX, interrupted exception");
        }

        return ch.attr(MX_RECORD_RESULT).get();
    }
}
