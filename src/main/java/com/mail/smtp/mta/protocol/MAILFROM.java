package com.mail.smtp.mta.protocol;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.entity.DomainEntity;
import com.mail.smtp.entity.UserEntity;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.mta.data.SmtpData;
import com.mail.smtp.mta.data.UserVO;
import com.mail.smtp.mta.handler.SmtpMsaServerHandler;
import com.mail.smtp.repository.DomainRepository;
import com.mail.smtp.repository.UserRepository;
import com.mail.smtp.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class MAILFROM
{
    private final UserService userService;
    private final SmtpConfig smtpConfig;

    public void process(ChannelHandlerContext ctx, SmtpData smtpData, String commandData)
    {
        if( commandData.equals("<>") && smtpConfig.getInt("smtp.allow.blank.sender", 0) == 1 )
        {
            UserVO userVO = new UserVO();
            smtpData.setMailfrom(userVO);
            smtpData.setBlankSender(true);
            String msg = "250 <> Sender OK\r\n";
            ctx.writeAndFlush(msg);
            return;
        }

        //587(submission port 접속시 인증을 받아야 함)
        if( ctx.handler() instanceof SmtpMsaServerHandler )
        {
            if( !smtpData.isAuthed() )
                throw new SmtpException(530);
        }

        UserVO userVO = userService.fetchUserVO(commandData);
        smtpData.setMailfrom(userVO);
        String msg = "250 " + userVO.getAddress() + " ... Sender OK\r\n";
        ctx.writeAndFlush(msg);
    }
}
