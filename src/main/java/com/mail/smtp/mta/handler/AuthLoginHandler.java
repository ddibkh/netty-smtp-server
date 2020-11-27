package com.mail.smtp.mta.handler;

import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.mta.Define;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.mta.authentication.CheckAuth;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.base64.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthLoginHandler<T extends CheckAuth> extends AuthHandler
{
    private Define.AUTH_PROCESS now_status;

    public AuthLoginHandler(SmtpData smtpData, ChannelHandler channelHandler, T auth)
    {
        super(smtpData, channelHandler, auth);
        now_status = Define.AUTH_PROCESS.USERNAME;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.trace("auth login channel added");
        //request Username
        ctx.writeAndFlush("334 VXNlcm5hbWU6\r\n");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        String line = String.valueOf(msg);
        //id
        if( now_status == Define.AUTH_PROCESS.USERNAME )
        {
            ByteBuf bb = Base64.decode(Unpooled.wrappedBuffer(line.getBytes()));
            byte[] bytes = new byte[bb.readableBytes()];
            bb.readBytes(bytes);
            String userId = new String(bytes);
            super.setUserId(userId);

            //request Password
            ctx.writeAndFlush("334 UGFzc3dvcmQ6\r\n");
            now_status = Define.AUTH_PROCESS.PASSWORD;
        }
        //password
        else
        {
            ByteBuf bb = Base64.decode(Unpooled.wrappedBuffer(line.getBytes()));
            byte[] bytes = new byte[bb.readableBytes()];
            bb.readBytes(bytes);
            String userPass = new String(bytes);
            log.trace("user pass : {}", userPass);
            super.setUserPass(userPass);

            boolean bAuth = super.auth();

            super.getSmtpData().setAuthed(bAuth);

            String response;
            if( bAuth )
                response = "235 Authentication successful\r\n";
            else
                throw new SmtpException(535);

            ctx.writeAndFlush(response);
            replaceBaseHandler(ctx);
        }
    }
}
