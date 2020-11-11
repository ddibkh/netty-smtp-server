package com.mail.smtp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/*
CREATE TABLE IF NOT EXISTS tbl_relay
(
    f_idx INTEGER NOT NULL AUTH INCREMENT PRIMARY KEY,
    f_didx INTEGER NOT NULL COMMENT 'f_idx of tbl_domain',
    f_type INTEGER NOT NULL DEFAULT 0 COMMENT '0 : ip, 1 : subnet',
    f_ip VARCHAR(255) NOT NULL,
    UNIQUE INDEX UIDX_RELAY_01 (f_didx, f_ip)
);
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "tbl_relay")
public class RelayEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_idx")
    private Integer idx;

    @ManyToOne
    @JoinColumn(name = "f_didx", columnDefinition = "integer not null")
    private DomainEntity domain;

    //0 : 단일 IP, 1 : 서브넷 마스크
    @Column(name = "f_type", columnDefinition = "integer not null default 0")
    @Min(0) @Max(1)
    private Integer type;

    @Column(name = "f_ip", columnDefinition = "varchar(255) not null")
    private String ip;
}
