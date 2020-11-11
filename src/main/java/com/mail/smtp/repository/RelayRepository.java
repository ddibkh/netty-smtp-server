package com.mail.smtp.repository;

import com.mail.smtp.entity.DomainEntity;
import com.mail.smtp.entity.RelayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RelayRepository extends JpaRepository< RelayEntity, Integer >
{
    Optional< List<RelayEntity> > findByDomain(DomainEntity domainEntity);
}
