package com.mail.smtp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/*
CREATE TABLE IF NOT EXISTS tbl_mailbox
(
	f_idx INTEGER NOT NULL AUTO INCREMENT PRIMARY KEY,
	f_aidx INTEGER NOT NULL COMMENT 'f_idx of tbl_user',
	f_name VARCHAR(255) NOT NULL COMMENT 'name of mailbox'
	UNIQUE INDEX UIDX_MAILBOX_01 (f_aidx, f_name),
	INDEX IDX_MAILBOX_01 (f_aidx)
)
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
(
    name = "tbl_mailbox",
    uniqueConstraints =
    {
        @UniqueConstraint(columnNames = {"f_aidx", "f_name"})
    }
)

@Entity
public class MailBoxEntity
{
    /* MailBox Index */
    public static final int MBOX_INDEX_INBOX = 1;
    public static final int MBOX_INDEX_SENT = 2;
    public static final int MBOX_INDEX_SPAM = 3;

    /* MailBox Name */
    public static final String MBOX_NAME_INBOX = "inbox";
    public static final String MBOX_NAME_SENT = "sent";
    public static final String MBOX_NAME_SPAM = "spam";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_idx")
    private Integer idx;

    @Column(name = "f_aidx", columnDefinition = "integer not null")
    private Integer aidx;

    @Column(name = "f_name", columnDefinition = "varchar(255) not null")
    private String name;
}
