package com.mail.smtp.mta.protocol;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.mta.SmtpData;
import com.mail.smtp.util.CommonUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Ehlo
{
    private final SmtpConfig smtpConfig;

    public void process(ChannelHandlerContext ctx, SmtpData smtpData)
    {
        String ehlo = "250-" + CommonUtil.getHostName() +
                " Pleased to meet you\r\n250-8BITMIME\r\n";

        if( smtpConfig.getInt("smtp.use.auth", 0).equals(1) )
            ehlo += "250-AUTH LOGIN\r\n250-AUTH PLAIN\r\n";

        if( smtpConfig.getInt("smtp.use.starttls", 1).equals(1) &&
            !smtpData.isSecureConnected() )
            ehlo += "250-STARTTLS\r\n";

        ehlo += "250 OK\r\n";

        ctx.write(ehlo);
    }
}
