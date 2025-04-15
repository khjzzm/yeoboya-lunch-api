CREATE TABLE anonymous_board
(
    board_id       BIGINT NOT NULL PRIMARY KEY,
    nickname       VARCHAR(255),
    writer_ip_hash VARCHAR(255),
    delete_at      DATETIME,
    password_hash  VARCHAR(255),
    report_count   INT DEFAULT 0
);