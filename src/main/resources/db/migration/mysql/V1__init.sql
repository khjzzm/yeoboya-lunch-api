create table if not exists abstract_board
(
    board_id           bigint auto_increment
        primary key,
    created_by         varchar(255) null,
    last_modified_by   varchar(255) null,
    content            text         null,
    title              varchar(255) null,
    view_count         int          not null,
    board_type         varchar(31)  not null,
    created_date       datetime     null,
    last_modified_date datetime     null
);

create table if not exists access_ip
(
    ip_id      bigint       not null
        primary key,
    block      tinyint(1)   not null,
    ip_address varchar(255) not null
);

create table if not exists banner
(
    banner_id        bigint auto_increment
        primary key,
    display_location varchar(255) not null,
    display_order    int          not null,
    end_date         timestamp    not null,
    start_date       timestamp    not null,
    title            varchar(255) not null
);

create table if not exists banner_file
(
    banner_file_id     bigint auto_increment
        primary key,
    extension          varchar(255) null,
    file_name          varchar(255) null,
    file_path          varchar(255) null,
    original_file_name varchar(255) null,
    size               bigint       null,
    banner_id          bigint       null,
    constraint fk_banner_file_banner
        foreign key (banner_id) references banner (banner_id)
            on delete cascade
);

create table if not exists faq
(
    faq_id           bigint auto_increment
        primary key,
    created_by       varchar(255) null,
    last_modified_by varchar(255) null,
    answer           varchar(255) not null,
    question         varchar(255) not null
);

create table if not exists hash_tag
(
    hashtag_id bigint auto_increment
        primary key,
    tag        varchar(255) null
);

create table if not exists hibernate_sequence
(
    next_val bigint null
);

create table if not exists inquiry
(
    inquiry_id       bigint auto_increment
        primary key,
    created_by       varchar(255) null,
    last_modified_by varchar(255) null,
    content          varchar(255) not null,
    email            varchar(255) not null,
    login_id         varchar(255) null,
    subject          varchar(255) not null
);

create table if not exists notice
(
    attachment_url varchar(255) null,
    author         varchar(255) not null,
    category       varchar(255) not null,
    end_date       datetime     null,
    priority       int          not null,
    start_date     datetime     null,
    status         varchar(255) not null,
    board_id       bigint       null,
    constraint fk_notice_board
        foreign key (board_id) references abstract_board (board_id)
            on delete cascade
);

create table if not exists old_notice
(
    notice_id        bigint auto_increment
        primary key,
    created_by       varchar(255) null,
    last_modified_by varchar(255) null,
    attachment_url   varchar(255) null,
    author           varchar(255) not null,
    category         varchar(255) not null,
    content          text         not null,
    end_date         timestamp    null,
    priority         int          not null,
    start_date       timestamp    null,
    status           varchar(255) not null,
    tags             varchar(255) null,
    title            varchar(255) not null,
    view_count       int          not null
);

create table if not exists notice_file
(
    notice_file_id     bigint auto_increment
        primary key,
    created_by         varchar(255) null,
    last_modified_by   varchar(255) null,
    checksum           varchar(255) null,
    extension          varchar(255) not null,
    file_name          varchar(255) not null,
    file_path          varchar(255) not null,
    image_url          varchar(255) not null,
    is_public          tinyint(1)   not null,
    mime_type          varchar(255) not null,
    original_file_name varchar(255) not null,
    size               bigint       not null,
    thumbnail_url      varchar(255) null,
    upload_date        timestamp    not null,
    uploaded_by        varchar(255) not null,
    notice_id          bigint       not null,
    created_date       datetime     null,
    last_modified_date datetime     null,
    constraint fk_notice
        foreign key (notice_id) references old_notice (notice_id)
            on delete cascade
);

create table if not exists resource
(
    resources_id  bigint auto_increment
        primary key,
    http_method   varchar(255) null,
    order_num     int          null,
    resource_name varchar(255) null,
    resource_type varchar(255) null,
    resource_desc varchar(255) null,
    constraint UK_resource_resource_name_http_method_resource_type
        unique (resource_name, http_method, resource_type)
);

create table if not exists role
(
    roles_id  bigint auto_increment
        primary key,
    role      varchar(255) null,
    role_desc varchar(255) null,
    constraint role
        unique (role)
);

create table if not exists member
(
    member_id          bigint auto_increment
        primary key,
    created_date       timestamp    null,
    last_modified_date timestamp    null,
    email              varchar(255) null,
    login_id           varchar(255) null,
    name               varchar(255) null,
    password           varchar(255) null,
    provider           varchar(255) null,
    provider_id        varchar(255) null,
    role_roles_id      bigint       null,
    constraint UK7blitxweiw758g32lg3bhavf6
        unique (email, provider),
    constraint login_id
        unique (login_id),
    constraint fk_member_role
        foreign key (role_roles_id) references role (roles_id)
            on delete set null
);

create table if not exists account
(
    account_id         bigint auto_increment
        primary key,
    created_date       timestamp    null,
    last_modified_date timestamp    null,
    account_number     varchar(255) null,
    bank_name          varchar(255) null,
    member_id          bigint       null,
    constraint member_id
        unique (member_id),
    constraint fk_account_member
        foreign key (member_id) references member (member_id)
            on delete cascade
);

create table if not exists api_keys
(
    apikey_id    bigint auto_increment
        primary key,
    api_key      varchar(255) not null,
    pricing_plan int          not null,
    member_id    bigint       null,
    constraint api_key
        unique (api_key),
    constraint fk_api_keys_member
        foreign key (member_id) references member (member_id)
            on delete cascade
);

create table if not exists board
(
    board_id         bigint auto_increment
        primary key,
    created_by       varchar(255) null,
    last_modified_by varchar(255) null,
    content          text         null,
    create_date      timestamp    null,
    pin              int          not null,
    secret           tinyint(1)   not null,
    title            varchar(255) not null,
    member_id        bigint       null,
    constraint fk_board_member
        foreign key (member_id) references member (member_id)
            on delete set null
);

create table if not exists board_file
(
    board_file_id      bigint auto_increment
        primary key,
    extension          varchar(255)         null,
    file_name          varchar(255)         null,
    file_path          varchar(255)         null,
    original_file_name varchar(255)         null,
    size               bigint               null,
    board_id           bigint               null,
    checksum           varchar(255)         not null,
    is_public          tinyint(1) default 0 not null,
    mime_type          varchar(255)         not null,
    thumbnail_url      varchar(255)         null,
    upload_date        timestamp            not null,
    uploaded_by        varchar(255)         not null,
    image_url          varchar(255)         not null,
    constraint fk_board_file_board
        foreign key (board_id) references board (board_id)
            on delete cascade
);

create table if not exists board_hash_tag
(
    board_hashtag_id bigint auto_increment
        primary key,
    board_id         bigint null,
    hashtag_id       bigint null,
    constraint fk_board_hash_tag_board
        foreign key (board_id) references board (board_id)
            on delete cascade,
    constraint fk_board_hash_tag_hashtag
        foreign key (hashtag_id) references hash_tag (hashtag_id)
            on delete cascade
);

create table if not exists free_board
(
    content   text       null,
    pin       int        not null,
    secret    tinyint(1) not null,
    member_id bigint     null,
    board_id  bigint     null,
    constraint fk_free_board_board
        foreign key (board_id) references abstract_board (board_id)
            on delete cascade,
    constraint fk_free_board_member
        foreign key (member_id) references member (member_id)
            on delete set null
);

create table if not exists likes
(
    like_id   bigint auto_increment
        primary key,
    board_id  bigint null,
    member_id bigint null,
    constraint UKfwpcqlf7jjnlg0xags8noqoc7
        unique (board_id, member_id),
    constraint board_id
        unique (board_id, member_id),
    constraint fk_likes_board
        foreign key (board_id) references board (board_id)
            on delete cascade,
    constraint fk_likes_member
        foreign key (member_id) references member (member_id)
            on delete cascade
);

create table if not exists login_info
(
    id          bigint auto_increment
        primary key,
    locale      varchar(255) null,
    login_time  timestamp    null,
    remote_ip   varchar(255) null,
    request_uri varchar(255) null,
    session_id  varchar(255) null,
    user_agent  varchar(255) null,
    member_id   bigint       null,
    constraint fk_login_info_member
        foreign key (member_id) references member (member_id)
            on delete cascade
);

create table if not exists member_info
(
    member_info_id bigint auto_increment
        primary key,
    bio            varchar(255) not null,
    nick_name      varchar(255) null,
    phone_number   varchar(255) null,
    member_id      bigint       null,
    constraint nick_name
        unique (nick_name),
    constraint fk_member_info_member
        foreign key (member_id) references member (member_id)
            on delete cascade
);

create table if not exists member_profile_file
(
    member_profile_file_id bigint auto_increment
        primary key,
    extension              varchar(255)         null,
    image_url              varchar(255)         null,
    file_name              varchar(255)         null,
    file_path              varchar(255)         null,
    is_default             tinyint(1)           null,
    original_file_name     varchar(255)         null,
    size                   bigint               null,
    member_id              bigint               null,
    checksum               varchar(255)         not null,
    mime_type              varchar(255)         not null,
    upload_date            timestamp            not null,
    uploaded_by            varchar(255)         not null,
    is_public              tinyint(1) default 0 not null,
    thumbnail_url          varchar(255)         null,
    constraint fk_member_profile_file_member
        foreign key (member_id) references member (member_id)
            on delete cascade
);

create table if not exists notice_read_status
(
    notice_read_status_id bigint auto_increment
        primary key,
    read_at               timestamp  not null,
    read_status           tinyint(1) not null,
    member_id             bigint     not null,
    notice_id             bigint     not null,
    constraint fk_notice_read_status_member
        foreign key (member_id) references member (member_id)
            on delete cascade,
    constraint fk_notice_read_status_notice
        foreign key (notice_id) references notice (notice_id)
            on delete cascade
);

create table if not exists reply
(
    reply_id           bigint auto_increment
        primary key,
    created_by         varchar(255)         null,
    last_modified_by   varchar(255)         null,
    content            varchar(255)         not null,
    create_date        timestamp            null,
    board_id           bigint               null,
    member_id          bigint               null,
    parent_reply_id    bigint               null,
    deleted            tinyint(1) default 0 null,
    created_date       timestamp            null,
    last_modified_date timestamp            null,
    constraint fk_reply_board
        foreign key (board_id) references board (board_id)
            on delete cascade,
    constraint fk_reply_member
        foreign key (member_id) references member (member_id)
            on delete cascade,
    constraint fk_reply_parent
        foreign key (parent_reply_id) references reply (reply_id)
            on delete cascade
);

create table if not exists role_hierarchy
(
    role_hierarchy_id bigint auto_increment
        primary key,
    child_name        varchar(255) null,
    parent_name       varchar(255) null,
    constraint UK_aux81x2bb3geojtq6mf8rs19l
        unique (child_name),
    constraint child_name
        unique (child_name),
    constraint fk_role_hierarchy_parent
        foreign key (parent_name) references role_hierarchy (child_name)
            on delete cascade
);

create table if not exists role_resource
(
    role_resource_id bigint auto_increment
        primary key,
    resource_id      bigint not null,
    role_id          bigint not null,
    constraint resource_id
        unique (resource_id, role_id),
    constraint fk_role_resources_resource
        foreign key (resource_id) references resource (resources_id)
            on delete cascade,
    constraint fk_role_resources_role
        foreign key (role_id) references role (roles_id)
            on delete cascade
);

create table if not exists shop
(
    shop_id          bigint auto_increment
        primary key,
    created_by       varchar(255) null,
    last_modified_by varchar(255) null,
    name             varchar(10)  not null,
    constraint name
        unique (name)
);

create table if not exists item
(
    item_id          bigint auto_increment
        primary key,
    created_by       varchar(255) null,
    last_modified_by varchar(255) null,
    name             varchar(255) null,
    price            int          not null,
    shop_id          bigint       not null,
    constraint uk_name_shop
        unique (name, shop_id),
    constraint fk_item_shop
        foreign key (shop_id) references shop (shop_id)
            on delete cascade
);

create table if not exists orders
(
    order_id        bigint auto_increment
        primary key,
    delivery_fee    int          not null,
    last_order_time timestamp    null,
    memo            varchar(255) null,
    order_date      timestamp    null,
    status          varchar(255) null,
    title           varchar(255) null,
    member_id       bigint       null,
    shop_id         bigint       null,
    constraint fk_orders_member
        foreign key (member_id) references member (member_id)
            on delete cascade,
    constraint fk_orders_shop
        foreign key (shop_id) references shop (shop_id)
            on delete set null
);

create table if not exists group_order
(
    group_order_id bigint auto_increment
        primary key,
    order_date     timestamp null,
    member_id      bigint    null,
    order_id       bigint    null,
    constraint UK_ORDER_MEMBER
        unique (order_id, member_id),
    constraint order_id
        unique (order_id, member_id),
    constraint fk_group_order_member
        foreign key (member_id) references member (member_id)
            on delete cascade,
    constraint fk_group_order_order
        foreign key (order_id) references orders (order_id)
            on delete cascade
);

create table if not exists order_item
(
    order_item_id    bigint auto_increment
        primary key,
    order_price      int    not null,
    order_quantity   int    not null,
    order_buy_member bigint null,
    item_id          bigint null,
    order_id         bigint null,
    constraint fk_order_item_buyer
        foreign key (order_buy_member) references group_order (group_order_id)
            on delete cascade,
    constraint fk_order_item_item
        foreign key (item_id) references item (item_id)
            on delete cascade,
    constraint fk_order_item_order
        foreign key (order_id) references orders (order_id)
            on delete cascade
);

create table if not exists review
(
    review_id        bigint auto_increment
        primary key,
    created_by       varchar(255) null,
    last_modified_by varchar(255) null,
    content          text         not null,
    shop_rating      int          not null,
    member_id        bigint       not null,
    order_id         bigint       not null,
    shop_id          bigint       null,
    constraint UK3rs8jisxaeew2dfx9m3qvx6fl
        unique (member_id, order_id),
    constraint member_id
        unique (member_id, order_id),
    constraint fk_review_member
        foreign key (member_id) references member (member_id)
            on delete cascade,
    constraint fk_review_order
        foreign key (order_id) references orders (order_id)
            on delete cascade,
    constraint fk_review_shop
        foreign key (shop_id) references shop (shop_id)
            on delete set null,
    check ((`shop_rating` >= 0) and (`shop_rating` <= 5))
);

create table if not exists token_ignore_urls
(
    token_ignore_id bigint auto_increment
        primary key,
    is_ignore       tinyint(1)   not null,
    url             varchar(255) not null
);

create table if not exists user_security_status
(
    user_security_status_id    bigint auto_increment
        primary key,
    is_account_non_expired     tinyint(1) not null,
    is_account_non_locked      tinyint(1) not null,
    is_credentials_non_expired tinyint(1) not null,
    is_enabled                 tinyint(1) not null,
    member_id                  bigint     null,
    constraint fk_user_security_status_member
        foreign key (member_id) references member (member_id)
            on delete cascade
);

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
       (5, true, '/user/**');