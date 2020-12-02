package com.mail.smtp.mta.initializer;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.mta.ApplicationContextProvider;
import com.mail.smtp.mta.handler.ResponseEncoder;
import com.mail.smtp.mta.handler.SmtpSSLServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MtaSSLInitializer extends ChannelInitializer< SocketChannel >
{
    private final SslContext sslContext;

    @Override
    protected void initChannel(SocketChannel socketChannel)
    {
        SmtpConfig smtpConfig = ApplicationContextProvider.getBean(SmtpConfig.class);

        ChannelPipeline p = socketChannel.pipeline();
        //파이프라인의 가장 앞쪽에 ssl handler 를 등록해준다.
        p.addLast(new ReadTimeoutHandler(smtpConfig.getInt("smtp.read.timeout", 30)));
        p.addLast(new WriteTimeoutHandler(smtpConfig.getInt("smtp.write.timeout", 30)));
        p.addLast(sslContext.newHandler(socketChannel.alloc()));
        p.addLast("line", new DelimiterBasedFrameDecoder(1000, Delimiters.lineDelimiter()));
        p.addLast("decoder", new StringDecoder());  // CharsetUtil.US-ASCII
        p.addLast("encoder2", new StringEncoder());
        p.addLast("encoder1", new ResponseEncoder());
        //sslContext of smtpdata is dummy
        p.addLast("basehandler", new SmtpSSLServerHandler(new SmtpData(sslContext)));
    }
}
