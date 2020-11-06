package com.mail.smtp.mta.protocol;

import com.mail.smtp.mta.SmtpData;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RcptTo
{
    public void process(ChannelHandlerContext ctx, SmtpData smtpData, String commandData)
    {
        /*
        여기에 commandData(rcpt to address) 의 검증 및 로컬사용자 여부 확인 및 계정 체크.
         */
        String msg = "250 " + commandData + " ... Recipient OK";
        ctx.write(msg + "\r\n");
        smtpData.addReceipent(commandData);
    }
}
