package com.mail.smtp.mta.protocol;

import com.mail.smtp.mta.SmtpData;
import com.mail.smtp.mta.handler.SmtpMsaServerHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailFrom
{
    public void process(ChannelHandlerContext ctx, SmtpData smtpData, String commandData)
    {
        /*
        여기에 commandData(mail from address) 의 검증 및 로컬사용자 여부 확인 및 계정 체크.
         */

        if( ctx.handler() instanceof SmtpMsaServerHandler )
        {
            if( !smtpData.isAuthed() )
            {
                ctx.write("505 Authentication required\r\n");
                return;
            }
        }

        String msg = "250 " + commandData + " ... Sender OK";
        ctx.write(msg + "\r\n");
        smtpData.setMailfrom(commandData);
    }
}
