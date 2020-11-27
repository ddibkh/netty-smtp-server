package com.mail.smtp.delivery;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.exception.DeliveryException;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class SendMail
{
    private final Logger log = LoggerFactory.getLogger("delivery");
    private final SmtpConfig smtpConfig;

    public void send(@NonNull String envFrom,
                     @NonNull Address[] to,
                     @NonNull String smtpHost,
                     @NonNull String mimeFilePath) throws DeliveryException
    {
        //1 : use envelope from in mimemessage
        boolean bUseEnvFromInMimeMessage = smtpConfig.getInt("smtp.from.mimemessage", 0) == 1;
        //1 : use send ssl transport
        boolean bSecure = smtpConfig.getInt("smtp.delivery.secure", 0) == 1;
        //1 : print transport flow
        boolean bDebug = smtpConfig.getInt("smtp.delivery.debug", 0) == 1;

        Properties props = getProperties(bSecure, bUseEnvFromInMimeMessage, bDebug, smtpHost, envFrom);

        //매번 새로운 세션을 생성해야 하기 때문에 getInstance 호출해야 한다.
        // getDefaultInstance 를 호출하면 이전에 생성된 세션이 적용된다.
        Session session = Session.getInstance(props);

        try(
                InputStream inputStream = new FileInputStream(mimeFilePath)
        )
        {
            MimeMessage message = new MimeMessage(session, inputStream);
            Transport.send(message, to);
        }
        catch( IOException e )
        {
            throw new DeliveryException("io exception, " + e.getMessage());
        }
        catch( SendFailedException sfe )
        {
            throw new DeliveryException("send fail exception, " + sfe.getMessage());
        }
        catch( MessagingException me )
        {
            //보안 연결 실패시 일반포트로 재시도.
            if( bSecure )
            {
                log.error("fail to delivery by secure mode, {}", me.getMessage());
                log.info("retry normal mode");
                resendNormal(bDebug, envFrom, to, smtpHost, mimeFilePath);
            }
            else
                throw new DeliveryException("messaging exceptoin, " + me.getMessage());
        }
    }

    private void resendNormal(boolean bDebug,
                              @NonNull String envFrom,
                              @NonNull Address[] to,
                              @NonNull String smtpHost,
                              @NonNull String mimeFilePath
                              ) throws DeliveryException
    {
        //1 : use envelope from in mimemessage
        boolean bUseEnvFromInMimeMessage = smtpConfig.getInt("smtp.from.mimemessage", 0) == 1;

        Properties props = getProperties(false, bUseEnvFromInMimeMessage, bDebug, smtpHost, envFrom);
        Session session = Session.getInstance(props);

        try(
                InputStream inputStream = new FileInputStream(mimeFilePath)
        )
        {
            MimeMessage message = new MimeMessage(session, inputStream);
            Transport.send(message, to);
        }
        catch( IOException e )
        {
            throw new DeliveryException("io exception, " + e.getMessage());
        }
        catch( SendFailedException sfe )
        {
            throw new DeliveryException("send fail exception, " + sfe.getMessage());
        }
        catch( MessagingException me )
        {
            throw new DeliveryException("messaging exceptoin, " + me.getMessage());
        }
    }

    private Properties getProperties(boolean bSecure,
                                     boolean bUseMimeFrom,
                                     boolean bDebug,
                                     String smtpHost,
                                     String envFrom)
    {
        Properties props = new Properties();

        //추후 확인.
        if( bSecure )
        {
            MailSSLSocketFactory socketFactory;
            try
            {
                //수신 서버의 SSL 인증서가 공인된 인증서가 아닌 경우(사설인증서) 에도 정상적으로 전송하기 위함.
                socketFactory = new MailSSLSocketFactory();
                socketFactory.setTrustAllHosts(true);

                props.put("mail.transport.protocol.rfc822", "smtps");
                props.put("mail.smtps.socketFactory", socketFactory);
                props.put("mail.smtps.host", smtpHost);
                if( !bUseMimeFrom )
                    props.put("mail.smtps.from", envFrom);
            }
            catch( GeneralSecurityException e )
            {
                log.error("fail to set ssl trust all host certification, so normal port transport");
                Properties propsNormal = new Properties();
                propsNormal.put("mail.transport.protocol.rfc822", "smtp");
                propsNormal.put("mail.smtp.host", smtpHost);
                if( !bUseMimeFrom )
                    propsNormal.put("mail.smtp.from", envFrom);

                return propsNormal;
            }
        }
        else
        {
            props.put("mail.transport.protocol.rfc822", "smtp");
            props.put("mail.smtp.host", smtpHost);
            if( !bUseMimeFrom )
                props.put("mail.smtp.from", envFrom);
        }

        if( bDebug )
            props.put("mail.debug", "true");

        return props;
    }
}
