create sequence HIBERNATE_SEQUENCE;

create table ABSTRACT_BOARD
(
    BOARD_ID           BIGINT auto_increment
        primary key,
    CREATED_BY         CHARACTER VARYING(255),
    LAST_MODIFIED_BY   CHARACTER VARYING(255),
    CONTENT            CHARACTER VARYING,
    TITLE              CHARACTER VARYING(255),
    BOARD_TYPE         CHARACTER VARYING(31) not null,
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_DATE TIMESTAMP,
    VIEW_COUNT         INTEGER default 0     not null
);

create table ABSTRACT_FILE
(
    FILE_TYPE          CHARACTER VARYING(31)  not null,
    FILE_ID            BIGINT                 not null
        primary key,
    CREATED_BY         CHARACTER VARYING(255),
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_BY   CHARACTER VARYING(255),
    LAST_MODIFIED_DATE TIMESTAMP,
    CHECKSUM           CHARACTER VARYING(255),
    EXTENSION          CHARACTER VARYING(255) not null,
    FILE_NAME          CHARACTER VARYING(255) not null,
    FILE_PATH          CHARACTER VARYING(255) not null,
    IMAGE_URL          CHARACTER VARYING(255) not null,
    IS_PUBLIC          BOOLEAN                not null,
    MIME_TYPE          CHARACTER VARYING(255) not null,
    ORIGINAL_FILE_NAME CHARACTER VARYING(255) not null,
    SIZE               BIGINT                 not null,
    THUMBNAIL_URL      CHARACTER VARYING(255),
    UPLOAD_DATE        TIMESTAMP              not null,
    UPLOADED_BY        CHARACTER VARYING(255) not null
);


create table ACCESS_IP
(
    IP_ID      BIGINT                 not null
        primary key,
    BLOCK      BOOLEAN                not null,
    IP_ADDRESS CHARACTER VARYING(255) not null
);

create table BANNER
(
    BANNER_ID        BIGINT auto_increment
        primary key,
    DISPLAY_LOCATION CHARACTER VARYING(255) not null,
    DISPLAY_ORDER    INTEGER                not null,
    END_DATE         TIMESTAMP              not null,
    START_DATE       TIMESTAMP              not null,
    TITLE            CHARACTER VARYING(255) not null
);

create table BANNER_FILE
(
    BANNER_FILE_ID     BIGINT auto_increment
        primary key,
    EXTENSION          CHARACTER VARYING(255),
    FILE_NAME          CHARACTER VARYING(255),
    FILE_PATH          CHARACTER VARYING(255),
    ORIGINAL_FILE_NAME CHARACTER VARYING(255),
    SIZE               BIGINT,
    BANNER_ID          BIGINT,
    constraint FKPFJFB8GV1QF16ENFKK7CXQHMA
        foreign key (BANNER_ID) references BANNER
);

create table FAQ
(
    FAQ_ID             BIGINT auto_increment
        primary key,
    CREATED_BY         CHARACTER VARYING(255),
    LAST_MODIFIED_BY   CHARACTER VARYING(255),
    ANSWER             CHARACTER VARYING(255) not null,
    QUESTION           CHARACTER VARYING(255) not null,
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_DATE TIMESTAMP
);

create table HASH_TAG
(
    HASHTAG_ID BIGINT auto_increment
        primary key,
    TAG        CHARACTER VARYING(255)
);

create table INQUIRY
(
    INQUIRY_ID         BIGINT auto_increment
        primary key,
    CREATED_BY         CHARACTER VARYING(255),
    LAST_MODIFIED_BY   CHARACTER VARYING(255),
    CONTENT            CHARACTER VARYING(255) not null,
    EMAIL              CHARACTER VARYING(255) not null,
    LOGIN_ID           CHARACTER VARYING(255),
    SUBJECT            CHARACTER VARYING(255) not null,
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_DATE TIMESTAMP
);

create table NOTICE
(
    BOARD_ID       BIGINT                 not null,
    CATEGORY       CHARACTER VARYING(255) not null,
    AUTHOR         CHARACTER VARYING(255) not null,
    START_DATE     DATE,
    END_DATE       DATE,
    PINNED         BOOLEAN default false,
    ATTACHMENT_URL CHARACTER VARYING(255),
    STATUS         CHARACTER VARYING(255) not null,
    primary key (BOARD_ID),
    constraint FK_NOTICE_BOARD foreign key (BOARD_ID) references ABSTRACT_BOARD (BOARD_ID)
);

create table NOTICE_FILE
(
    IS_THUMBNAIL    BOOLEAN not null,
    USED_IN_CONTENT BOOLEAN not null,
    FILE_ID         BIGINT  not null,
    NOTICE_ID       BIGINT,
    primary key (FILE_ID),
    constraint FK_NOTICE_FILE_ABSTRACT_FILE
        foreign key (FILE_ID) references ABSTRACT_FILE (FILE_ID),
    constraint FK_NOTICE_FILE_NOTICE
        foreign key (NOTICE_ID) references NOTICE (BOARD_ID)
);


create table RESOURCE
(
    RESOURCES_ID  BIGINT auto_increment
        primary key,
    HTTP_METHOD   CHARACTER VARYING(255) not null,
    ORDER_NUM     INTEGER,
    RESOURCE_DESC CHARACTER VARYING(255),
    RESOURCE_NAME CHARACTER VARYING(255) not null,
    RESOURCE_TYPE CHARACTER VARYING(255),
    constraint UK62R740BQG1K2K26H6WDSJWCR4
        unique (RESOURCE_NAME, HTTP_METHOD, RESOURCE_TYPE)
);

create table ROLE
(
    ROLES_ID  BIGINT auto_increment
        primary key,
    ROLE      CHARACTER VARYING(255) constraint UK_BJXN5II7V7YGWX39ET0WAWU0Q
            unique,
    ROLE_DESC CHARACTER VARYING(255)
);

create table MEMBER
(
    MEMBER_ID          BIGINT auto_increment
        primary key,
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_DATE TIMESTAMP,
    EMAIL              CHARACTER VARYING(255),
    LOGIN_ID           CHARACTER VARYING(255) constraint UK_ENFM5PATWJQULW8K4WWUO6F60
            unique,
    NAME               CHARACTER VARYING(255),
    PASSWORD           CHARACTER VARYING(255),
    PROVIDER           CHARACTER VARYING(255),
    PROVIDER_ID        CHARACTER VARYING(255),
    ROLE_ROLES_ID      BIGINT,
    constraint UK7BLITXWEIW758G32LG3BHAVF6
        unique (EMAIL, PROVIDER),
    constraint FKI4S5P3HQU42UE5VAPGO0B2Y4I
        foreign key (ROLE_ROLES_ID) references ROLE
);

create table ACCOUNT
(
    ACCOUNT_ID         BIGINT auto_increment
        primary key,
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_DATE TIMESTAMP,
    ACCOUNT_NUMBER     CHARACTER VARYING(255),
    BANK_NAME          CHARACTER VARYING(255),
    MEMBER_ID          BIGINT constraint UK_6E0FGGOUP5UCNLG21HWXMBL36
            unique,
    constraint FKR5J0HUYND7NSV1S7E9VB8QVWO
        foreign key (MEMBER_ID) references MEMBER
);

create table API_KEYS
(
    APIKEY_ID    BIGINT auto_increment
        primary key,
    API_KEY      CHARACTER VARYING(255) not null constraint UK_SGDKNGJ9U2LB7U4AR5GUWNPX2
            unique,
    PRICING_PLAN INTEGER                not null,
    MEMBER_ID    BIGINT,
    constraint FK8U8BMA84WHGO7Y00SCJ3KRDG1
        foreign key (MEMBER_ID) references MEMBER
);


create table BOARD_FILE
(
    BOARD_FILE_ID      BIGINT auto_increment
        primary key,
    EXTENSION          CHARACTER VARYING(255),
    FILE_NAME          CHARACTER VARYING(255),
    FILE_PATH          CHARACTER VARYING(255),
    ORIGINAL_FILE_NAME CHARACTER VARYING(255),
    SIZE               BIGINT,
    BOARD_ID           BIGINT,
    CHECKSUM           CHARACTER VARYING(255) not null,
    IS_PUBLIC          BOOLEAN                not null,
    MIME_TYPE          CHARACTER VARYING(255) not null,
    THUMBNAIL_URL      CHARACTER VARYING(255),
    UPLOAD_DATE        TIMESTAMP              not null,
    UPLOADED_BY        CHARACTER VARYING(255) not null,
    IMAGE_URL          CHARACTER VARYING(255) not null,
    constraint FK7Y5MP0LKSEHQT19IBJDX5XUFO
        foreign key (BOARD_ID) references ABSTRACT_BOARD
);

create table BOARD_HASH_TAG
(
    BOARD_HASHTAG_ID BIGINT auto_increment
        primary key,
    BOARD_ID         BIGINT,
    HASHTAG_ID       BIGINT,
    constraint FK9NTDQG7P34KFPL7642M4BHCFI
        foreign key (HASHTAG_ID) references HASH_TAG,
    constraint FKKGHPPB3XGWY9GKNMYQBBJS3LG
        foreign key (BOARD_ID) references ABSTRACT_BOARD
);

create table FREE_BOARD
(
    CREATE_DATE TIMESTAMP,
    PIN         INTEGER not null,
    SECRET      BOOLEAN not null,
    BOARD_ID    BIGINT  not null
        primary key,
    MEMBER_ID   BIGINT,
    constraint FKDXCXT1JNP0Q8S67K2JABHODWP
        foreign key (BOARD_ID) references ABSTRACT_BOARD,
    constraint FKQNUMS9O3FGD0Y3I15AS6DAXXD
        foreign key (MEMBER_ID) references MEMBER
);

create table LIKES
(
    LIKE_ID   BIGINT auto_increment
        primary key,
    BOARD_ID  BIGINT,
    MEMBER_ID BIGINT,
    constraint UKFWPCQLF7JJNLG0XAGS8NOQOC7
        unique (BOARD_ID, MEMBER_ID),
    constraint FKA4VKF1SKCFU5R6O5GFB5JF295
        foreign key (MEMBER_ID) references MEMBER,
    constraint FKPCCGP495O1VW93HO48PE31Y41
        foreign key (BOARD_ID) references ABSTRACT_BOARD
);

create table LOGIN_INFO
(
    ID          BIGINT auto_increment
        primary key,
    LOCALE      CHARACTER VARYING(255),
    LOGIN_TIME  TIMESTAMP,
    REMOTE_IP   CHARACTER VARYING(255),
    REQUEST_URI CHARACTER VARYING(255),
    SESSION_ID  CHARACTER VARYING(255),
    USER_AGENT  CHARACTER VARYING(255),
    MEMBER_ID   BIGINT,
    constraint FKPSLRGWNWVY7K36W2CY8PEA5UI
        foreign key (MEMBER_ID) references MEMBER
);

create table MEMBER_INFO
(
    MEMBER_INFO_ID BIGINT auto_increment
        primary key,
    BIO            CHARACTER VARYING(255) not null,
    NICK_NAME      CHARACTER VARYING(255) constraint UK_LCJNRHUOT6ADFQH8TRS7I7CUX
            unique,
    PHONE_NUMBER   CHARACTER VARYING(255),
    MEMBER_ID      BIGINT,
    constraint FKBPTTEAE7BFAA7OBI1OHS523M0
        foreign key (MEMBER_ID) references MEMBER
);

create table MEMBER_PROFILE_FILE
(
    MEMBER_PROFILE_FILE_ID BIGINT auto_increment
        primary key,
    CHECKSUM               CHARACTER VARYING(255) not null,
    EXTENSION              CHARACTER VARYING(255) not null,
    FILE_NAME              CHARACTER VARYING(255) not null,
    FILE_PATH              CHARACTER VARYING(255) not null,
    IMAGE_URL              CHARACTER VARYING(255) not null,
    IS_DEFAULT             BOOLEAN                not null,
    IS_PUBLIC              BOOLEAN                not null,
    MIME_TYPE              CHARACTER VARYING(255) not null,
    ORIGINAL_FILE_NAME     CHARACTER VARYING(255) not null,
    SIZE                   BIGINT                 not null,
    THUMBNAIL_URL          CHARACTER VARYING(255),
    UPLOAD_DATE            TIMESTAMP              not null,
    UPLOADED_BY            CHARACTER VARYING(255) not null,
    MEMBER_ID              BIGINT,
    constraint FKAMRBP28H6XX85GYMCWIEI9LQJ
        foreign key (MEMBER_ID) references MEMBER
);

create table NOTICE_READ_STATUS
(
    NOTICE_READ_STATUS_ID BIGINT auto_increment
        primary key,
    READ_AT               TIMESTAMP not null,
    MEMBER_ID             BIGINT    not null,
    NOTICE_ID             BIGINT    not null,
    constraint FK4OBMDHWHRJXV5C8HPWYH8GDM5
        foreign key (MEMBER_ID) references MEMBER
);

create table REPLY
(
    REPLY_ID           BIGINT auto_increment
        primary key,
    CREATED_BY         CHARACTER VARYING(255),
    LAST_MODIFIED_BY   CHARACTER VARYING(255),
    CONTENT            CHARACTER VARYING(255) not null,
    CREATE_DATE        TIMESTAMP,
    BOARD_ID           BIGINT,
    MEMBER_ID          BIGINT,
    PARENT_REPLY_ID    BIGINT,
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_DATE TIMESTAMP,
    DELETED            BOOLEAN,
    constraint FKA2AH1L9EA6D6KC1XGM30NA7KV
        foreign key (PARENT_REPLY_ID) references REPLY,
    constraint FKEN6VRMI5OTH4BG6YBFC202FMU
        foreign key (MEMBER_ID) references MEMBER,
    constraint FKJK1GJMDRTQBA9A43PDCJ113X6
        foreign key (BOARD_ID) references ABSTRACT_BOARD
);

create table ROLE_HIERARCHY
(
    ROLE_HIERARCHY_ID BIGINT auto_increment
        primary key,
    CHILD_NAME        CHARACTER VARYING(255) constraint UK_AUX81X2BB3GEOJTQ6MF8RS19L
            unique,
    PARENT_NAME       CHARACTER VARYING(255),
    constraint FK7NX52TGAR4FM7TK54JIL95OXB
        foreign key (PARENT_NAME) references ROLE_HIERARCHY (CHILD_NAME)
);

create table ROLE_RESOURCE
(
    ROLE_RESOURCE_ID BIGINT auto_increment
        primary key,
    RESOURCE_ID      BIGINT not null,
    ROLE_ID          BIGINT not null,
    constraint UKREK6FGC8Q53MEQ2UDCQ2Q6LH4
        unique (RESOURCE_ID, ROLE_ID),
    constraint FK7K960KK6PU1PWSK7ML4HYCP53
        foreign key (ROLE_ID) references ROLE,
    constraint FK9RV0ETS7XQC65MMPDICYKKHJ3
        foreign key (RESOURCE_ID) references RESOURCE
);

create table SHOP
(
    SHOP_ID            BIGINT auto_increment
        primary key,
    CREATED_BY         CHARACTER VARYING(255),
    LAST_MODIFIED_BY   CHARACTER VARYING(255),
    NAME               CHARACTER VARYING(10) not null constraint UK_KSA05NDH95N2BFDWB5WT68541
            unique,
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_DATE TIMESTAMP
);

create table ITEM
(
    ITEM_ID            BIGINT auto_increment
        primary key,
    CREATED_BY         CHARACTER VARYING(255),
    LAST_MODIFIED_BY   CHARACTER VARYING(255),
    NAME               CHARACTER VARYING(255),
    PRICE              INTEGER not null,
    SHOP_ID            BIGINT  not null,
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_DATE TIMESTAMP,
    constraint UK_NAME_SHOP
        unique (NAME, SHOP_ID),
    constraint FKSO5MQBN1H85IOP14AHCKKWAHH
        foreign key (SHOP_ID) references SHOP
);

create table ORDERS
(
    ORDER_ID        BIGINT auto_increment
        primary key,
    DELIVERY_FEE    INTEGER not null,
    LAST_ORDER_TIME TIMESTAMP,
    MEMO            CHARACTER VARYING(255),
    ORDER_DATE      TIMESTAMP,
    STATUS          CHARACTER VARYING(255),
    TITLE           CHARACTER VARYING(255),
    MEMBER_ID       BIGINT,
    SHOP_ID         BIGINT,
    constraint FKPKTXWHJ3X9M4GTH5FF6BKQGEB
        foreign key (MEMBER_ID) references MEMBER,
    constraint FKQN03KKO0738SEHAAL2GR2UXL6
        foreign key (SHOP_ID) references SHOP
);

create table GROUP_ORDER
(
    GROUP_ORDER_ID BIGINT auto_increment
        primary key,
    ORDER_DATE     TIMESTAMP,
    MEMBER_ID      BIGINT,
    ORDER_ID       BIGINT,
    constraint UK_ORDER_MEMBER
        unique (ORDER_ID, MEMBER_ID),
    constraint FK2HV41LC7K0WO3JT4W9G8MJR4A
        foreign key (ORDER_ID) references ORDERS,
    constraint FK9W809C5R0F7TXF2FHBYHNYXK4
        foreign key (MEMBER_ID) references MEMBER
);

create table ORDER_ITEM
(
    ORDER_ITEM_ID    BIGINT auto_increment
        primary key,
    ORDER_PRICE      INTEGER not null,
    ORDER_QUANTITY   INTEGER not null,
    ORDER_BUY_MEMBER BIGINT,
    ITEM_ID          BIGINT,
    ORDER_ID         BIGINT,
    constraint FK9GAA82780418KIVE9LAHENBXV
        foreign key (ORDER_BUY_MEMBER) references GROUP_ORDER,
    constraint FKIJA6HJJIIT8DPRNMVTVGDP6RU
        foreign key (ITEM_ID) references ITEM,
    constraint FKT4DC2R9NBVBUJRLJV3E23IIBT
        foreign key (ORDER_ID) references ORDERS
);

create table REVIEW
(
    REVIEW_ID          BIGINT auto_increment
        primary key,
    CREATED_BY         CHARACTER VARYING(255),
    LAST_MODIFIED_BY   CHARACTER VARYING(255),
    CONTENT            CHARACTER VARYING(255) not null,
    SHOP_RATING        INTEGER                not null,
    MEMBER_ID          BIGINT                 not null,
    ORDER_ID           BIGINT                 not null,
    SHOP_ID            BIGINT,
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_DATE TIMESTAMP,
    constraint UK3RS8JISXAEEW2DFX9M3QVX6FL
        unique (MEMBER_ID, ORDER_ID),
    constraint FKK0CCX5I4CI2WD70VEGUG074W1
        foreign key (MEMBER_ID) references MEMBER,
    constraint FKNKC5S3DA46CBX8OEQRFHNM7ES
        foreign key (ORDER_ID) references ORDERS,
    constraint FKQAKGR7LN2AOY4Q57H58JE8IDP
        foreign key (SHOP_ID) references SHOP,
    check (("SHOP_RATING" >= 0)
        AND ("SHOP_RATING" <= 5))
);

create table TOKEN_IGNORE_URLS
(
    TOKEN_IGNORE_ID BIGINT auto_increment
        primary key,
    IS_IGNORE       BOOLEAN                not null,
    URL             CHARACTER VARYING(255) not null
);

create table USER_SECURITY_STATUS
(
    USER_SECURITY_STATUS_ID    BIGINT auto_increment
        primary key,
    IS_ACCOUNT_NON_EXPIRED     BOOLEAN not null,
    IS_ACCOUNT_NON_LOCKED      BOOLEAN not null,
    IS_CREDENTIALS_NON_EXPIRED BOOLEAN not null,
    IS_ENABLED                 BOOLEAN not null,
    MEMBER_ID                  BIGINT,
    constraint FKFTEKKUUDDRJUFDA5Q7KYQJKSJ
        foreign key (MEMBER_ID) references MEMBER
);

