package com.mail.smtp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/*
CREATE TABLE IF NOT EXISTS `tbl_user`
(
	f_idx INTEGER NOT NULL AUTO INCREMENT PRIMARY KEY,
	f_id VARCHAR(255) NOT NULL,
	f_didx INTEGER NOT NULL COMMENT 'f_idx of tbl_domain',
	f_pwd VARCHAR(255) NOT NULL COMMENT 'auth password',
	f_pidx INTEGER NOT NULL COMMENT 'f_idx of tbl_path',
	CONSTRAINT UIDX_USER_01 UNIQUE (f_didx, f_id)
)
*/

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table
(
    name = "tbl_user",
    uniqueConstraints =
    {
        @UniqueConstraint(columnNames = {"f_didx", "f_id"})
    }
)
@Entity
@DynamicUpdate
@Builder
public class UserEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_idx")
    private Integer idx;

    @Column(name = "f_id", columnDefinition = "varchar(255) not null")
    private String userid;

    @OneToOne
    @JoinColumn(name = "f_didx", columnDefinition = "integer not null")
    private DomainEntity domain;

    @Column(name = "f_pwd", columnDefinition = "varchar(255) not null")
    private String userPwd;

    @OneToOne
    @JoinColumn(name = "f_pidx", columnDefinition = "integer not null")
    private PathEntity path;
}
