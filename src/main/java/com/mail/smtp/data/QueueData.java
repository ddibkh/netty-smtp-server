package com.mail.smtp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueData implements Serializable
{
    private String mailUid;
    private String envelopeFrom;
    private List<String> toLocal;
    private List<String> toRemote;
    private String queuePath;
    private MailAttribute mailAttribute;
}
