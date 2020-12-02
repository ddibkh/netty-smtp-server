package com.mail.smtp.mta.protocol;

import com.mail.smtp.data.ResponseData;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.data.UserVO;
import com.mail.smtp.mta.handler.SmtpDataHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class DATA
{
    public void process(ChannelHandlerContext ctx, SmtpData smtpData)
    {
        Optional< UserVO > optMailFrom = Optional.ofNullable(smtpData.getMailfrom());
        //MAIL FROM 을 거치지 않으면 sequence command exception 발생.
        optMailFrom.orElseThrow(() -> new SmtpException(503));

        if( smtpData.getListRcptTo().isEmpty() )
            throw new SmtpException(503);

        String msg = "354 End data with <CR><LF>.<CR><LF>";
        //ctx.write(msg + "\r\n");
        ctx.write(new ResponseData(smtpData.getRandomUID(), msg + "\r\n"));
        ChannelPipeline cp = ctx.pipeline();
        cp.replace("basehandler", "datahandler", new SmtpDataHandler(smtpData, ctx.handler()));
    }
}
