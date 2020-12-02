package com.mail.smtp.dns.configuration;

import com.mail.smtp.exception.DnsException;
import io.netty.bootstrap.Bootstrap;
import io.netty.handler.codec.dns.DnsRecordType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
public class BootstrapFactory
{
    private final Bootstrap tcpMxBootstrap;
    private final Bootstrap udpMxBootstrap;
    private final Bootstrap tcpTxtBootstrap;
    private final Bootstrap udpTxtBootstrap;
    private final Bootstrap tcpABootstrap;
    private final Bootstrap udpABootstrap;
    /*
    public static final DnsRecordType A = new DnsRecordType(1, "A");
    public static final DnsRecordType MX = new DnsRecordType(15, "MX");
    public static final DnsRecordType TXT = new DnsRecordType(16, "TXT");
     */
    public Bootstrap getBootstrapTcp(DnsRecordType type)
    {
        Bootstrap bootstrap;
        switch(type.intValue())
        {
            case 1:
                bootstrap = tcpABootstrap;
                break;
            case 15:
                bootstrap = tcpMxBootstrap;
                break;
            case 16:
                bootstrap = tcpTxtBootstrap;
                break;
            default:
                throw new DnsException("not support record type");
        }

        return bootstrap;
    }

    public Bootstrap getBootstrapUdp(DnsRecordType type)
    {
        Bootstrap bootstrap;
        switch(type.intValue())
        {
            case 1:
                bootstrap = udpABootstrap;
                break;
            case 15:
                bootstrap = udpMxBootstrap;
                break;
            case 16:
                bootstrap = udpTxtBootstrap;
                break;
            default:
                throw new DnsException("not support record type");
        }

        return bootstrap;
    }
}
