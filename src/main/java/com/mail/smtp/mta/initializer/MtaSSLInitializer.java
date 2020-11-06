package com.mail.smtp.mta.initializer;

import com.mail.smtp.mta.SmtpData;
import com.mail.smtp.mta.handler.SmtpListenerHandler;
import com.mail.smtp.mta.handler.SmtpSSLServerHandler;
import com.mail.smtp.mta.handler.SmtpStringEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;

@Slf4j
@RequiredArgsConstructor
public class MtaSSLInitializer extends ChannelInitializer< SocketChannel >
{
    private final SslContext sslContext;

    @Override
    protected void initChannel(SocketChannel socketChannel)
    {
        ChannelPipeline p = socketChannel.pipeline();
        //파이프라인의 가장 앞쪽에 ssl handler 를 등록해준다.
        p.addLast(sslContext.newHandler(socketChannel.alloc()));
        p.addLast("line", new DelimiterBasedFrameDecoder(1000, Delimiters.lineDelimiter()));
        p.addLast("decoder", new StringDecoder());  // CharsetUtil.US-ASCII
        //p.addLast("encoder", new StringEncoder());
        p.addLast("encoder", new SmtpStringEncoder());
        //sslContext of smtpdata is dummy
        p.addLast("basehandler", new SmtpSSLServerHandler(new SmtpData(sslContext)));
        p.addLast("listenerhandler", new SmtpListenerHandler());
    }
}
