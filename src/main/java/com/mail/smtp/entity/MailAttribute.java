package com.mail.smtp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class MailAttribute implements Serializable
{
    private String uid;
    private String subject;
    private String envelopeSender;
    private String envelopeReceiver;
    private String headerFrom;
    private String headerTo;
    private String headerCc;
    private String headerBcc;
    private String messageId;
    private String mailSize;
    private String sendDate;
    private String recvDate;
    private String emlPath;
}
