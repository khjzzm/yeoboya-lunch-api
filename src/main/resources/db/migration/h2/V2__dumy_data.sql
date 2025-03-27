-- MEMBER + ROLE + PROVIDER
INSERT INTO member (member_id, login_id, email, name, password, provider, provider_id, role_roles_id, created_date, last_modified_date)
VALUES
    (101, 'admin', 'admin@test.com', '관리자', '$2b$12$e.8T6TE/.Ax0Ob4g2TkUhO4y4WcI/zxiHgsIhuXOp/wHdUWGIbjLW', 'yeoboya', '101', 1, NOW(), NOW()),
    (102, 'manager', 'manager@test.com', '매니저1', '$2b$12$e.8T6TE/.Ax0Ob4g2TkUhO4y4WcI/zxiHgsIhuXOp/wHdUWGIbjLW', 'kakao', '102', 2, NOW(), NOW()),
    (103, 'user', 'user@test.com', '유저1', '$2b$12$e.8T6TE/.Ax0Ob4g2TkUhO4y4WcI/zxiHgsIhuXOp/wHdUWGIbjLW', 'naver', '103', 3, NOW(), NOW()),
    (104, 'guest01', 'guest01@test.com', '게스트1', 'guest_pass', 'google', '104', 4, NOW(), NOW()),
    (105, 'block01', 'block01@test.com', '차단1', 'block_pass', 'facebook', '105', 5, NOW(), NOW()),
    (106, 'user02', 'user02@test.com', '유저2', 'user_pass', 'google', '106', 3, NOW(), NOW()),
    (107, 'user03', 'user03@test.com', '유저3', 'user_pass', 'google', '107', 3, NOW(), NOW()),
    (108, 'user04', 'user04@test.com', '유저4', 'user_pass', 'google', '108', 3, NOW(), NOW()),
    (109, 'user05', 'user05@test.com', '유저5', 'user_pass', 'google', '109', 3, NOW(), NOW()),
    (110, 'guest02', 'guest02@test.com', '게스트2', 'guest_pass', 'github', '110', 4, NOW(), NOW());

-- USER_SECURITY_STATUS
INSERT INTO user_security_status (user_security_status_id, is_account_non_expired, is_account_non_locked, is_credentials_non_expired, is_enabled, member_id)
VALUES
    (201, true, true, true, true, 101),
    (202, true, true, true, true, 102),
    (203, true, true, true, true, 103),
    (204, true, true, true, true, 104),
    (205, true, false, true, true, 105),  -- 차단 사용자
    (206, true, true, true, true, 106),
    (207, true, true, true, true, 107),
    (208, true, true, true, true, 108),
    (209, true, true, true, true, 109),
    (210, true, true, true, true, 110);

-- MEMBER_INFO
INSERT INTO member_info (member_info_id, bio, nick_name, phone_number, member_id)
VALUES
    (301, '관리자 소개', 'admin_nick1', '010-0000-0001', 101),
    (302, '매니저 소개', 'manager_nick1', '010-0000-0002', 102),
    (303, '유저 소개1', 'user_nick1', '010-0000-0003', 103),
    (304, '게스트 소개1', 'guest_nick1', '010-0000-0004', 104),
    (305, '차단 소개1', 'block_nick1', '010-0000-0005', 105),
    (306, '유저 소개2', 'user_nick2', '010-0000-0006', 106),
    (307, '유저 소개3', 'user_nick3', '010-0000-0007', 107),
    (308, '유저 소개4', 'user_nick4', '010-0000-0008', 108),
    (309, '유저 소개5', 'user_nick5', '010-0000-0009', 109),
    (310, '게스트 소개2', 'guest_nick2', '010-0000-0010', 110);

-- ACCOUNT (선택)
INSERT INTO account (account_id, account_number, bank_name, member_id, created_date, last_modified_date)
VALUES
    (401, '100-000-0001', '카카오뱅크', 101, NOW(), NOW()),
    (402, '100-000-0002', '신한은행', 102, NOW(), NOW()),
    (403, '100-000-0003', '국민은행', 103, NOW(), NOW()),
    (404, '100-000-0004', '우리은행', 104, NOW(), NOW()),
    (405, '100-000-0005', '하나은행', 105, NOW(), NOW());


-- BOARD_ID는 1000번부터 시작 (예시)
INSERT INTO ABSTRACT_BOARD (BOARD_ID, CREATED_BY, LAST_MODIFIED_BY, CONTENT, TITLE, BOARD_TYPE, CREATED_DATE, LAST_MODIFIED_DATE, VIEW_COUNT)
VALUES
    (1000, 'admin', 'admin', '공지 내용입니다 - 1', '공지사항 제목 - 1', 'NOTICE', NOW(), NOW(), 12),
    (1001, 'admin', 'admin', '공지 내용입니다 - 2', '공지사항 제목 - 2', 'NOTICE', NOW(), NOW(), 5),
    (1002, 'admin', 'admin', '공지 내용입니다 - 3', '공지사항 제목 - 3', 'NOTICE', NOW(), NOW(), 33),
    (1003, 'admin', 'admin', '공지 내용입니다 - 4', '공지사항 제목 - 4', 'NOTICE', NOW(), NOW(), 19),
    (1004, 'admin', 'admin', '공지 내용입니다 - 5', '공지사항 제목 - 5', 'NOTICE', NOW(), NOW(), 22),
    (1005, 'admin', 'admin', '공지 내용입니다 - 6', '공지사항 제목 - 6', 'NOTICE', NOW(), NOW(), 17),
    (1006, 'admin', 'admin', '공지 내용입니다 - 7', '공지사항 제목 - 7', 'NOTICE', NOW(), NOW(), 25),
    (1007, 'admin', 'admin', '공지 내용입니다 - 8', '공지사항 제목 - 8', 'NOTICE', NOW(), NOW(), 10),
    (1008, 'admin', 'admin', '공지 내용입니다 - 9', '공지사항 제목 - 9', 'NOTICE', NOW(), NOW(), 45),
    (1009, 'admin', 'admin', '공지 내용입니다 - 10', '공지사항 제목 - 10', 'NOTICE', NOW(), NOW(), 18),
    (1010, 'admin', 'admin', '공지 내용입니다 - 11', '공지사항 제목 - 11', 'NOTICE', NOW(), NOW(), 21),
    (1011, 'admin', 'admin', '공지 내용입니다 - 12', '공지사항 제목 - 12', 'NOTICE', NOW(), NOW(), 27),
    (1012, 'admin', 'admin', '공지 내용입니다 - 13', '공지사항 제목 - 13', 'NOTICE', NOW(), NOW(), 36),
    (1013, 'admin', 'admin', '공지 내용입니다 - 14', '공지사항 제목 - 14', 'NOTICE', NOW(), NOW(), 15),
    (1014, 'admin', 'admin', '공지 내용입니다 - 15', '공지사항 제목 - 15', 'NOTICE', NOW(), NOW(), 40),
    (1015, 'admin', 'admin', '공지 내용입니다 - 16', '공지사항 제목 - 16', 'NOTICE', NOW(), NOW(), 11),
    (1016, 'admin', 'admin', '공지 내용입니다 - 17', '공지사항 제목 - 17', 'NOTICE', NOW(), NOW(), 13),
    (1017, 'admin', 'admin', '공지 내용입니다 - 18', '공지사항 제목 - 18', 'NOTICE', NOW(), NOW(), 8),
    (1018, 'admin', 'admin', '공지 내용입니다 - 19', '공지사항 제목 - 19', 'NOTICE', NOW(), NOW(), 6),
    (1019, 'admin', 'admin', '공지 내용입니다 - 20', '공지사항 제목 - 20', 'NOTICE', NOW(), NOW(), 9);

INSERT INTO NOTICE (ATTACHMENT_URL, AUTHOR, CATEGORY, END_DATE, PRIORITY, START_DATE, STATUS, BOARD_ID)
VALUES
    ('https://example.com/1', 'admin', '일반', NULL, 1, NOW(), 'ACTIVE', 1000),
    ('https://example.com/2', 'admin', '일반', NULL, 0, NOW(), 'ACTIVE', 1001),
    ('https://example.com/3', 'admin', '긴급', NULL, 2, NOW(), 'ACTIVE', 1002),
    ('https://example.com/4', 'admin', '일반', NULL, 1, NOW(), 'ACTIVE', 1003),
    ('https://example.com/5', 'admin', '점검', NULL, 1, NOW(), 'INACTIVE', 1004),
    ('https://example.com/6', 'admin', '일반', NULL, 0, NOW(), 'ACTIVE', 1005),
    ('https://example.com/7', 'admin', '일반', NULL, 2, NOW(), 'ACTIVE', 1006),
    ('https://example.com/8', 'admin', '점검', NULL, 1, NOW(), 'INACTIVE', 1007),
    ('https://example.com/9', 'admin', '일반', NULL, 2, NOW(), 'ACTIVE', 1008),
    ('https://example.com/10', 'admin', '일반', NULL, 0, NOW(), 'ACTIVE', 1009),
    ('https://example.com/11', 'admin', '일반', NULL, 1, NOW(), 'ACTIVE', 1010),
    ('https://example.com/12', 'admin', '긴급', NULL, 2, NOW(), 'ACTIVE', 1011),
    ('https://example.com/13', 'admin', '일반', NULL, 0, NOW(), 'ACTIVE', 1012),
    ('https://example.com/14', 'admin', '점검', NULL, 1, NOW(), 'INACTIVE', 1013),
    ('https://example.com/15', 'admin', '일반', NULL, 2, NOW(), 'ACTIVE', 1014),
    ('https://example.com/16', 'admin', '일반', NULL, 0, NOW(), 'ACTIVE', 1015),
    ('https://example.com/17', 'admin', '긴급', NULL, 1, NOW(), 'ACTIVE', 1016),
    ('https://example.com/18', 'admin', '일반', NULL, 2, NOW(), 'ACTIVE', 1017),
    ('https://example.com/19', 'admin', '일반', NULL, 1, NOW(), 'ACTIVE', 1018),
    ('https://example.com/20', 'admin', '점검', NULL, 0, NOW(), 'ACTIVE', 1019);

