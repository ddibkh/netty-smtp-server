package com.mail.smtp.util;

import java.io.IOException;

public interface IConfigLoader
{
    ConfigMap load(String filePath) throws IOException;
}
