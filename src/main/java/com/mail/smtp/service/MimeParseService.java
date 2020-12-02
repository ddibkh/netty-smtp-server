package com.mail.smtp.service;

import com.mail.smtp.data.MailAttribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MimeParseService
{
    public MailAttribute getMailAttributeFromEml(String uid, String emlPath) throws Exception
    {
        //extrace mail attribute
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);

        MimeMessage message;
        try( InputStream inputStream = new FileInputStream(emlPath) )
        {
            message = new MimeMessage(mailSession, inputStream);
        }
        catch( FileNotFoundException fnfe )
        {
            log.error("[{}] fail to save sent box, file not found exception, {}", uid, fnfe.getMessage());
            throw new Exception(fnfe);
        }
        catch( IOException ie)
        {
            log.error("[{}] fail to save sent box, io exception, {}", uid, ie.getMessage());
            throw new Exception(ie);
        }

        final MailAttribute mailAttribute = new MailAttribute();
        mailAttribute.setSubject(Optional.ofNullable(message.getSubject()).orElse(""));
        String headerFrom = Optional.ofNullable(message.getFrom()[0].toString()).orElse("");
        if( !headerFrom.equals("") )
            headerFrom = MimeUtility.decodeText(MimeUtility.unfold(headerFrom));
        mailAttribute.setHeaderFrom(headerFrom);

        Optional.ofNullable(message.getRecipients(Message.RecipientType.TO)).ifPresentOrElse(
                (array) -> mailAttribute.setHeaderTo(
                        Stream.of(array)
                                .map(address ->
                                {
                                    try
                                    {
                                        return MimeUtility.decodeText(MimeUtility.unfold(address.toString()));
                                    }
                                    catch( UnsupportedEncodingException e )
                                    {
                                        log.error("[{}] fail to decode header To address, {}", uid, address.toString());
                                        return "";
                                    }
                                })
                                .collect(Collectors.toList())),
                //in case not exist TO header
                () -> mailAttribute.setHeaderTo(Collections.emptyList())
        );

        Optional.ofNullable(message.getRecipients(Message.RecipientType.CC)).ifPresentOrElse(
                (array) -> mailAttribute.setHeaderCc(
                        Stream.of(array)
                                .map(address ->
                                {
                                    try
                                    {
                                        return MimeUtility.decodeText(MimeUtility.unfold(address.toString()));
                                    }
                                    catch( UnsupportedEncodingException e )
                                    {
                                        log.error("[{}] fail to decode header Cc address, {}", uid, address.toString());
                                        return "";
                                    }
                                })
                                .collect(Collectors.toList())),
                //in case not exist CC header
                () -> mailAttribute.setHeaderCc(Collections.emptyList())
        );

        Optional.ofNullable(message.getRecipients(Message.RecipientType.BCC)).ifPresentOrElse(
                (array) -> mailAttribute.setHeaderBcc(
                        Stream.of(array)
                                .map(address ->
                                {
                                    try
                                    {
                                        return MimeUtility.decodeText(MimeUtility.unfold(address.toString()));
                                    }
                                    catch( UnsupportedEncodingException e )
                                    {
                                        log.error("[{}] fail to decode header Bcc address, {}", uid, address.toString());
                                        return "";
                                    }
                                })
                                .collect(Collectors.toList())),
                //in case not exist CC header
                () -> mailAttribute.setHeaderBcc(Collections.emptyList())
        );

        mailAttribute.setMessageId(Optional.ofNullable(message.getMessageID()).orElse(""));
        mailAttribute.setMailSize(message.getSize());
        mailAttribute.setSendDate(Optional.ofNullable(message.getHeader("Date", null)).orElse(""));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss Z");
        Date recvTime = new Date();
        String recvDate = dateFormat.format(recvTime);
        mailAttribute.setRecvDate(recvDate);

        return mailAttribute;
    }
}
