package com.mail.smtp.dns.resolver;

import io.netty.handler.codec.dns.DnsRecordType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RequestType
{
    public static final RequestType REQUEST_A = new RequestType(DnsRecordType.A);
    public static final RequestType REQUEST_MX = new RequestType(DnsRecordType.MX);
    public static final RequestType REQUEST_TXT = new RequestType(DnsRecordType.TXT);
    private final DnsRecordType type;
}
