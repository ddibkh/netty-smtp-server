package com.mail.smtp.mta.initializer;

import com.mail.smtp.mta.MailCounter;
import com.mail.smtp.mta.SmtpData;
import com.mail.smtp.mta.handler.SmtpListenerHandler;
import com.mail.smtp.mta.handler.SmtpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class SmtpConfiguration
{
    /*@Scope("prototype")
    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public EventLoopGroup bossGroup() {
        return new NioEventLoopGroup(1);
    }

    @Scope("prototype")
    @Bean(name = "workGroup", destroyMethod = "shutdownGracefully")
    public EventLoopGroup workGroup() {
        return new NioEventLoopGroup();
    }*/

    /*@Bean(name = "bootstrap")
    public ServerBootstrap bootstrap()
    {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                //.handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>()
                {
                    @Override
                    public void initChannel(SocketChannel ch)
                    {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("line", new DelimiterBasedFrameDecoder(1000, Delimiters.lineDelimiter()));
                        p.addLast("decoder", new StringDecoder());  // CharsetUtil.US-ASCII
                        p.addLast("encoder", new StringEncoder());
                        p.addLast("basehandler", new SmtpServerHandler(new SmtpData()));
                        p.addLast("listenerhandler", new SmtpListenerHandler());
                    }
                });

        return bootstrap;
    }*/

    @Bean
    public AtomicInteger counter()
    {
        return new AtomicInteger();
    }

    @Bean
    public MailCounter mailCounter()
    {
        return new MailCounter();
    }
}
