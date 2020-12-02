package com.mail.smtp.mta.handler;

import com.mail.smtp.data.ResponseData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseEncoder extends MessageToByteEncoder<ResponseData>
{
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponseData responseData, ByteBuf byteBuf) throws Exception
    {
        String uid = responseData.getMailUid();
        String msg = responseData.getMessage();
        String response = String.valueOf(msg);
        if( response.endsWith("\r\n") )
            response = response.substring(0, response.length() - 2);
        log.info("[{}] <<< {}", uid, response);
        channelHandlerContext.writeAndFlush(msg);
    }
}
