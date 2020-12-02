package com.mail.smtp.dns.handler;

import com.mail.smtp.dns.result.DnsResult;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.util.AttributeKey;
import lombok.Getter;

public abstract class DnsResponseHandler<T extends DnsResponse> extends SimpleChannelInboundHandler< T >
{
    /*
    DNS 조회 채널의 결과를 받기 위한 선언. AttributeKey 에 결과 값을 저장한다.
     */
    public final static AttributeKey<DnsResult> RECORD_RESULT = AttributeKey.valueOf("record_result");
    public final static AttributeKey<String> ERROR_MSG = AttributeKey.valueOf("errormsg");

    @Getter
    private final DnsRecordType recordType;

    public DnsResponseHandler(Class<T> classI, DnsRecordType recordType)
    {
        super(classI);
        this.recordType = recordType;
    }
}
