package com.mail.smtp.entity;

import com.mail.smtp.converter.MailAttributeConverter;
import com.mail.smtp.data.MailAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/*
CREATE TABLE IF NOT EXISTS tbl_maillist
(
	f_idx INTEGER NOT NULL AUTO INCREMENT PRIMARY KEY,
	f_midx INTEGER NOT NULL COMMENT 'f_idx of tbl_mailbox',
	f_mailinfo JSON NOT NULL COMMENT 'information of mail',
	INDEX IDX_MAILLIST_01 (f_midx)
)
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "tbl_maillist")
@Entity
//@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class MailListEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_idx")
    private Integer idx;

    @ManyToOne
    @JoinColumn(name = "f_midx", columnDefinition = "integer not null")
    private MailBoxEntity mbox;

    @Column(name = "f_uid", columnDefinition = "varchar(64) not null")
    private String uid;

    @Column(name = "f_emlpath", columnDefinition = "varchar(255) not null")
    private String emlPath;

    @Column(name = "f_envfrom", columnDefinition = "varchar(255)")
    private String envFrom;

    @Column(name = "f_envto", columnDefinition = "varchar(4096)")
    private String envTo;

    @Convert(converter = MailAttributeConverter.class)
    @Column(name = "mail_attribute", columnDefinition = "CLOB not null")
    private MailAttribute mailAttribute;
}
