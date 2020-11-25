package com.mail.smtp.mta.protocol;

import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.data.SmtpData;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProtocolService implements IProtocol
{
    private final AUTH auth;
    private final DATA data;
    private final EHLO ehlo;
    private final HELO helo;
    private final MAILFROM mailFrom;
    private final RCPTTO rcptTo;
    private final RSET rset;
    private final STARTTLS startTls;
    private final QUIT quit;
    private final NOOP noop;

    @Override
    public void process(ChannelHandlerContext ctx, String message, SmtpData smtpData)
    {
        String command;
        String commandData;

        int i = message.indexOf(":");
        if( i > 0 )
        {
            command = message.substring(0, i);
            commandData = message.substring(i + 1);
        }
        else
        {
            i = message.indexOf(" ");
            if (i > 0)
            {
                command = message.substring(0, i).toUpperCase();
                commandData = message.substring(i + 1);
            }
            else
            {
                command = message.toUpperCase();
                commandData = "";
            }
        }

        command = command.trim();

        if (command.length() == 0)
            throw new SmtpException(500);

        commandData = commandData.trim();
        command = command.toUpperCase();

        switch( command )
        {
            case "AUTH":
                auth.process(ctx, smtpData, commandData);
                break;
            case "HELO":
                helo.process(ctx, smtpData, commandData);
                break;
            case "EHLO":
                ehlo.process(ctx, smtpData, commandData);
                break;
            case "STARTTLS":
                startTls.process(ctx, smtpData);
                break;
            case "MAIL FROM":
                if( commandData.equals("") )
                    throw new SmtpException(501);
                mailFrom.process(ctx, smtpData, commandData);
                break;
            case "NOOP":
                noop.process(ctx);
                break;
            case "RCPT TO":
                if( commandData.equals("") )
                    throw new SmtpException(501);
                rcptTo.process(ctx, smtpData, commandData);
                break;
            case "DATA":
                data.process(ctx, smtpData);
                break;
            case "RSET":
                rset.process(ctx, smtpData);
                break;
            case "QUIT":
                quit.process(ctx);
                break;
            default:
                log.error("unrecognized command : {}", command);
                throw new SmtpException(500);
        }
    }
}
