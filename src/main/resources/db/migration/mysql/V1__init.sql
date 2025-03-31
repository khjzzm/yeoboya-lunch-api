create table abstract_board
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

create table access_ip
(
    ip_id      bigint       not null
        primary key,
    block      tinyint(1)   not null,
    ip_address varchar(255) not null
);

create table banner
(
    banner_id        bigint auto_increment
        primary key,
    display_location varchar(255) not null,
    display_order    int          not null,
    end_date         timestamp    not null,
    start_date       timestamp    not null,
    title            varchar(255) not null
);

create table banner_file
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

create table faq
(
    faq_id           bigint auto_increment
        primary key,
    created_by       varchar(255) null,
    last_modified_by varchar(255) null,
    answer           varchar(255) not null,
    question         varchar(255) not null
);

create table hash_tag
(
    hashtag_id bigint auto_increment
        primary key,
    tag        varchar(255) null
);

create table inquiry
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

create table notice
(
    attachment_url varchar(255) null,
    author         varchar(255) not null,
    category       varchar(255) not null,
    end_date       date         null,
    start_date     date         null,
    status         varchar(255) not null,
    board_id       bigint       null,
    pinned         bit          null,
    constraint fk_notice_board
        foreign key (board_id) references abstract_board (board_id)
            on delete cascade
);

create table notice_file
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
    last_modified_date datetime     null
);

create table resource
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

create table role
(
    roles_id  bigint auto_increment
        primary key,
    role      varchar(255) null,
    role_desc varchar(255) null,
    constraint role
        unique (role)
);

create table member
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
    constraint uk_member_email_provider
        unique (email, provider),
    constraint login_id
        unique (login_id),
    constraint fk_member_role
        foreign key (role_roles_id) references role (roles_id)
            on delete set null
);

create table account
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

create table api_keys
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

create table board
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

create table board_file
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

create table board_hash_tag
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

create table free_board
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

create table likes
(
    like_id   bigint auto_increment
        primary key,
    board_id  bigint null,
    member_id bigint null,
    constraint uk_likes_board_member
        unique (board_id, member_id),
    constraint fk_likes_board
        foreign key (board_id) references board (board_id)
            on delete cascade,
    constraint fk_likes_member
        foreign key (member_id) references member (member_id)
            on delete cascade
);

create table login_info
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

create table member_info
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

create table member_profile_file
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

create table notice_read_status
(
    notice_read_status_id bigint auto_increment
        primary key,
    read_at               timestamp not null,
    member_id             bigint    not null,
    notice_id             bigint    not null,
    constraint fk_notice_read_status_member
        foreign key (member_id) references member (member_id)
            on delete cascade
);

create table reply
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

create table role_hierarchy
(
    role_hierarchy_id bigint auto_increment
        primary key,
    child_name        varchar(255) null,
    parent_name       varchar(255) null,
    constraint uk_role_hierarchy_child_name
        unique (child_name),
    constraint child_name
        unique (child_name),
    constraint fk_role_hierarchy_parent
        foreign key (parent_name) references role_hierarchy (child_name)
            on delete cascade
);

create table role_resource
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

create table shop
(
    shop_id          bigint auto_increment
        primary key,
    created_by       varchar(255) null,
    last_modified_by varchar(255) null,
    name             varchar(10)  not null,
    constraint name
        unique (name)
);

create table item
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

create table orders
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

create table group_order
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

create table order_item
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

create table review
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
    constraint member_id
        unique (member_id, order_id),
    constraint uk_review_member_order
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

create table token_ignore_urls
(
    token_ignore_id bigint auto_increment
        primary key,
    is_ignore       tinyint(1)   not null,
    url             varchar(255) not null
);

create table user_security_status
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