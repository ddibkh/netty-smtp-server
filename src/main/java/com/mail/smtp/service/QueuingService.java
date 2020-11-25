package com.mail.smtp.service;

import com.mail.smtp.qmessage.JmsSender;
import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.data.*;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueuingService
{
    private final SmtpConfig smtpConfig;
    private final JmsSender jmsSender;

    public boolean queuing(SmtpData smtpData, MailAttribute mailAttribute, String tempPath) throws SecurityException
    {
        //queuing
        Optional< List< UserVO > > optReceivers = Optional.ofNullable(smtpData.getListRcptTo());
        optReceivers.ifPresentOrElse((userVOList) -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date time = new Date();
            String timeDate = simpleDateFormat.format(time);

            //queuing
            String queuePath = smtpConfig.getString("smtp.queue.path", "");
            if( queuePath.equals("") )
                queuePath = CommonUtil.getMyPath() + File.separator + "queue";

            queuePath += File.separator;
            queuePath += timeDate;
            queuePath += File.separator;

            File file = new File(queuePath);
            if( !file.exists() )
            {
                if( !file.mkdirs() )
                {
                    log.error("fail to queueing, mkdir failed, {}", queuePath);
                    throw new SmtpException(458);
                }
            }

            String strEmlName = queuePath + smtpData.getRandomUID() + ".eml";

            log.info("try to queuing, path : {}", strEmlName);

            if( !CommonUtil.fileCopy(tempPath, strEmlName) )
                throw new SmtpException(458);

            List<String> toLocal = smtpData.getListRcptTo().stream().
                    filter(UserVO::isLocal).map(UserVO::getAddress).collect(Collectors.toList());

            List<String> toRemote = smtpData.getListRcptTo().stream().
                    filter(userVO -> !userVO.isLocal()).map(UserVO::getAddress).collect(Collectors.toList());

            QueueData queueData = new QueueData(
                    smtpData.getRandomUID(),
                    smtpData.getMailfrom().getAddress(),
                    toLocal,
                    toRemote,
                    strEmlName,
                    mailAttribute
            );
            jmsSender.sendMessageQueue(queueData);
            log.info("success to queuing");
        }, () -> log.info("skip to queuing not exist receipients"));

        return true;
    }
}
