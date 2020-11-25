package com.mail.smtp.delivery;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.dns.handler.DnsResponseHandlerA;
import com.mail.smtp.dns.handler.DnsResponseHandlerMX;
import com.mail.smtp.dns.handler.DnsResponseHandlerTXT;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.dns.*;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
public class DeliveryConfiguration
{
    @Bean
    public Executor deliveryPoolExecutor()
    {
        ThreadPoolTaskExecutor tp = new ThreadPoolTaskExecutor();
        tp.setCorePoolSize(10);
        tp.setQueueCapacity(50);
        tp.setMaxPoolSize(100);
        tp.setThreadNamePrefix("delivery");
        //MDC 스레드 별로 유지되므로 비동기 스레드 실행시 현재 스레드의 MDC 를 복제해 주는 로직이 필요함.
        tp.setTaskDecorator(new TaskDecorator()
        {
            @Override
            public Runnable decorate(Runnable runnable)
            {
                Map<String, String> mdcMap = MDC.getCopyOfContextMap();
                if( mdcMap != null )
                {
                    return () -> {
                        MDC.setContextMap(mdcMap);
                        runnable.run();
                    };
                }
                else
                    return () -> runnable.run();
            }
        });
        tp.initialize();
        return tp;
    }
}
