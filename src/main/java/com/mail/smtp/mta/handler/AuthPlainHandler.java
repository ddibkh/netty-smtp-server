package com.mail.smtp.mta.handler;

import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.mta.authentication.CheckAuth;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.base64.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthPlainHandler<T extends CheckAuth> extends AuthHandler
{
    private final String plainText;
    public AuthPlainHandler(SmtpData smtpData, ChannelHandler channelHandler, T auth, String plainText)
    {
        super(smtpData, channelHandler, auth);
        this.plainText = plainText;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
    {
        log.trace("auth plain channel added");
        if( this.plainText.equals("") )
            ctx.writeAndFlush("334\r\n");
        else
            channelRead(ctx, this.plainText);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        String line = String.valueOf(msg);
        ByteBuf bb = Base64.decode(Unpooled.wrappedBuffer(line.getBytes()));
        byte[] bytes = new byte[bb.readableBytes()];
        bb.readBytes(bytes);
        String authPlain = new String(bytes);

        //authzid-authorization(UTF8NUL)authcid-authentication(UTF8NUL)passwd
        authPlain = authPlain.codePoints().map(c -> {
            if( c == 0 )
                return ',';
            else
                return c;
        }).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

        log.debug("auth plain : {}", authPlain);
        String[] plainText = authPlain.split(",");

        if( plainText.length == 3 )
        {
            String authId = plainText[0];
            String userId = plainText[1];
            String userPass = plainText[2];

            super.setUserId(userId);
            super.setUserPass(userPass);

            boolean bAuth = super.auth();

            super.getSmtpData().setAuthed(bAuth);

            if( bAuth )
                ctx.writeAndFlush("235 Authentication successful\r\n");
            else
                throw new SmtpException(535);
        }
        else
        {
            log.error("fail to auth plain, invalid data format {}", line);
            throw new SmtpException(501);
        }

        replaceBaseHandler(ctx);
    }
}
