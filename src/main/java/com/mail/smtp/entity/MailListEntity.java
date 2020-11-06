package com.mail.smtp.entity;

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
public class MailListEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_idx")
    private Integer idx;

    @ManyToOne
    @JoinColumn(name = "f_midx", columnDefinition = "integer not null")
    private MailBoxEntity mbox;

    @Convert(converter = MailAttributeConverter.class)
    private MailAttribute mailAttribute;
}
