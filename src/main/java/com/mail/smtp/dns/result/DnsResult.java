package com.mail.smtp.dns.result;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class DnsResult
{
    public enum Type {MX, A, TXT}

    private final Type type;
    private final String domain;
    private final List<String> records;
}
