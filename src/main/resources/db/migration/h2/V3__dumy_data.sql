-- MEMBER + ROLE + PROVIDER
INSERT INTO member (member_id, login_id, email, name, password, provider, provider_id, role_roles_id, created_date,
                    last_modified_date)
VALUES (101, 'admin', 'admin@test.com', '관리자', '$2b$12$e.8T6TE/.Ax0Ob4g2TkUhO4y4WcI/zxiHgsIhuXOp/wHdUWGIbjLW',
        'yeoboya', '101', 1, NOW(), NOW()),
       (102, 'manager', 'manager@test.com', '매니저1', '$2b$12$e.8T6TE/.Ax0Ob4g2TkUhO4y4WcI/zxiHgsIhuXOp/wHdUWGIbjLW',
        'kakao', '102', 2, NOW(), NOW()),
       (103, 'user', 'user@test.com', '유저1', '$2b$12$e.8T6TE/.Ax0Ob4g2TkUhO4y4WcI/zxiHgsIhuXOp/wHdUWGIbjLW', 'naver',
        '103', 3, NOW(), NOW()),
       (104, 'guest01', 'guest01@test.com', '게스트1', 'guest_pass', 'google', '104', 4, NOW(), NOW()),
       (105, 'block01', 'block01@test.com', '차단1', 'block_pass', 'facebook', '105', 5, NOW(), NOW()),
       (106, 'user02', 'user02@test.com', '유저2', 'user_pass', 'google', '106', 3, NOW(), NOW()),
       (107, 'user03', 'user03@test.com', '유저3', 'user_pass', 'google', '107', 3, NOW(), NOW()),
       (108, 'user04', 'user04@test.com', '유저4', 'user_pass', 'google', '108', 3, NOW(), NOW()),
       (109, 'user05', 'user05@test.com', '유저5', 'user_pass', 'google', '109', 3, NOW(), NOW()),
       (110, 'guest02', 'guest02@test.com', '게스트2', 'guest_pass', 'github', '110', 4, NOW(), NOW());

-- USER_SECURITY_STATUS
INSERT INTO user_security_status (user_security_status_id, is_account_non_expired, is_account_non_locked,
                                  is_credentials_non_expired, is_enabled, member_id)
VALUES (201, true, true, true, true, 101),
       (202, true, true, true, true, 102),
       (203, true, true, true, true, 103),
       (204, true, true, true, true, 104),
       (205, true, false, true, true, 105), -- 차단 사용자
       (206, true, true, true, true, 106),
       (207, true, true, true, true, 107),
       (208, true, true, true, true, 108),
       (209, true, true, true, true, 109),
       (210, true, true, true, true, 110);

-- MEMBER_INFO
INSERT INTO member_info (member_info_id, bio, nick_name, phone_number, member_id)
VALUES (301, '관리자 소개', 'admin_nick1', '010-0000-0001', 101),
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
VALUES (401, '100-000-0001', '카카오뱅크', 101, NOW(), NOW()),
       (402, '100-000-0002', '신한은행', 102, NOW(), NOW()),
       (403, '100-000-0003', '국민은행', 103, NOW(), NOW()),
       (404, '100-000-0004', '우리은행', 104, NOW(), NOW()),
       (405, '100-000-0005', '하나은행', 105, NOW(), NOW());


-- BOARD_ID는 1000번부터 시작 (예시)
-- ABSTRACT_BOARD (공지사항 본문)
INSERT INTO ABSTRACT_BOARD (BOARD_ID, CREATED_BY, LAST_MODIFIED_BY, CONTENT, TITLE, BOARD_TYPE, CREATED_DATE, LAST_MODIFIED_DATE, VIEW_COUNT)
VALUES
    (1000, 'admin', 'admin', '이번 주 식단표를 안내드립니다. 즐거운 점심 시간 되세요!', '3월 마지막 주 식단 안내', 'NOTICE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 12),
    (1001, 'admin', 'admin', '4월 1일부터 카페 운영 시간이 아래와 같이 변경됩니다. 참고 바랍니다.', '사내 카페 운영시간 변경 안내', 'NOTICE', DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), 8),
    (1002, 'admin', 'admin', '오늘 15시부터 1시간 동안 사내 메신저 점검이 예정되어 있습니다.', '사내 메신저 서비스 점검 안내', 'NOTICE', DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), 33),
    (1003, 'admin', 'admin', '신입사원 입문교육이 아래 일정으로 진행됩니다. 많은 참여 바랍니다.', '2025년 1차 신입사원 교육 일정', 'NOTICE', DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP), 14),
    (1004, 'admin', 'admin', '사내 정기 전산 점검이 진행될 예정입니다. 서비스 이용에 참고 부탁드립니다.', '전산 시스템 점검 공지', 'NOTICE', DATEADD('DAY', -4, CURRENT_TIMESTAMP), DATEADD('DAY', -4, CURRENT_TIMESTAMP), 22),
    (1005, 'admin', 'admin', '사내 보안 정책 강화를 위해 비밀번호 변경을 권고드립니다.', '보안 정책 변경 및 비밀번호 변경 요청', 'NOTICE', DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP), 17),
    (1006, 'admin', 'admin', '사내 임직원용 웰니스 프로그램 신청이 시작되었습니다.', '웰니스 프로그램 신청 안내', 'NOTICE', DATEADD('DAY', -6, CURRENT_TIMESTAMP), DATEADD('DAY', -6, CURRENT_TIMESTAMP), 25),
    (1007, 'admin', 'admin', '오늘은 여보야 점심 특별 메뉴가 제공됩니다. 놓치지 마세요!', '오늘의 특별 점심 메뉴 안내', 'NOTICE', DATEADD('DAY', -7, CURRENT_TIMESTAMP), DATEADD('DAY', -7, CURRENT_TIMESTAMP), 10),
    (1008, 'admin', 'admin', '이번 주 금요일 17시부터 서버 긴급 점검이 예정되어 있습니다.', '서버 긴급 점검 안내 (3/29)', 'NOTICE', DATEADD('DAY', -8, CURRENT_TIMESTAMP), DATEADD('DAY', -8, CURRENT_TIMESTAMP), 45),
    (1009, 'admin', 'admin', '임직원 여러분의 의견을 듣고자 설문을 진행합니다.', '사내 문화 개선 설문 참여 요청', 'NOTICE', DATEADD('DAY', -9, CURRENT_TIMESTAMP), DATEADD('DAY', -9, CURRENT_TIMESTAMP), 18),
    (1010, 'admin', 'admin', '근로자의 날 휴무 일정 및 급식 운영 여부를 안내드립니다.', '근로자의 날 휴무 안내', 'NOTICE', DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', -10, CURRENT_TIMESTAMP), 21),
    (1011, 'admin', 'admin', '업데이트된 복지포인트 제도에 대해 안내드립니다.', '복지포인트 제도 개편 안내', 'NOTICE', DATEADD('DAY', -11, CURRENT_TIMESTAMP), DATEADD('DAY', -11, CURRENT_TIMESTAMP), 27),
    (1012, 'admin', 'admin', '사내 그룹웨어가 새로운 버전으로 업그레이드됩니다.', '그룹웨어 시스템 업데이트 공지', 'NOTICE', DATEADD('DAY', -12, CURRENT_TIMESTAMP), DATEADD('DAY', -12, CURRENT_TIMESTAMP), 36),
    (1013, 'admin', 'admin', '회의실 예약 시스템에 오류가 발생해 점검 중입니다.', '회의실 예약 시스템 점검 안내', 'NOTICE', DATEADD('DAY', -13, CURRENT_TIMESTAMP), DATEADD('DAY', -13, CURRENT_TIMESTAMP), 15),
    (1014, 'admin', 'admin', '업무에 참고할 수 있는 여름 휴가 가이드라인을 공유드립니다.', '2025 여름 휴가 가이드 배포', 'NOTICE', DATEADD('DAY', -14, CURRENT_TIMESTAMP), DATEADD('DAY', -14, CURRENT_TIMESTAMP), 40),
    (1015, 'admin', 'admin', '이번 분기 인사이동 내역을 공유드립니다.', '2025년 2분기 인사이동 공지', 'NOTICE', DATEADD('DAY', -15, CURRENT_TIMESTAMP), DATEADD('DAY', -15, CURRENT_TIMESTAMP), 11),
    (1016, 'admin', 'admin', '전 사원 대상 긴급 공지입니다. 오늘 18시 전까지 확인 바랍니다.', '전사 긴급공지: 업무자료 제출 마감', 'NOTICE', DATEADD('DAY', -16, CURRENT_TIMESTAMP), DATEADD('DAY', -16, CURRENT_TIMESTAMP), 13),
    (1017, 'admin', 'admin', '이번 주 금요일, 사내 네트워크가 일시 중단됩니다.', '사내 네트워크 일시 중단 안내', 'NOTICE', DATEADD('DAY', -17, CURRENT_TIMESTAMP), DATEADD('DAY', -17, CURRENT_TIMESTAMP), 8),
    (1018, 'admin', 'admin', '팀원 간 협업 강화를 위한 워크숍이 열립니다.', '협업 워크숍 개최 안내', 'NOTICE', DATEADD('DAY', -18, CURRENT_TIMESTAMP), DATEADD('DAY', -18, CURRENT_TIMESTAMP), 6),
    (1019, 'admin', 'admin', '회의실 무단 사용 관련 주의 공지입니다.', '회의실 무단 사용 자제 요청', 'NOTICE', DATEADD('DAY', -19, CURRENT_TIMESTAMP), DATEADD('DAY', -19, CURRENT_TIMESTAMP), 9);

-- NOTICE (공지사항 메타데이터)
INSERT INTO NOTICE (ATTACHMENT_URL, AUTHOR, CATEGORY, START_DATE, END_DATE, PINNED, STATUS, BOARD_ID)
VALUES
    ('https://example.com/notice1', 'admin', '식단', CURRENT_DATE, DATEADD('DAY', 3, CURRENT_DATE), 0, 'ACTIVE', 1000),
    ('https://example.com/notice2', 'admin', '운영', CURRENT_DATE, DATEADD('DAY', 7, CURRENT_DATE), 0, 'ACTIVE', 1001),
    ('https://example.com/notice3', 'admin', '점검', DATEADD('DAY', -1, CURRENT_DATE), DATEADD('DAY', 1, CURRENT_DATE), 1, 'ACTIVE', 1002),
    ('https://example.com/notice4', 'admin', '교육', CURRENT_DATE, DATEADD('DAY', 30, CURRENT_DATE), 0, 'ACTIVE', 1003),
    ('https://example.com/notice5', 'admin', '점검', DATEADD('DAY', -10, CURRENT_DATE), DATEADD('DAY', -5, CURRENT_DATE), 0, 'INACTIVE', 1004),
    ('https://example.com/notice6', 'admin', '보안', CURRENT_DATE, DATEADD('DAY', 14, CURRENT_DATE), 0, 'ACTIVE', 1005),
    ('https://example.com/notice7', 'admin', '복지', CURRENT_DATE, DATEADD('MONTH', 1, CURRENT_DATE), 0, 'ACTIVE', 1006),
    ('https://example.com/notice8', 'admin', '식단', CURRENT_DATE, DATEADD('DAY', 1, CURRENT_DATE), 0, 'ACTIVE', 1007),
    ('https://example.com/notice9', 'admin', '점검', DATEADD('DAY', -1, CURRENT_DATE), CURRENT_DATE, 1, 'ACTIVE', 1008),
    ('https://example.com/notice10', 'admin', '설문', CURRENT_DATE, DATEADD('DAY', 3, CURRENT_DATE), 0, 'ACTIVE', 1009),
    ('https://example.com/notice11', 'admin', '휴무', CURRENT_DATE, DATE '2025-05-01', 0, 'ACTIVE', 1010),
    ('https://example.com/notice12', 'admin', '복지', CURRENT_DATE, DATEADD('DAY', 14, CURRENT_DATE), 0, 'ACTIVE', 1011),
    ('https://example.com/notice13', 'admin', '시스템', CURRENT_DATE, DATEADD('DAY', 7, CURRENT_DATE), 0, 'ACTIVE', 1012),
    ('https://example.com/notice14', 'admin', '점검', DATEADD('DAY', -5, CURRENT_DATE), DATEADD('DAY', -1, CURRENT_DATE), 0, 'INACTIVE', 1013),
    ('https://example.com/notice15', 'admin', '가이드', CURRENT_DATE, DATE '2025-08-31', 0, 'ACTIVE', 1014),
    ('https://example.com/notice16', 'admin', '인사', CURRENT_DATE, DATEADD('DAY', 7, CURRENT_DATE), 0, 'ACTIVE', 1015),
    ('https://example.com/notice17', 'admin', '긴급', CURRENT_DATE, DATEADD('DAY', 1, CURRENT_DATE), 1, 'ACTIVE', 1016),
    ('https://example.com/notice18', 'admin', '점검', DATEADD('DAY', -1, CURRENT_DATE), DATEADD('DAY', 1, CURRENT_DATE), 0, 'ACTIVE', 1017),
    ('https://example.com/notice19', 'admin', '행사', CURRENT_DATE, DATEADD('DAY', 3, CURRENT_DATE), 0, 'ACTIVE', 1018),
    ('https://example.com/notice20', 'admin', '주의', CURRENT_DATE, DATEADD('DAY', 365, CURRENT_DATE), 0, 'ACTIVE', 1019);

