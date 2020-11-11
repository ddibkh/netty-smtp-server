package com.mail.smtp.repository;

import com.mail.smtp.entity.DomainEntity;
import com.mail.smtp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//simple repository for UserEntity
public interface UserRepository extends JpaRepository< UserEntity, Integer >
{
    Optional<UserEntity> findByUserid(String userId);
    Optional<UserEntity> findByUseridAndDomain(String userId, DomainEntity domain);
}
