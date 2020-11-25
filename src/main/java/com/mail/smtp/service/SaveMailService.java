package com.mail.smtp.service;

import com.mail.smtp.config.SmtpConfig;
import com.mail.smtp.data.MailAttribute;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.data.UserVO;
import com.mail.smtp.entity.MailBoxEntity;
import com.mail.smtp.entity.MailListEntity;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.repository.MailBoxRepository;
import com.mail.smtp.repository.MailListRepository;
import com.mail.smtp.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SaveMailService
{
    private final SmtpConfig smtpConfig;
    private final MailBoxRepository mailBoxRepository;
    private final MailListRepository mailListRepository;

    public boolean saveSentBox(SmtpData smtpData, MailAttribute mailAttribute, String tempPath)
    {
        Optional< MailBoxEntity > optBox = mailBoxRepository.findByAidxAndName(smtpData.getMailfrom().getUserIndex(), MailBoxEntity.MBOX_NAME_SENT);
        optBox.orElseThrow(() -> new RuntimeException("can't find user sent box"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date time = new Date();
        String timeDate = simpleDateFormat.format(time);

        String userPath = smtpConfig.getString("smtp.user.path", "");
        String uuid = CommonUtil.makeUID();
        StringBuilder stringBuilder = new StringBuilder();
        if( userPath.equals("") )
            userPath = CommonUtil.getMyPath() + File.separator + "user";

        stringBuilder.append(userPath)
                .append(File.separator)
                .append(smtpData.getMailfrom().getAddressDomain())
                .append(File.separator)
                .append(smtpData.getMailfrom().getAddressId())
                .append(File.separator)
                .append(timeDate)
                .append(File.separator);

        File file = new File(stringBuilder.toString());
        if( !file.exists() )
        {
            if( !file.mkdirs() )
            {
                log.error("fail to save sent box, mkdir failed, {}", stringBuilder.toString());
                return false;
            }
        }

        stringBuilder.append(uuid).append(".eml");

        log.info("try to save sent box, path : {}", stringBuilder.toString());

        if( !CommonUtil.fileCopy(tempPath, stringBuilder.toString()) )
            throw new SmtpException(458);

        MailListEntity mailListEntity = new MailListEntity();
        mailListEntity.setMbox(optBox.get());
        mailListEntity.setUid(smtpData.getRandomUID());
        mailListEntity.setEmlPath(stringBuilder.toString());
        mailListEntity.setEnvFrom(smtpData.getMailfrom().getAddress());
        mailListEntity.setEnvTo(smtpData.getReceipents());
        mailListEntity.setMailAttribute(mailAttribute);

        mailListRepository.save(mailListEntity);

        log.info("success to save sent box, path : {}", stringBuilder.toString());

        return true;
    }

    public boolean saveUserBox(UserVO user, String tempPath, MailAttribute mailAttribute, String boxName)
    {
        final Logger log = LoggerFactory.getLogger("delivery");
        Optional< MailBoxEntity > optBox = mailBoxRepository.findByAidxAndName(user.getUserIndex(), boxName);
        optBox.orElseThrow(() -> new RuntimeException("can't find user sent box"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date time = new Date();
        String timeDate = simpleDateFormat.format(time);

        String userPath = smtpConfig.getString("smtp.user.path", "");
        StringBuilder stringBuilder = new StringBuilder();
        if( userPath.equals("") )
            userPath = CommonUtil.getMyPath() + File.separator + "user";

        stringBuilder.append(userPath)
                .append(File.separator)
                .append(user.getAddressDomain())
                .append(File.separator)
                .append(user.getAddressId())
                .append(File.separator)
                .append(timeDate)
                .append(File.separator);

        File file = new File(stringBuilder.toString());
        if( !file.exists() )
        {
            if( !file.mkdirs() )
            {
                log.error("fail to save user mail box, mkdir failed, {}", stringBuilder.toString());
                return false;
            }
        }

        stringBuilder.append(mailAttribute.getMailUid()).append(".eml");

        log.info("try to user mail box, path : {}", stringBuilder.toString());

        if( !CommonUtil.fileCopy(tempPath, stringBuilder.toString()) )
            throw new SmtpException(458);

        MailListEntity mailListEntity = new MailListEntity();
        mailListEntity.setMbox(optBox.get());
        mailListEntity.setUid(mailAttribute.getMailUid());
        mailListEntity.setEmlPath(stringBuilder.toString());
        mailListEntity.setEnvFrom(mailAttribute.getEnvFrom());
        mailListEntity.setEnvTo(user.getAddress());
        mailListEntity.setMailAttribute(mailAttribute);

        mailListRepository.save(mailListEntity);

        log.info("success to save user box, path : {}", stringBuilder.toString());

        return true;
    }
}
