package com.mail.smtp.mta.protocol;

import com.mail.smtp.mta.data.SmtpData;
import com.mail.smtp.util.CommonUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Data
@Component
@Slf4j
public class HELO
{
    public void process(ChannelHandlerContext ctx, SmtpData smtpData, String commandData)
    {
        smtpData.setHelo(commandData);
        ctx.writeAndFlush("250 " + CommonUtil.getHostName() + "\r\n");
    }
}
