-- 1. 기존 외래키 제약조건 제거
ALTER TABLE notice_file
    DROP FOREIGN KEY fk_notice_file_abstract_file;

-- 2. abstract_file.file_id 컬럼 BIGINT → VARCHAR(36) 변경
ALTER TABLE abstract_file
    MODIFY COLUMN file_id VARCHAR(36) NOT NULL;

-- 3. PK 제거 후 다시 설정
ALTER TABLE abstract_file
    DROP PRIMARY KEY;

ALTER TABLE abstract_file
    ADD PRIMARY KEY (file_id);

-- 4. notice_file.file_id도 VARCHAR(36)으로 변경
ALTER TABLE notice_file
    MODIFY COLUMN file_id VARCHAR(36) NOT NULL;

-- 5. 외래키 제약조건 다시 추가
ALTER TABLE notice_file
    ADD CONSTRAINT fk_notice_file_abstract_file
        FOREIGN KEY (file_id) REFERENCES abstract_file (file_id);