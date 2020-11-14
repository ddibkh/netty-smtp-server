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
    private List<String> headerFrom;
    private List<String> headerTo;
    private List<String> headerCc;
    private List<String> headerBcc;
    private String messageId;
    private String mailSize;
    private String sendDate;
    private String recvDate;
    private String emlPath;
}
