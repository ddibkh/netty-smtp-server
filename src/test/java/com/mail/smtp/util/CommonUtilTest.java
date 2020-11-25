package com.mail.smtp.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

class CommonUtilTest
{
    @Test
    void getMyPath()
    {
        String myPath = CommonUtil.getMyPath();
        assertTrue(myPath.equals("D:\\2.workspace\\java_project\\smtp"));
    }

    @Test
    void getParentPath()
    {
        String myPath = CommonUtil.getMyPath();
        myPath += File.separator;
        myPath += "pom.xml";

        File file = new File(myPath);
        assertTrue(file.getParent().equals("D:\\2.workspace\\java_project\\smtp"));
    }

    @Test
    void fileCopy()
    {
        String source = "D:\\2.workspace\\java_project\\smtp\\pom.xml";
        String dest = "D:\\2.workspace\\java_project\\smtp\\temp\\pom.xml";
        CommonUtil.fileCopy(source, dest);
        assertTrue(new File(dest).exists());
    }

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
}