package com.mail.smtp.repository;

import com.mail.smtp.entity.MailBoxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MailBoxRepository extends JpaRepository< MailBoxEntity, Integer >
{
    Optional<MailBoxEntity> findByAidxAndName(Integer aidx, String name);
}
