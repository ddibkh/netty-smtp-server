package com.mail.smtp.repository;

import com.mail.smtp.entity.DomainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DomainRepository extends JpaRepository< DomainEntity, Integer >
{
    Optional<DomainEntity> findByDomainName(String domainName);
}
