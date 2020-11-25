package com.mail.smtp.mta.protocol;

import com.mail.smtp.exception.RelayException;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.data.UserVO;
import com.mail.smtp.service.RelayService;
import com.mail.smtp.service.UserService;
import com.mail.smtp.util.CommonUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class RCPTTO
{
    private final UserService userService;
    private final RelayService relayService;

    public void process(ChannelHandlerContext ctx, SmtpData smtpData, String commandData)
    {
        Optional< UserVO > optMailFrom = Optional.ofNullable(smtpData.getMailfrom());
        //MAIL FROM 을 거치지 않으면 sequence command exception 발생.
        optMailFrom.orElseThrow(() -> new SmtpException(503));

        UserVO userVO = userService.fetchUserVO(commandData);

        //deny remote -> remote
        if( !smtpData.getMailfrom().isLocal() && !userVO.isLocal() )
            throw new SmtpException(551);

        String localIP;
        try
        {
            localIP = CommonUtil.getLocalIP();
        }
        catch( UnknownHostException uhe )
        {
            localIP = "127.0.0.1";
        }

        //로컬에서 발송한 경우는 허용.
        if( !smtpData.getClientIP().equals("127.0.0.1") &&
            !smtpData.getClientIP().equals(localIP) )
        {
            //수신자 로컬 수신인 경우 체크 없이 수신한다.
            //송신자 로컬, 수신자 리모트인 경우 인증 및 relay check
            if( smtpData.getMailfrom().isLocal() && !smtpData.isAuthed() && !userVO.isLocal() )
            {
                //check relay
                boolean relayAllowed =
                        relayService.relayAllowed(smtpData.getMailfrom().getDomainIndex(), smtpData.getClientIP());

                if( !relayAllowed )
                    //오류코드 다시 정리 필요. subcode 까지 고려.
                    throw new RelayException(smtpData.getClientIP());
            }
        }

        smtpData.addReceipent(userVO);
        String msg = "250 " + userVO.getAddress() + " ... Receipient OK\r\n";
        ctx.writeAndFlush(msg);
    }
}
