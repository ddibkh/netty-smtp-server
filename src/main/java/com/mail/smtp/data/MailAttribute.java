package com.mail.smtp.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class MailAttribute implements Serializable
{
    private String connIP;
    private String subject;
    private String envFrom;
    private String headerFrom;
    private List<String> headerTo;
    private List<String> headerCc;
    private List<String> headerBcc;
    private String messageId;
    private Integer mailSize;
    private String sendDate;
    private String recvDate;
    private String mailUid;
}
