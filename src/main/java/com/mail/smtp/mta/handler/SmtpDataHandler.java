/*
 * Basic SMTP server in Java using Netty
 *
 * Author: ddibkh
 */

package com.mail.smtp.mta.handler;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.data.MailAttribute;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.mta.ApplicationContextProvider;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.service.MimeParseService;
import com.mail.smtp.service.QueuingService;
import com.mail.smtp.service.SaveMailService;
import com.mail.smtp.util.CommonUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class SmtpDataHandler extends ChannelInboundHandlerAdapter
{
	private final SmtpData smtpData;
	private final ChannelHandler baseHandler;
	private String tempEmlPath;

	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
		log.trace("smtp data handler active!! [" + toString() + "]");
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx)
	{
    	log.trace("handlerAdded [" + toString() + "]");
    	//================================= DEBUG ================================= 
    	log.trace("clientip : " + smtpData.getClientIP());
    	log.trace("client port : " + smtpData.getClientPort());
    	log.trace("mail from : " + smtpData.getMailfrom());
    	log.trace("rcpt to: " + smtpData.getReceipents());
    	//================================= DEBUG =================================
    }
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
	{
    	log.trace("handlerRemoved [" + toString() + "]");
		Optional.ofNullable(tempEmlPath).ifPresent(path -> {
			File file = new File(path);
			if( file.exists() )
			{
				if( file.delete() )
					log.debug("success to deleted temp file, {}", path);
				else
					log.debug("fail to delete temp file, {}", path);
			}
		});
    }
    
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		String line = (String)msg;
		line += "\r\n";

		//complete receive data
		if ( line.trim().equals(".") ) {
			SmtpConfig smtpConfig = ApplicationContextProvider.getBean(SmtpConfig.class);
			String tempPath = smtpConfig.getString("smtp.temp.path", "");
			if( tempPath.equals("") )
				tempPath = CommonUtil.getMyPath() + File.separator + "temp" + File.separator;

			File file = new File(tempPath);
			if( !file.exists() )
			{
				if( !file.mkdirs() )
				{
					log.error("fail to data handler, mkdir temp path failed, {}", tempPath);
					throw new SmtpException(458);
				}
			}

			//save eml to temp path
			StringBuilder tempBuilder = new StringBuilder();
			tempBuilder.append(tempPath).append(smtpData.getRandomUID()).append(".eml");

			try( FileOutputStream fileOutputStream = new FileOutputStream(tempBuilder.toString()) )
			{
				String strMessage = smtpData.getMessage();
				fileOutputStream.write(strMessage.getBytes());
			}
			catch( Exception e )
			{
				log.error("fail to save temp eml " + tempBuilder.toString() + ", " + e.getMessage());
				throw new SmtpException(458);
			}

			tempEmlPath = tempBuilder.toString();

			MimeParseService mimeParseService = ApplicationContextProvider.getBean(MimeParseService.class);
			MailAttribute mailAttribute;
			try{
				mailAttribute = mimeParseService.getMailAttributeFromEml(tempEmlPath);
				mailAttribute.setMailUid(smtpData.getRandomUID());
				mailAttribute.setEnvFrom(smtpData.getMailfrom().getAddress());
			}catch( Exception e ) {
				log.error("fail to get mail attribute, exception, {}", e.getMessage());
				throw new SmtpException(458);
			}

			QueuingService queuingService = ApplicationContextProvider.getBean(QueuingService.class);
			queuingService.queuing(smtpData, mailAttribute, tempEmlPath);

			//save to eml and queuing
			//save sent box
			if( smtpData.getMailfrom().isLocal() && smtpConfig.getInt("smtp.save.sent", 1).equals(1) )
			{
				SaveMailService saveMailService = ApplicationContextProvider.getBean(SaveMailService.class);
				saveMailService.saveSentBox(smtpData, mailAttribute, tempEmlPath);
			}

			String response = "250 Message accepted for delivery";
            ctx.writeAndFlush(response + "\r\n");
			smtpData.setCompleteData(true);
            ChannelPipeline cp = ctx.pipeline();
            cp.replace("datahandler", "basehandler", baseHandler);
        }
        else {
            // unescape leading dot
            if ( line.startsWith("..") ) {
            	line = line.substring(1);
            }

			smtpData.addMessage(line);
        }
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
	{
		log.trace("smtpdatahandler channelreadcomplete [" + toString() + "]");
		ctx.flush();
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx)
	{
		log.trace("smtpdatahandler channelUnregistered [" + toString() + "]");
		ChannelPipeline cp = ctx.pipeline();
		cp.replace("datahandler", "basehandler", baseHandler);
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		if( cause instanceof ReadTimeoutException )
			log.error("exception occured, read timed out");
		else if( cause instanceof SmtpException )
		{
			log.error("exception occured, error code : {}, message : {}",
					( (SmtpException) cause ).getErrorCode(), cause.getMessage());
			ctx.writeAndFlush(( (SmtpException) cause ).getResponse());
		}
		else
			log.error("exception occured, {}", cause.getMessage());

		if( log.isTraceEnabled() )
    		cause.printStackTrace();

        ctx.close();
		MDC.clear();
    }
}
