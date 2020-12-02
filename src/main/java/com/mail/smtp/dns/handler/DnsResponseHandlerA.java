package com.mail.smtp.dns.handler;

import com.mail.smtp.dns.result.DnsResult;
import com.mail.smtp.exception.DnsException;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.util.NetUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/*
auther : ddibkh
description : A 레코드 결과 처리 핸들러
reference : https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/dns
 */
public class DnsResponseHandlerA<T extends DnsResponse> extends DnsResponseHandler<T>
{
    @Getter
    private String domainName;

    public DnsResponseHandlerA(Class<T> classI)
    {
        super(classI, DnsRecordType.A);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        String message;
        if( cause instanceof ReadTimeoutException )
            message = "A handler read timed out";
        else if( cause instanceof WriteTimeoutException )
            message = "A handler write timed out";
        else
            message = String.format("A handler exception caught, %s", cause.getMessage());

        //close 이전에 attribute 를 set 해야한다.
        DnsResult dnsResult = new DnsResult(DnsResult.Type.A, domainName, Collections.emptyList());
        ctx.channel().attr(RECORD_RESULT).set(dnsResult);
        ctx.channel().attr(ERROR_MSG).set(message);

        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                T dnsResponse)
    {
        if (dnsResponse.count(DnsSection.QUESTION) > 0) {
            DnsQuestion question = dnsResponse.recordAt(DnsSection.QUESTION, 0);
            domainName = question.name();
        }
        else
            domainName = "";

        int count = dnsResponse.count(DnsSection.ANSWER);

        //error
        if( count == 0 )
        {
            throw new DnsException(dnsResponse.code().toString());
        }
        else
        {
            List<String> results = new ArrayList<>();
            for (int i = 0;  i < count; i++)
            {
                DnsRecord record = dnsResponse.recordAt(DnsSection.ANSWER, i);
                if (record.type() == DnsRecordType.A) {
                    DnsRawRecord raw = (DnsRawRecord) record;
                    results.add(NetUtil.bytesToIpAddress(ByteBufUtil.getBytes(raw.content())));
                }
            }

            DnsResult aResult = new DnsResult(DnsResult.Type.A, domainName, results);
            channelHandlerContext.channel().attr(RECORD_RESULT).set(aResult);
        }

        channelHandlerContext.close();
    }
}
