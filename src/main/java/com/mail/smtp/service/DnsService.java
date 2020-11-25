package com.mail.smtp.service;

import com.mail.smtp.config.SmtpConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.dns.*;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.util.NetUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/*
reference link :
    https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/dns
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class DnsService
{
    private final SmtpConfig smtpConfig;

    private void handleQueryRespMX(DefaultDnsResponse msg)
    {
        if (msg.count(DnsSection.QUESTION) > 0) {
            DnsQuestion question = msg.recordAt(DnsSection.QUESTION, 0);
            System.out.printf("name: %s%n", question.name());
        }

        int count = msg.count(DnsSection.ANSWER);
        System.out.println("answer count : " + count);

        //error
        if( count == 0 )
        {
            System.out.println("code : " + msg.code().toString());
            System.out.println("code int : " + msg.code().intValue());
        }

        for (int i = 0;  i < count; i++) {
            DnsRecord record = msg.recordAt(DnsSection.ANSWER, i);
            if (record.type() == DnsRecordType.MX) {
                //just print the IP after query
                DnsRawRecord raw = (DnsRawRecord) record;
                ByteBuf content = raw.content();
                //read 2byte
                System.out.println("preference : " + content.readUnsignedShort());
                System.out.println("mx name  : " + DefaultDnsRecordDecoder.decodeName(content));
            }
        }
    }

    private Bootstrap makeBootstrap(@NonNull EventLoopGroup group)
    {
        Bootstrap b = new Bootstrap();
        b.group(group)
        .channel(NioSocketChannel.class)
        .handler(
            new ChannelInitializer< SocketChannel >()
            {
                @Override
                protected void initChannel(SocketChannel socketChannel)
                {
                    ChannelPipeline p = socketChannel.pipeline();
                    p.addLast(new TcpDnsQueryEncoder())
                        .addLast(new TcpDnsResponseDecoder())
                        .addLast(new SimpleChannelInboundHandler< DefaultDnsResponse >()
                        {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                                        DefaultDnsResponse defaultDnsResponse)
                            {
                                try
                                {
                                    handleQueryRespMX(defaultDnsResponse);
                                }
                                finally
                                {
                                    channelHandlerContext.close();
                                }
                            }
                        });
                }
            }
        );

        return b;
    }

    public void resolveMxRecordByTcp(String domainName) throws Exception
    {
        String dnsIp = smtpConfig.getString("smtp.dns.ip", "8.8.8.8");
        Integer dnsTimeout = smtpConfig.getInt("smtp.dns.timeout", 30);
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = makeBootstrap(group);

        try
        {
            final Channel ch = b.connect(dnsIp, 53).sync().channel();
            int randomID = new Random().nextInt(60000 - 1000) + 1000;
            DnsQuery query = new DefaultDnsQuery(randomID, DnsOpCode.QUERY)
                    .setRecord(DnsSection.QUESTION, new DefaultDnsQuestion(domainName, DnsRecordType.MX));
            ch.writeAndFlush(query).sync();
            boolean bSuccess = ch.closeFuture().await(dnsTimeout, TimeUnit.SECONDS);

            //timeout occured
            if( !bSuccess )
                ch.close().sync();
        }
        finally
        {
            group.shutdownGracefully();
        }
    }

    public void resolveMxRecordByUdp(String domainName)
    {

    }

    public void resolveTxtRecordByTcp(String domainName)
    {

    }

    public void resolveTxtRecordByUdp(String domainName)
    {

    }

    public void resolveARecordByTcp(String domainName)
    {

    }

    public void resolveARecordByUdp(String domainName)
    {

    }
}
