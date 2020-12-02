package com.mail.smtp.mta.protocol;

import com.mail.smtp.data.ResponseData;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.mta.authentication.Sha256PwdAuth;
import com.mail.smtp.mta.handler.AuthLoginHandler;
import com.mail.smtp.mta.handler.AuthPlainHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AUTH
{
    public void process(@NonNull ChannelHandlerContext ctx, @NonNull SmtpData smtpData, @NonNull String commandData)
    {
        String authProtocol = commandData.codePoints().
                map(Character::toUpperCase).limit(5)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

        authProtocol = authProtocol.toUpperCase();
        if( authProtocol.equals("LOGIN") )
            login(ctx, smtpData);
        else if( authProtocol.equals("PLAIN") )
        {
            if( commandData.length() > 5 )
                plain(ctx, smtpData, commandData.substring(6));
            else
                plain(ctx, smtpData, "");
        }
        else
        {
            log.error("[{}] unsupported AUTH command : AUTH {}", smtpData.getRandomUID(), commandData);
            //ctx.writeAndFlush("500 unsupported AUTH command\r\n");
            ctx.writeAndFlush(new ResponseData(smtpData.getRandomUID(), "500 unsupported AUTH command\r\n"));
        }
    }

    private void login(ChannelHandlerContext ctx, SmtpData smtpData)
    {
        ChannelPipeline cp = ctx.pipeline();
        cp.replace("basehandler", "authhandler", new AuthLoginHandler<>(smtpData, ctx.handler(), new Sha256PwdAuth()));
    }

    private void plain(ChannelHandlerContext ctx, SmtpData smtpData, String plainText)
    {
        ChannelPipeline cp = ctx.pipeline();
        cp.replace("basehandler", "authhandler", new AuthPlainHandler<>(smtpData, ctx.handler(), new Sha256PwdAuth(), plainText));
    }
}
