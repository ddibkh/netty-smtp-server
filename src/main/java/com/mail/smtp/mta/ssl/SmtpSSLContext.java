package com.mail.smtp.mta.ssl;

import com.mail.smtp.config.SmtpConfig;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

@Component
@RequiredArgsConstructor
public class SmtpSSLContext
{
    private final SmtpConfig smtpConfig;
    public SslContext sslContext(boolean bStartTls) throws GeneralSecurityException, IOException
    {
        String keyFile = smtpConfig.getString("smtp.cert.path", "keystore.jks");
        String keyPass = smtpConfig.getString("smtp.cert.password", "123456");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try( InputStream in = new FileInputStream(keyFile) )
        {
            keyStore.load(in, keyPass.toCharArray());
        }

        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyPass.toCharArray());

        SslContext sslContext = SslContextBuilder
                .forServer(keyManagerFactory)
                .protocols("TLSv1.2", "TLSv1.3")
                .startTls(bStartTls)
                .build();
        return sslContext;
    }
}
