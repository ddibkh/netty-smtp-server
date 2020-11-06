package com.mail.smtp.mta.protocol;

import com.mail.smtp.mta.SmtpData;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProtocolService implements IProtocol
{
    private final Auth auth;
    private final Data data;
    private final Ehlo ehlo;
    private final Helo helo;
    private final MailFrom mailFrom;
    private final RcptTo rcptTo;
    private final Rset rset;
    private final StartTls startTls;
    private final Quit quit;
    private final Noop noop;

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

        command.trim();
        commandData.trim();

        command = command.toUpperCase();

        if (command.length() == 0) {
            ctx.write("500 Error: bad syntax\r\n");
            return;
        }

        switch( command )
        {
            case "Auth":
                auth.process(ctx, smtpData);
                break;
            case "HELO":
                helo.process(ctx);
                break;
            case "EHLO":
                ehlo.process(ctx, smtpData);
                break;
            case "STARTTLS":
                startTls.process(ctx, smtpData);
                break;
            case "MAIL FROM":
                if( commandData.equals("") )
                    ctx.write("500 Error: bad syntax\r\n");
                mailFrom.process(ctx, smtpData, commandData);
                break;
            case "NOOP":
                noop.process(ctx);
                break;
            case "RCPT TO":
                if( commandData.equals("") )
                    ctx.write("500 Error: bad syntax\r\n");
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
                ctx.write("500 unrecognized command\r\n");
                break;
        }
    }
}
