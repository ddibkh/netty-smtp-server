package com.mail.smtp.mta;
/*
 * Basic SMTP server in Java using Netty
 *
 * Author: Maarten Oelering
 * Modifier: kkwang
 */

import com.mail.smtp.util.CommonUtil;
import io.netty.channel.*;
import kr.co.deepsoft.util.JNIJavaMail;
import kr.co.deepsoft.util.MimeAttachInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.MDC;

import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@ChannelHandler.Sharable
public class SmtpServerHandler extends ChannelInboundHandlerAdapter
{
	protected static Integer ncnt=1;
    protected static String hostname;
    protected SmtpData m_Data;
    protected StringBuffer message;
    protected String randomUID;

	SmtpServerHandler(SmtpData sd)
	{
		this.m_Data = sd;
		message = new StringBuffer();
		randomUID = "";
	}
	
	public SmtpData getSmtpData()
	{
		return m_Data;
	}

    static {
        try
        {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (java.net.UnknownHostException e) 
        {
            hostname = "?";
        }
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
        //randomUID = RandomStringUtils.randomNumeric(10);
        randomUID = CommonUtil.makeUID();

        MDC.put("IP", clientip);
        MDC.put("UID", randomUID);
        int port = sa.getPort();
        m_Data.setClientIP(clientip);
        m_Data.setClientPort(port);

        log.info("connected " + clientip + ":" + port);
        ctx.writeAndFlush("220 " + hostname + " ESMTP\r\n");
        log.info("[channelactive] send message : 220 " + hostname + " ESMTP");
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
    	//System.out.println("channelRegistered");
    }
    
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)
    {
    	//System.out.println("channelUnregistered");
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
    	ctx.close();
    	//InetSocketAddress sa = ((InetSocketAddress) ctx.channel().remoteAddress());
    	//String clientip = sa.getAddress().getHostAddress();
    	//int port = sa.getPort();
    	log.info("closed " + m_Data.getClientIP() + ":" + m_Data.getClientPort());
    	
    	if( m_Data.isbCompleteData() )
    	{
    		//save mail and parse
        	//String strEmlName = RandomStringUtils.randomNumeric(10);
            String strEmlName = randomUID;
        	strEmlName += ".eml";
        	
        	log.info("eml name : " + strEmlName);

            try( FileOutputStream fileOutputStream = new FileOutputStream(strEmlName) )
            {
                String strMessage = m_Data.getMessage();
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

    			m_Data.setSubject(strSubject);
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
    public void channelReadComplete(ChannelHandlerContext ctx)
    {
    	//System.out.println("channelReadComplete");
    	ctx.flush();
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
    {
    	//System.out.println("userEventTriggered");
    }
    
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx)
    {
    	//System.out.println("channelWritabilityChanged");
    }
    


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) 
    {
    	//ByteBuf readMessage = (ByteBuf)msg;
        // We know it is a String because we put some codec in TelnetPipelineFactory.
        //String line = readMessage.toString(Charset.defaultCharset());
    	String line = (String)msg;
        onCommand(ctx, line);
    }
        
    private void onCommand(ChannelHandlerContext ctx, String line) 
    {
        String command;

        int i = line.indexOf(" ");
        if (i > 0) {
            command = line.substring(0, i).toUpperCase();
        }
        else {
            command = line.toUpperCase();
        }
        
        if (command.length() == 0) {
        	//ByteBuf bb = Unpooled.copiedBuffer("500 Error: bad syntax\r\n", Charset.defaultCharset());
        	//ctx.writeAndFlush(bb);
            ctx.write("500 Error: bad syntax\r\n");
            return;
        }

        switch( command )
        {
            case "HELO":
                //ByteBuf bb = Unpooled.copiedBuffer("250 " + hostname + "\r\n", Charset.defaultCharset());
                //ctx.writeAndFlush(bb);
                ctx.write("250 " + hostname + "\r\n");
                break;
            case "EHLO":
                //ByteBuf bb = Unpooled.copiedBuffer("250 " + hostname + "\r\n", Charset.defaultCharset());
                //ctx.writeAndFlush(bb);
                ctx.write("250 " + hostname + "\r\n");
                break;
            case "MAIL":
                message.setLength(0);  // new transaction

                //ByteBuf bb = Unpooled.copiedBuffer("250 OK\r\n", Charset.defaultCharset());
                //ctx.writeAndFlush(bb);
                ctx.write("250 OK\r\n");
                m_Data.setMailfrom(line);
                break;
            case "RCPT":
                //ByteBuf bb = Unpooled.copiedBuffer("250 OK\r\n", Charset.defaultCharset());
                //ctx.writeAndFlush(bb);
                ctx.write("250 OK\r\n");
                m_Data.addReceipent(line);
                break;
            case "DATA":
                //ByteBuf bb = Unpooled.copiedBuffer("354 End data with <CR><LF>.<CR><LF>\r\n", Charset.defaultCharset());
                //ctx.write(bb);
                ctx.write("354 End data with <CR><LF>.<CR><LF>\r\n");

                //ChannelPipeline cp = ctx.channel().pipeline();
                ChannelPipeline cp = ctx.pipeline();
                cp.replace("basehandler", "datahandler", new SmtpDataHandler(this));
                break;
            case "RSET":
                //ByteBuf bb = Unpooled.copiedBuffer("250 OK\r\n", Charset.defaultCharset());
                //ctx.writeAndFlush(bb);
                ctx.write("250 OK\r\n");
                break;
            case "QUIT":
                ChannelFuture future = ctx.write("221 " + hostname + " closing connection\r\n");
                future.addListener(ChannelFutureListener.CLOSE);
                synchronized( ncnt )
                {
                    System.out.println("received mail count : " + ncnt++);
                }
                break;
            default:
                //ByteBuf bb = Unpooled.copiedBuffer("500 unrecognized command\r\n", Charset.defaultCharset());
                //ctx.writeAndFlush(bb);
                ctx.write("500 unrecognized command\r\n");
                break;
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
    	cause.printStackTrace();
        ctx.close();
    }
}

