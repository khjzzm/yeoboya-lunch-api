-- V5: Add CATEGORY_ID to abstract_board and foreign key constraint (H2 version)

-- 1. CATEGORY_ID 컬럼 추가
ALTER TABLE abstract_board
    ADD COLUMN CATEGORY_ID BIGINT;

-- 2. FK 제약조건 추가 (H2는 이름 필수)
ALTER TABLE abstract_board
    ADD CONSTRAINT FK_ABSTRACT_BOARD_CATEGORY
        FOREIGN KEY (CATEGORY_ID) REFERENCES category(CATEGORY_ID);