package com.mail.smtp.delivery;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
        //netty 환경에서는 threadpool 안에서 비동기로 처리되기 때문에 MDC 를 활용하기 어려워 보인다.
        //MDC 스레드 별로 유지되므로 비동기 스레드 실행시 현재 스레드의 MDC 를 복제해 주는 로직이 필요함.
        /*tp.setTaskDecorator(new TaskDecorator()
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
        });*/
        tp.initialize();
        return tp;
    }
}
