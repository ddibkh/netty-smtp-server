package com.mail.smtp.mta.protocol;

import com.mail.smtp.mta.SmtpData;
import com.mail.smtp.util.CommonUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Data
@Component
@Slf4j
public class Helo
{
    public void process(ChannelHandlerContext ctx)
    {
        ctx.write("250 " + CommonUtil.getHostName() + "\r\n");
    }
}
