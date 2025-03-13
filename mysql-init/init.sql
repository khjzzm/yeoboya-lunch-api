-- 1. 사용자 계정 생성 및 권한 부여
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, ALTER, INDEX, TRIGGER
    ON `yeoboya_lunch`.* TO 'y10h'@'%';
FLUSH PRIVILEGES;

-- 2. 읽기 전용 계정 추가
CREATE USER IF NOT EXISTS 'readonly_y10h'@'%' IDENTIFIED BY 'lunch';
GRANT SELECT ON `yeoboya_lunch`.* TO 'readonly_y10h'@'%';
FLUSH PRIVILEGES;


-- 외래키 참조 순서를 고려한 테이블 생성
-- [1] 의존관계 없는 테이블들 (11)
CREATE TABLE IF NOT EXISTS access_ip
(
    ip_id      BIGINT       NOT NULL PRIMARY KEY,
    block      BOOLEAN      NOT NULL,
    ip_address VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS banner
(
    banner_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    display_location VARCHAR(255) NOT NULL,
    display_order    INT          NOT NULL,
    end_date         TIMESTAMP    NOT NULL,
    start_date       TIMESTAMP    NOT NULL,
    title            VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS faq
(
    faq_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255),
    last_modified_by VARCHAR(255),
    answer           VARCHAR(255) NOT NULL,
    question         VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS hash_tag
(
    hashtag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS inquiry
(
    inquiry_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255),
    last_modified_by VARCHAR(255),
    content          VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    login_id         VARCHAR(255),
    subject          VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS notice
(
    notice_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255),
    last_modified_by VARCHAR(255),
    attachment_url   VARCHAR(255),
    author           VARCHAR(255) NOT NULL,
    category         VARCHAR(255) NOT NULL,
    content          TEXT         NOT NULL,
    end_date         TIMESTAMP,
    priority         INT          NOT NULL,
    start_date       TIMESTAMP,
    status           VARCHAR(255) NOT NULL,
    tags             VARCHAR(255),
    title            VARCHAR(255) NOT NULL,
    view_count       INT          NOT NULL
);

CREATE TABLE IF NOT EXISTS role
(
    roles_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(255) UNIQUE,
    role_desc VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS shop
(
    shop_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255),
    last_modified_by VARCHAR(255),
    name             VARCHAR(10) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS token_ignore_urls
(
    token_ignore_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    is_ignore       BOOLEAN      NOT NULL,
    url             VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS resource
(
    resources_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    http_method   varchar(255) null,
    order_num     int          null,
    resource_name varchar(255) null,
    resource_type varchar(255) null,
    resource_desc varchar(255) null,
    constraint UK_resource_resource_name_http_method_resource_type unique (resource_name, http_method, resource_type)
);


CREATE TABLE IF NOT EXISTS role_hierarchy
(
    role_hierarchy_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    child_name        VARCHAR(255) UNIQUE,
    parent_name       VARCHAR(255),
    CONSTRAINT fk_role_hierarchy_parent FOREIGN KEY (parent_name) REFERENCES role_hierarchy (child_name) ON DELETE CASCADE
);


-- [2] role을 참조하는 테이블 (member 등) (2)
CREATE TABLE IF NOT EXISTS member
(
    member_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date       TIMESTAMP,
    last_modified_date TIMESTAMP,
    email              VARCHAR(255),
    login_id           VARCHAR(255) UNIQUE,
    name               VARCHAR(255),
    password           VARCHAR(255),
    provider           VARCHAR(255),
    provider_id        VARCHAR(255),
    role_roles_id            BIGINT,
    CONSTRAINT fk_member_role FOREIGN KEY (role_roles_id) REFERENCES role (roles_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS account
(
    account_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date       TIMESTAMP,
    last_modified_date TIMESTAMP,
    account_number     VARCHAR(255),
    bank_name          VARCHAR(255),
    member_id          BIGINT UNIQUE,
    CONSTRAINT fk_account_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

-- [3] board 및 관련 테이블 (5)
CREATE TABLE IF NOT EXISTS board
(
    board_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255),
    last_modified_by VARCHAR(255),
    content          TEXT,
    create_date      TIMESTAMP,
    pin              INT          NOT NULL,
    secret           BOOLEAN      NOT NULL,
    title            VARCHAR(255) NOT NULL,
    member_id        BIGINT,
    CONSTRAINT fk_board_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS banner_file
(
    banner_file_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    extension          VARCHAR(255),
    file_name          VARCHAR(255),
    file_path          VARCHAR(255),
    original_file_name VARCHAR(255),
    size               BIGINT,
    banner_id          BIGINT,
    CONSTRAINT fk_banner_file_banner FOREIGN KEY (banner_id) REFERENCES banner (banner_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS board_file
(
    board_file_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    extension          VARCHAR(255),
    file_name          VARCHAR(255),
    file_path          VARCHAR(255),
    original_file_name VARCHAR(255),
    size               BIGINT,
    board_id           BIGINT,
    CONSTRAINT fk_board_file_board FOREIGN KEY (board_id) REFERENCES board (board_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS board_hash_tag
(
    board_hashtag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id         BIGINT,
    hashtag_id       BIGINT,
    CONSTRAINT fk_board_hash_tag_board FOREIGN KEY (board_id) REFERENCES board (board_id) ON DELETE CASCADE,
    CONSTRAINT fk_board_hash_tag_hashtag FOREIGN KEY (hashtag_id) REFERENCES hash_tag (hashtag_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes
(
    like_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id  BIGINT,
    member_id BIGINT,
    UNIQUE (board_id, member_id),
    CONSTRAINT fk_likes_board FOREIGN KEY (board_id) REFERENCES board (board_id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

-- [4] 주문/리뷰 관련 테이블 (5)
CREATE TABLE IF NOT EXISTS orders
(
    order_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_fee    INT NOT NULL,
    last_order_time TIMESTAMP,
    memo            VARCHAR(255),
    order_date      TIMESTAMP,
    status          VARCHAR(255),
    title           VARCHAR(255),
    member_id       BIGINT,
    shop_id         BIGINT,
    CONSTRAINT fk_orders_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE,
    CONSTRAINT fk_orders_shop FOREIGN KEY (shop_id) REFERENCES shop (shop_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS review
(
    review_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255),
    last_modified_by VARCHAR(255),
    content          TEXT   NOT NULL,
    shop_rating      INT    NOT NULL CHECK (shop_rating >= 0 AND shop_rating <= 5),
    member_id        BIGINT NOT NULL,
    order_id         BIGINT NOT NULL,
    shop_id          BIGINT,
    UNIQUE (member_id, order_id),
    CONSTRAINT fk_review_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE,
    CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
    CONSTRAINT fk_review_shop FOREIGN KEY (shop_id) REFERENCES shop (shop_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS group_order
(
    group_order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_date     TIMESTAMP,
    member_id      BIGINT,
    order_id       BIGINT,
    UNIQUE (order_id, member_id),
    CONSTRAINT fk_group_order_order FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
    CONSTRAINT fk_group_order_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS item
(
    item_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255),
    last_modified_by VARCHAR(255),
    name             VARCHAR(255),
    price            INT    NOT NULL,
    shop_id          BIGINT NOT NULL,
    CONSTRAINT uk_name_shop UNIQUE (name, shop_id),
    CONSTRAINT fk_item_shop FOREIGN KEY (shop_id) REFERENCES shop (shop_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_item
(
    order_item_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_price      INT NOT NULL,
    order_quantity   INT NOT NULL,
    order_buy_member BIGINT,
    item_id          BIGINT,
    order_id         BIGINT,
    CONSTRAINT fk_order_item_buyer FOREIGN KEY (order_buy_member) REFERENCES group_order (group_order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_item FOREIGN KEY (item_id) REFERENCES item (item_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE
);

-- [5] 그 외 member 관련 테이블들 (7)
CREATE TABLE IF NOT EXISTS user_security_status
(
    user_security_status_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    is_account_non_expired     BOOLEAN NOT NULL,
    is_account_non_locked      BOOLEAN NOT NULL,
    is_credentials_non_expired BOOLEAN NOT NULL,
    is_enabled                 BOOLEAN NOT NULL,
    member_id                  BIGINT,
    CONSTRAINT fk_user_security_status_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS api_keys
(
    apikey_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_key      VARCHAR(255) NOT NULL UNIQUE,
    pricing_plan INT          NOT NULL,
    member_id    BIGINT,
    CONSTRAINT fk_api_keys_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS login_info
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    locale      VARCHAR(255),
    login_time  TIMESTAMP,
    remote_ip   VARCHAR(255),
    request_uri VARCHAR(255),
    session_id  VARCHAR(255),
    user_agent  VARCHAR(255),
    member_id   BIGINT,
    CONSTRAINT fk_login_info_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS member_info
(
    member_info_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bio            VARCHAR(255) NOT NULL,
    nick_name      VARCHAR(255) UNIQUE,
    phone_number   VARCHAR(255),
    member_id      BIGINT,
    CONSTRAINT fk_member_info_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS member_profile_file
(
    member_profile_file_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    extension              VARCHAR(255),
    external_form          VARCHAR(255),
    file_name              VARCHAR(255),
    file_path              VARCHAR(255),
    is_default             BOOLEAN,
    original_file_name     VARCHAR(255),
    size                   BIGINT,
    member_id              BIGINT,
    CONSTRAINT fk_member_profile_file_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notice_read_status
(
    notice_read_status_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    read_at               TIMESTAMP NOT NULL,
    read_status           BOOLEAN   NOT NULL,
    member_id             BIGINT    NOT NULL,
    notice_id             BIGINT    NOT NULL,
    CONSTRAINT fk_notice_read_status_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE,
    CONSTRAINT fk_notice_read_status_notice FOREIGN KEY (notice_id) REFERENCES notice (notice_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reply
(
    reply_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255),
    last_modified_by VARCHAR(255),
    content          VARCHAR(255) NOT NULL,
    create_date      TIMESTAMP,
    board_id         BIGINT,
    member_id        BIGINT,
    parent_reply_id  BIGINT,
    CONSTRAINT fk_reply_parent FOREIGN KEY (parent_reply_id) REFERENCES reply (reply_id) ON DELETE CASCADE,
    CONSTRAINT fk_reply_board FOREIGN KEY (board_id) REFERENCES board (board_id) ON DELETE CASCADE,
    CONSTRAINT fk_reply_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

-- [6] role와 resource를 연결하는 테이블 (1)
CREATE TABLE IF NOT EXISTS role_resource
(
    role_resource_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id         BIGINT NOT NULL,
    role_id             BIGINT NOT NULL,
    CONSTRAINT fk_role_resource_role FOREIGN KEY (role_id) REFERENCES role (roles_id) ON DELETE CASCADE,
    CONSTRAINT fk_role_resource_resource FOREIGN KEY (resource_id) REFERENCES resource (resources_id) ON DELETE CASCADE
);

-- 역할과 그 계층 관계 설정
INSERT INTO role_hierarchy(CHILD_NAME, PARENT_NAME)
VALUES ('ROLE_ADMIN', null),
       ('ROLE_MANAGER', 'ROLE_ADMIN'),
       ('ROLE_USER', 'ROLE_MANAGER'),
       ('ROLE_GUEST', 'ROLE_USER'),
       ('ROLE_BLOCK', 'ROLE_GUEST');

-- 역할 정의
INSERT INTO role(ROLES_ID, ROLE, ROLE_DESC)
VALUES (1, 'ROLE_ADMIN', '어드민'),
       (2, 'ROLE_MANAGER', '매니저'),
       (3, 'ROLE_USER', '유저'),
       (4, 'ROLE_GUEST', '게스트'),
       (5, 'ROLE_BLOCK', '차단');

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
       (5, true, '/user/*');
