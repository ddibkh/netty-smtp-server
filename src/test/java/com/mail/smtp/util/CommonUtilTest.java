package com.mail.smtp.util;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

class CommonUtilTest
{
    @Test
    void grouping()
    {
        List<String> to = Arrays.asList("test1@test.com",
                "a@a.com",
                "b@b.com",
                "c@c.com",
                "d@d.com",
                "e@c.com",
                "f@c.com");
        Map<String, List<String> > domainGroup =
                to.stream().collect(Collectors.groupingBy(address -> {
                    int index = address.indexOf('@');
                    return address.substring(index + 1);
                }));

        System.out.println(domainGroup.toString());
    }

    @Test
    void dateFormat()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        System.out.println(formatter.format(new Date()));
    }
}