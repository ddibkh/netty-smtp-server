package com.mail.smtp.delivery;

import com.mail.smtp.dns.result.DnsResult;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.List;
import java.util.Objects;

public class DnslookupSuccessHandler implements SuccessCallback< List<DnsResult> >
{
    @Override
    public void onSuccess(List< DnsResult > dnsResults)
    {
        Objects.requireNonNull(dnsResults).stream().forEach(System.out::println);
    }
}
