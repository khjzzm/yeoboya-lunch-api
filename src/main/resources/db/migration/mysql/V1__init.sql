-- 1. Independent 테이블들

-- Category (모든 게시판에서 공용)
CREATE TABLE category
(
    CATEGORY_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(100)
);

-- 2. Category와 관련된 매핑 테이블
CREATE TABLE board_type_category
(
    BOARD_TYPE_CATEGORY_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_type             VARCHAR(30) NOT NULL,
    category_id            BIGINT NOT NULL,
    CONSTRAINT fk_board_type_category_category FOREIGN KEY (category_id) REFERENCES category (CATEGORY_ID)
);

-- 3. Role (회원 권한)
CREATE TABLE role
(
    roles_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    role      VARCHAR(255) NULL,
    role_desc VARCHAR(255) NULL,
    CONSTRAINT role UNIQUE (role)
);

-- 4. Resource (권한 관련 리소스)
CREATE TABLE resource
(
    resources_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    http_method   VARCHAR(255) NULL,
    order_num     INT          NULL,
    resource_name VARCHAR(255) NULL,
    resource_type VARCHAR(255) NULL,
    resource_desc VARCHAR(255) NULL,
    CONSTRAINT uk_resource_resource_name_http_method_resource_type UNIQUE (resource_name, http_method, resource_type)
);

-- 5. Shop (상점)
CREATE TABLE shop
(
    shop_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255) NULL,
    last_modified_by VARCHAR(255) NULL,
    name             VARCHAR(10) NOT NULL,
    CONSTRAINT name UNIQUE (name)
);

-- 6. Member (회원) – Role에 의존
CREATE TABLE member
(
    member_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date       TIMESTAMP NULL,
    last_modified_date TIMESTAMP NULL,
    email              VARCHAR(255) NULL,
    login_id           VARCHAR(255) NULL,
    name               VARCHAR(255) NULL,
    password           VARCHAR(255) NULL,
    provider           VARCHAR(255) NULL,
    provider_id        VARCHAR(255) NULL,
    role_roles_id      BIGINT NULL,
    CONSTRAINT uk_member_email_provider UNIQUE (email, provider),
    CONSTRAINT login_id UNIQUE (login_id),
    CONSTRAINT fk_member_role FOREIGN KEY (role_roles_id) REFERENCES role (roles_id) ON DELETE SET NULL
);

-- 7. Account (회원 계좌) – Member에 의존
CREATE TABLE account
(
    account_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date       TIMESTAMP NULL,
    last_modified_date TIMESTAMP NULL,
    account_number     VARCHAR(255) NULL,
    bank_name          VARCHAR(255) NULL,
    member_id          BIGINT NULL,
    CONSTRAINT member_id UNIQUE (member_id),
    CONSTRAINT fk_account_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

-- 8. Api_keys (API 키) – Member에 의존
CREATE TABLE api_keys
(
    apikey_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_key      VARCHAR(255) NOT NULL,
    pricing_plan INT NOT NULL,
    member_id    BIGINT NULL,
    CONSTRAINT api_key UNIQUE (api_key),
    CONSTRAINT fk_api_keys_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

-- 9. Abstract Board – 카테고리에 의존
CREATE TABLE abstract_board
(
    board_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by         VARCHAR(255) NULL,
    last_modified_by   VARCHAR(255) NULL,
    content            TEXT NULL,
    title              VARCHAR(255) NULL,
    view_count         INT NOT NULL,
    board_type         VARCHAR(31) NOT NULL,
    created_date       DATETIME NULL,
    last_modified_date DATETIME NULL,
    category_id        BIGINT NULL,
    CONSTRAINT FK_ABSTRACT_BOARD_CATEGORY FOREIGN KEY (category_id) REFERENCES category (CATEGORY_ID)
);

-- 10. Abstract File – 독립
CREATE TABLE abstract_file
(
    file_type          VARCHAR(31) NOT NULL,
    file_id            VARCHAR(36) NOT NULL,
    created_by         VARCHAR(255),
    created_date       DATETIME,
    last_modified_by   VARCHAR(255),
    last_modified_date DATETIME,
    checksum           VARCHAR(255),
    extension          VARCHAR(255) NOT NULL,
    file_name          VARCHAR(255) NOT NULL,
    file_path          VARCHAR(255) NOT NULL,
    image_url          VARCHAR(255) NOT NULL,
    is_public          BOOLEAN NOT NULL,
    mime_type          VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    size               BIGINT NOT NULL,
    thumbnail_url      VARCHAR(255),
    upload_date        DATETIME NOT NULL,
    uploaded_by        VARCHAR(255) NOT NULL,
    PRIMARY KEY (file_id)
);

-- 11. Access IP – 독립
CREATE TABLE access_ip
(
    ip_id      BIGINT NOT NULL PRIMARY KEY,
    block      TINYINT(1) NOT NULL,
    ip_address VARCHAR(255) NOT NULL
);

-- 12. Banner – 독립
CREATE TABLE banner
(
    banner_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    display_location VARCHAR(255) NOT NULL,
    display_order    INT NOT NULL,
    end_date         TIMESTAMP NOT NULL,
    start_date       TIMESTAMP NOT NULL,
    title            VARCHAR(255) NOT NULL
);

-- 13. Banner File – 배너에 의존
CREATE TABLE banner_file
(
    banner_file_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    extension          VARCHAR(255) NULL,
    file_name          VARCHAR(255) NULL,
    file_path          VARCHAR(255) NULL,
    original_file_name VARCHAR(255) NULL,
    size               BIGINT NULL,
    banner_id          BIGINT NULL,
    CONSTRAINT fk_banner_file_banner FOREIGN KEY (banner_id) REFERENCES banner (banner_id) ON DELETE CASCADE
);

-- 14. FAQ – 독립
CREATE TABLE faq
(
    faq_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255) NULL,
    last_modified_by VARCHAR(255) NULL,
    answer           VARCHAR(255) NOT NULL,
    question         VARCHAR(255) NOT NULL
);

-- 15. Hash Tag – 독립
CREATE TABLE hash_tag
(
    hashtag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag        VARCHAR(255) NULL
);

-- 16. Inquiry – 독립
CREATE TABLE inquiry
(
    inquiry_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255) NULL,
    last_modified_by VARCHAR(255) NULL,
    content          VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    login_id         VARCHAR(255) NULL,
    subject          VARCHAR(255) NOT NULL
);

-- 17. Notice – Notice는 AbstractBoard에 의존
CREATE TABLE notice
(
    board_id       BIGINT NOT NULL,
    author         VARCHAR(255) NOT NULL,
    start_date     DATE,
    end_date       DATE,
    pinned         BOOLEAN DEFAULT FALSE,
    attachment_url VARCHAR(255),
    status         VARCHAR(255) NOT NULL,
    PRIMARY KEY (board_id),
    CONSTRAINT fk_notice_board FOREIGN KEY (board_id) REFERENCES abstract_board (board_id)
);

-- 18. Free Board – AbstractBoard와 Member에 의존
CREATE TABLE free_board
(
    pin       VARCHAR(4),
    secret    TINYINT(1) NOT NULL,
    member_id BIGINT NULL,
    board_id  BIGINT NULL,
    CONSTRAINT fk_free_board_board FOREIGN KEY (board_id) REFERENCES abstract_board (board_id) ON DELETE CASCADE,
    CONSTRAINT fk_free_board_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE SET NULL
);

-- 19. Notice File – AbstractFile과 Notice에 의존
CREATE TABLE notice_file
(
    is_thumbnail    BOOLEAN NOT NULL,
    used_in_content BOOLEAN NOT NULL,
    file_id         VARCHAR(36) NOT NULL,
    notice_id       BIGINT,
    PRIMARY KEY (file_id),
    CONSTRAINT fk_notice_file_abstract_file FOREIGN KEY (file_id) REFERENCES abstract_file (file_id),
    CONSTRAINT fk_notice_file_notice FOREIGN KEY (notice_id) REFERENCES notice (board_id)
);

-- 20. Free Board File – FreeBoard와 AbstractFile에 의존
CREATE TABLE free_board_file
(
    is_thumbnail    BOOLEAN NOT NULL,
    used_in_content BOOLEAN NOT NULL,
    file_id         VARCHAR(255) NOT NULL,
    free_board_id   BIGINT,
    PRIMARY KEY (file_id),
    CONSTRAINT fk_free_board_file_board_id FOREIGN KEY (free_board_id) REFERENCES free_board (board_id),
    CONSTRAINT fk_free_board_file_file_id FOREIGN KEY (file_id) REFERENCES abstract_file (file_id)
);

-- 21. Board Hash Tag – AbstractBoard와 HashTag에 의존
CREATE TABLE board_hash_tag
(
    board_hashtag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id         BIGINT NULL,
    hashtag_id       BIGINT NULL,
    CONSTRAINT fk_board_hash_tag_board FOREIGN KEY (board_id) REFERENCES abstract_board (board_id) ON DELETE CASCADE,
    CONSTRAINT fk_board_hash_tag_hashtag FOREIGN KEY (hashtag_id) REFERENCES hash_tag (hashtag_id) ON DELETE CASCADE
);

-- 22. Likes – AbstractBoard와 Member에 의존
CREATE TABLE likes
(
    like_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id  BIGINT NULL,
    member_id BIGINT NULL,
    CONSTRAINT uk_likes_board_member UNIQUE (board_id, member_id),
    CONSTRAINT fk_likes_board FOREIGN KEY (board_id) REFERENCES abstract_board (board_id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

-- 23. Login Info – Member에 의존
CREATE TABLE login_info
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    locale      VARCHAR(255) NULL,
    login_time  TIMESTAMP NULL,
    remote_ip   VARCHAR(255) NULL,
    request_uri VARCHAR(255) NULL,
    session_id  VARCHAR(255) NULL,
    user_agent  VARCHAR(255) NULL,
    member_id   BIGINT NULL,
    CONSTRAINT fk_login_info_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

-- 24. Member Info – Member에 의존
CREATE TABLE member_info
(
    member_info_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bio            VARCHAR(255) NOT NULL,
    nick_name      VARCHAR(255) NULL,
    phone_number   VARCHAR(255) NULL,
    member_id      BIGINT NULL,
    CONSTRAINT nick_name UNIQUE (nick_name),
    CONSTRAINT fk_member_info_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

-- 25. Member Profile File – Member에 의존
CREATE TABLE member_profile_file
(
    member_profile_file_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    extension              VARCHAR(255) NULL,
    image_url              VARCHAR(255) NULL,
    file_name              VARCHAR(255) NULL,
    file_path              VARCHAR(255) NULL,
    is_default             TINYINT(1) NULL,
    original_file_name     VARCHAR(255) NULL,
    size                   BIGINT NULL,
    member_id              BIGINT NULL,
    checksum               VARCHAR(255) NOT NULL,
    mime_type              VARCHAR(255) NOT NULL,
    upload_date            TIMESTAMP NOT NULL,
    uploaded_by            VARCHAR(255) NOT NULL,
    is_public              TINYINT(1) DEFAULT 0 NOT NULL,
    thumbnail_url          VARCHAR(255) NULL,
    CONSTRAINT fk_member_profile_file_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

-- 26. Notice Read Status – Member와 Notice에 의존
CREATE TABLE notice_read_status
(
    notice_read_status_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    read_at               TIMESTAMP NOT NULL,
    member_id             BIGINT NOT NULL,
    notice_id             BIGINT NOT NULL,
    CONSTRAINT fk_notice_read_status_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

-- 27. Reply – AbstractBoard, Member 및 자기 자신(부모 댓글)에 의존
CREATE TABLE reply
(
    reply_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by         VARCHAR(255) NULL,
    last_modified_by   VARCHAR(255) NULL,
    content            VARCHAR(255) NOT NULL,
    create_date        TIMESTAMP NULL,
    board_id           BIGINT NULL,
    member_id          BIGINT NULL,
    parent_reply_id    BIGINT NULL,
    deleted            TINYINT(1) DEFAULT 0 NULL,
    created_date       TIMESTAMP NULL,
    last_modified_date TIMESTAMP NULL,
    CONSTRAINT fk_reply_board FOREIGN KEY (board_id) REFERENCES abstract_board (board_id) ON DELETE CASCADE,
    CONSTRAINT fk_reply_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE,
    CONSTRAINT fk_reply_parent FOREIGN KEY (parent_reply_id) REFERENCES reply (reply_id) ON DELETE CASCADE
);

-- 28. Role Hierarchy – 자기참조, 별도 순서 (빈 테이블 생성 후 데이터 삽입)
CREATE TABLE role_hierarchy
(
    role_hierarchy_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    child_name        VARCHAR(255) NULL,
    parent_name       VARCHAR(255) NULL,
    CONSTRAINT uk_role_hierarchy_child_name UNIQUE (child_name),
    CONSTRAINT child_name UNIQUE (child_name),
    CONSTRAINT fk_role_hierarchy_parent FOREIGN KEY (parent_name) REFERENCES role_hierarchy (child_name) ON DELETE CASCADE
);

-- 29. Role Resource – Resource와 Role에 의존
CREATE TABLE role_resource
(
    role_resource_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id      BIGINT NOT NULL,
    role_id          BIGINT NOT NULL,
    CONSTRAINT resource_id UNIQUE (resource_id, role_id),
    CONSTRAINT fk_role_resource_resource FOREIGN KEY (resource_id) REFERENCES resource (resources_id) ON DELETE CASCADE,
    CONSTRAINT fk_role_resource_role FOREIGN KEY (role_id) REFERENCES role (roles_id) ON DELETE CASCADE
);

-- 30. Orders – Member와 Shop에 의존
CREATE TABLE orders
(
    order_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_fee    INT NOT NULL,
    last_order_time TIMESTAMP NULL,
    memo            VARCHAR(255) NULL,
    order_date      TIMESTAMP NULL,
    status          VARCHAR(255) NULL,
    title           VARCHAR(255) NULL,
    member_id       BIGINT NULL,
    shop_id         BIGINT NULL,
    CONSTRAINT fk_orders_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE,
    CONSTRAINT fk_orders_shop FOREIGN KEY (shop_id) REFERENCES shop (shop_id) ON DELETE SET NULL
);

-- 31. Group Order – Member와 Orders에 의존
CREATE TABLE group_order
(
    group_order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_date     TIMESTAMP NULL,
    member_id      BIGINT NULL,
    order_id       BIGINT NULL,
    CONSTRAINT uk_order_member UNIQUE (order_id, member_id),
    CONSTRAINT order_id UNIQUE (order_id, member_id),
    CONSTRAINT fk_group_order_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE,
    CONSTRAINT fk_group_order_order FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE
);


-- 32. Item – Shop에 의존
CREATE TABLE item
(
    item_id          BIGINT auto_increment PRIMARY KEY,
    created_by       VARCHAR(255) NULL,
    last_modified_by VARCHAR(255) NULL,
    name             VARCHAR(255) NULL,
    price            INT          NOT NULL,
    shop_id          BIGINT       NOT NULL,
    CONSTRAINT uk_name_shop UNIQUE (name, shop_id),
    CONSTRAINT fk_item_shop FOREIGN KEY (shop_id) REFERENCES shop (shop_id) ON DELETE CASCADE
);

-- 33. Order Item – Group Order, Item, Orders에 의존
CREATE TABLE order_item
(
    order_item_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_price      INT NOT NULL,
    order_quantity   INT NOT NULL,
    order_buy_member BIGINT NULL,
    item_id          BIGINT NULL,
    order_id         BIGINT NULL,
    CONSTRAINT fk_order_item_buyer FOREIGN KEY (order_buy_member) REFERENCES group_order (group_order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_item FOREIGN KEY (item_id) REFERENCES item (item_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE
);

-- 34. Review – Member, Orders, Shop에 의존
CREATE TABLE review
(
    review_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by       VARCHAR(255) NULL,
    last_modified_by VARCHAR(255) NULL,
    content          TEXT NOT NULL,
    shop_rating      INT NOT NULL,
    member_id        BIGINT NOT NULL,
    order_id         BIGINT NOT NULL,
    shop_id          BIGINT NULL,
    CONSTRAINT uk_review_member_order UNIQUE (member_id, order_id),
    CONSTRAINT fk_review_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE,
    CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
    CONSTRAINT fk_review_shop FOREIGN KEY (shop_id) REFERENCES shop (shop_id) ON DELETE SET NULL,
    CHECK ((shop_rating >= 0) AND (shop_rating <= 5))
);

-- 35. Token Ignore URLs – Independent
CREATE TABLE token_ignore_urls
(
    token_ignore_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    is_ignore       TINYINT(1) NOT NULL,
    url             VARCHAR(255) NOT NULL
);

-- 36. User Security Status – Member에 의존
CREATE TABLE user_security_status
(
    user_security_status_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    is_account_non_expired     TINYINT(1) NOT NULL,
    is_account_non_locked      TINYINT(1) NOT NULL,
    is_credentials_non_expired TINYINT(1) NOT NULL,
    is_enabled                 TINYINT(1) NOT NULL,
    member_id                  BIGINT NULL,
    CONSTRAINT fk_user_security_status_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);