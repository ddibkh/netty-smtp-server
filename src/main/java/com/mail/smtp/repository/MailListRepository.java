package com.mail.smtp.repository;

import com.mail.smtp.entity.MailBoxEntity;
import com.mail.smtp.entity.MailListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MailListRepository extends CrudRepository< MailListEntity, Integer >
{
    Optional< List<MailListEntity> > findByMbox(MailBoxEntity mbox);
}
