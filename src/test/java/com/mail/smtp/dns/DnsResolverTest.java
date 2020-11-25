package com.mail.smtp.dns;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.delivery.DnslookupFailureHandler;
import com.mail.smtp.delivery.DnslookupSuccessHandler;
import com.mail.smtp.dns.result.DnsResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest( classes = {
        DnsResolverMX.class,
        DnsResolverTXT.class,
        DnsResolverA.class,
        SmtpConfig.class,
        DnsConfiguration.class
} )
class DnsResolverTest
{
    @Resource(name="dnsResolverMX")
    private DnsResolver dnsResolverMX;
    @Resource(name="dnsResolverTXT")
    private DnsResolver dnsResolverTXT;
    @Resource(name="dnsResolverA")
    private DnsResolver dnsResolverA;

    /*@Test
    void mxAsyncTest()
    {
        for( int i = 0; i < 100; i++ )
        {
            String domainName;
            ListenableFuture<List<DnsResult>> f;
            if( i % 4 == 0 )
            {
                domainName = "deepsoft.co.kr";
                f = dnsResolverTXT.resolveDomainByUdp(domainName);
            }
            else if( i % 4 == 1)
            {
                domainName = "naver.com";
                f = dnsResolverA.resolveDomainByTcp(domainName);
            }
            else if( i % 4 == 2 )
            {
                domainName = "google.com";
                f = dnsResolverMX.resolveDomainByUdp(domainName);
            }
            else
            {
                domainName = "nate.com";
                f = dnsResolverMX.resolveDomainByTcp(domainName);
            }

            *//*f.addCallback(
                    results -> Objects.requireNonNull(results).stream().forEach(System.out::println)
                    ,
                    throwable -> System.out.println("exception : " + throwable.getMessage())
            );*//*

            f.addCallback(
                    new DnslookupSuccessHandler(),
                    new DnslookupFailureHandler()
            );

            while( !f.isDone() );
        }
    }*/

}