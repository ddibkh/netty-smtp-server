package com.mail.smtp.mta.handler;
/*
 * Basic SMTP server in Java using Netty
 *
 * Author: Maarten Oelering
 * Modifier: kkwang
 */

import com.mail.smtp.mta.ApplicationContextProvider;
import com.mail.smtp.mta.SmtpData;
import com.mail.smtp.mta.protocol.ProtocolService;
import com.mail.smtp.util.CommonUtil;
import io.netty.channel.*;
import kr.co.deepsoft.util.JNIJavaMail;
import kr.co.deepsoft.util.MimeAttachInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

@Slf4j
@ChannelHandler.Sharable
public class SmtpServerHandler extends ChannelInboundHandlerAdapter
{
	protected static Integer ncnt=1;
    protected SmtpData smtpData;

	public SmtpServerHandler(SmtpData sd)
	{
		this.smtpData = sd;
	}
	
	public SmtpData getSmtpData()
	{
		return smtpData;
	}

    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        // Send greeting for a new connection.
        //ByteBuf bb = Unpooled.copiedBuffer("220 " + hostname + " ESMTP\r\n", Charset.defaultCharset());
        //ctx.writeAndFlush(bb);
        /*
         * ByteBuf 안쓸려면 encoder 를 StringEncoder 로 셋팅해줘야 함.
         */
        InetSocketAddress sa = ((InetSocketAddress) ctx.channel().remoteAddress());
        String clientip = sa.getAddress().getHostAddress();

        MDC.put("IP", clientip);
        MDC.put("UID", smtpData.getRandomUID());
        int port = sa.getPort();
        smtpData.setClientIP(clientip);
        smtpData.setClientPort(port);

        log.info("connected " + clientip + ":" + port);
        ctx.writeAndFlush("220 " + CommonUtil.getHostName() + " ESMTP\r\n");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        String line = (String)msg;
        log.info(">>> {}", line);
        ProtocolService protocol = ApplicationContextProvider.getBean(ProtocolService.class);
        protocol.process(ctx, line, smtpData);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
    	ctx.close();
    	log.info("closed " + smtpData.getClientIP() + ":" + smtpData.getClientPort());

    	if( smtpData.isCompleteData() )
    	{
            synchronized( ncnt )
            {
                System.out.println("received mail count : " + ncnt++);
            }

    		//save mail and parse
            String strEmlName = smtpData.getRandomUID();
        	strEmlName += ".eml";
        	
        	log.info("eml name : " + strEmlName);

            try( FileOutputStream fileOutputStream = new FileOutputStream(strEmlName) )
            {
                String strMessage = smtpData.getMessage();
                fileOutputStream.write(strMessage.getBytes());
            }
            catch( Exception e )
            {
                log.error("fail to save eml " + strEmlName + ", " + e.getMessage());
                throw e;
            }
    		
    		//parsing eml
    		JNIJavaMail mi = new JNIJavaMail();
    		try
    		{
    			mi.open(strEmlName);
    			//public String getHeaderValue(String hdrName, boolean decode, String defaultFromCS, String toCS, String PEmlID)
    			String strSubject = mi.getHeaderValue("Subject", true, "UTF-8", "UTF-8", "");
    			String strHdrFrom = mi.getHeaderValue("From", true, "UTF-8", "UTF-8", "");
    			String strHdrTo = mi.getHeaderValue("To", true, "UTF-8", "UTF-8", "");
    			
    			//public ArrayList<MimeAttachInfo> getAttachInfo(String defaultFromCS, String toCS, String PEmlID)
    			ArrayList< MimeAttachInfo > attachInfo = mi.getAttachInfo("UTF-8", "UTF-8", "");
    			log.info("============== parsing result ==============");
    			log.info("subject : " + strSubject);
    			log.info("from : " + strHdrFrom);
    			log.info("to : " + strHdrTo);
    			log.info("============== attach result ==============");
    			for( int i = 0; i < attachInfo.size(); i++ )
    				log.info("attach " + i + " : " + attachInfo.get(i).fileName);
    			log.info("============== attach result ==============");
    			log.info("============== parsing result ==============");

                smtpData.setSubject(strSubject);
    		}
    		catch(Exception e)
    		{
    			log.error("fail to parsing eml " + strEmlName + ", " + e.getMessage());
    			throw e;
    		}
    		finally
    		{
    			mi.close();
    		}

    		MDC.clear();
    	}
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
    {
        log.trace("handlerAdded [" + toString() + "]");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
    {
        log.trace("handlerRemoved [" + toString() + "]");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
    {
        log.trace("channelRegistered [" + toString() + "]");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)
    {
        log.trace("channelUnRegistered [" + toString() + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
    {
        log.trace("channelReadComplete [" + toString() + "]");
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
    {
        log.trace("userEventTriggered [" + toString() + "]");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx)
    {
        log.trace("channelWritabilityChanged [" + toString() + "]");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        log.error("exception occured, {}", cause.getMessage());
    	if( log.isTraceEnabled() )
    	    cause.printStackTrace();
        ctx.close();
        MDC.clear();
    }
}

