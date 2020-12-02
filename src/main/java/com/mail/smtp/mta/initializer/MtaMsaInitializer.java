package com.mail.smtp.mta.initializer;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.mta.ApplicationContextProvider;
import com.mail.smtp.mta.handler.*;
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
public class MtaMsaInitializer extends ChannelInitializer< SocketChannel >
{
    private final SslContext sslContext;

    @Override
    protected void initChannel(SocketChannel socketChannel)
    {
        SmtpConfig smtpConfig = ApplicationContextProvider.getBean(SmtpConfig.class);

        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new ReadTimeoutHandler(smtpConfig.getInt("smtp.read.timeout", 30)));
        p.addLast(new WriteTimeoutHandler(smtpConfig.getInt("smtp.write.timeout", 30)));
        p.addLast("line", new DelimiterBasedFrameDecoder(1000, Delimiters.lineDelimiter()));
        p.addLast("decoder", new StringDecoder());  // CharsetUtil.US-ASCII
        p.addLast("encoder2", new StringEncoder());
        p.addLast("encoder1", new ResponseEncoder());
        //for STARTTLS
        p.addLast("basehandler", new SmtpMsaServerHandler(new SmtpData(sslContext)));
    }
}
