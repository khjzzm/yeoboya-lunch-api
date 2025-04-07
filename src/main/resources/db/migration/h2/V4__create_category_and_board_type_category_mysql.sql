-- 카테고리 테이블
CREATE TABLE category (
                          CATEGORY_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(20) NOT NULL UNIQUE,
                          description VARCHAR(100)
);

-- 게시판 유형별 카테고리 매핑 테이블
CREATE TABLE board_type_category (
                                     BOARD_TYPE_CATEGORY_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     board_type VARCHAR(30) NOT NULL,
                                     category_id BIGINT NOT NULL,
                                     CONSTRAINT fk_board_type_category_category FOREIGN KEY (category_id) REFERENCES category(CATEGORY_ID)
);

-- 카테고리 SEED 데이터
INSERT INTO category (name, description) VALUES
                                             ('자유', '자유롭게 이야기해요'),
                                             ('정보', '유용한 정보를 공유해요'),
                                             ('질문', '궁금한 걸 물어보세요'),
                                             ('이벤트', '이벤트 소식을 전해요'),
                                             ('고충상담', '익명으로 고민을 나눠요');

-- 게시판 유형별 허용 카테고리 설정
-- FREE 게시판: 자유, 정보, 질문, 이벤트
INSERT INTO board_type_category (board_type, category_id)
SELECT 'FREE', CATEGORY_ID FROM category WHERE name IN ('자유', '정보', '질문', '이벤트');

-- ANON 게시판: 고충상담
INSERT INTO board_type_category (board_type, category_id)
SELECT 'ANON', CATEGORY_ID FROM category WHERE name IN ('고충상담');

-- QNA 게시판: 질문
INSERT INTO board_type_category (board_type, category_id)
SELECT 'QNA', CATEGORY_ID FROM category WHERE name IN ('질문');