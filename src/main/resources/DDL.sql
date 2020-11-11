-- simple smtp database schema

CREATE TABLE IF NOT EXISTS tbl_user
(
	f_idx INTEGER NOT NULL AUTO INCREMENT PRIMARY KEY,
	f_id VARCHAR(255) NOT NULL,
	f_didx INTEGER NOT NULL COMMENT 'f_idx of tbl_domain',
	f_pwd VARCHAR(255) NOT NULL COMMENT 'auth password',
	f_pidx INTEGER NOT NULL COMMENT 'f_idx of tbl_path',
	UNIQUE INDEX UIDX_USER_01 (f_didx, f_id)
);


CREATE TABLE IF NOT EXISTS tbl_domain
(
	f_idx INTEGER NOT NULL AUTO INCREMENT PRIMARY KEY,
	f_name VARCHAR(255) NOT NULL COMMENT 'domain name',
	f_status INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tbl_path
(
	f_idx INTEGER NOT NULL AUTO INCREMENT PRIMARY KEY,
	f_path VARCHAR(255) NOT NULL COMMENT 'user path',
	UNIQUE INDEX UIDX_PATH_01 (f_path)
);

CREATE TABLE IF NOT EXISTS tbl_mailbox
(
	f_idx INTEGER NOT NULL AUTO INCREMENT PRIMARY KEY,
	f_aidx INTEGER NOT NULL COMMENT 'f_idx of tbl_user',
	f_name VARCHAR(255) NOT NULL COMMENT 'name of mailbox'
	UNIQUE INDEX UIDX_MAILBOX_01 (f_aidx, f_name),
	INDEX IDX_MAILBOX_01 (f_aidx)
);

CREATE TABLE IF NOT EXISTS tbl_maillist
(
	f_idx INTEGER NOT NULL AUTO INCREMENT PRIMARY KEY,
	f_midx INTEGER NOT NULL COMMENT 'f_idx of tbl_mailbox',
	f_mailinfo JSON NOT NULL COMMENT 'information of mail',
	INDEX IDX_MAILLIST_01 (f_midx)
);

CREATE TABLE IF NOT EXISTS tbl_relay
(
    f_idx INTEGER NOT NULL AUTH INCREMENT PRIMARY KEY,
    f_didx INTEGER NOT NULL COMMENT 'f_idx of tbl_domain',
    f_type INTEGER NOT NULL DEFAULT 0 COMMENT '0 : ip, 1 : subnet',
    f_ip VARCHAR(255) NOT NULL,
    UNIQUE INDEX UIDX_RELAY_01 (f_didx, f_ip)
);
