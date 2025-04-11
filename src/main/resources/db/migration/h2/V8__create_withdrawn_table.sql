CREATE TABLE withdrawn_member (
                                  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                  login_id VARCHAR(100) NOT NULL,
                                  email VARCHAR(255) NOT NULL,
                                  provider VARCHAR(50),
                                  reason TEXT,
                                  withdrawn_at DATETIME NOT NULL
);