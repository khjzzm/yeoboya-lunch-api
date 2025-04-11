-- 역할 구조
INSERT INTO role_hierarchy(CHILD_NAME, PARENT_NAME)
VALUES ('ROLE_ADMIN', null),
       ('ROLE_MANAGER', 'ROLE_ADMIN'),
       ('ROLE_USER', 'ROLE_MANAGER'),
       ('ROLE_GUEST', 'ROLE_USER'),
       ('ROLE_BLOCK', 'ROLE_GUEST'),
       ('ROLE_WITHDRAWN', 'ROLE_BLOCK');

-- 역할 정의
INSERT INTO role(ROLES_ID, ROLE, ROLE_DESC)
VALUES (1, 'ROLE_ADMIN', '어드민'),
       (2, 'ROLE_MANAGER', '매니저'),
       (3, 'ROLE_USER', '유저'),
       (4, 'ROLE_GUEST', '게스트'),
       (5, 'ROLE_BLOCK', '차단'),
       (6, 'ROLE_WITHDRAWN', '탈퇴');

-- IP접근 설정
INSERT INTO access_ip (ip_id, ip_address, block)
VALUES (0, '0:0:0:0:0:0:0:1', false),
       (1, '127.0.0.1', false);

-- 토큰 무시 URL 설정
INSERT INTO token_ignore_urls (token_ignore_id, is_ignore, url)
VALUES (1, true, '/'),
       (2, true, '/actuator/health'),
       (3, true, '/redoc.html'),
       (4, true, '/v3/*'),
       (5, true, '/user/**');