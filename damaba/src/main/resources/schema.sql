DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS user_profile_image;
DROP TABLE IF EXISTS promotion;
DROP TABLE IF EXISTS promotion_image;
DROP TABLE IF EXISTS promotion_active_region;
DROP TABLE IF EXISTS promotion_hashtag;

CREATE TABLE `user`
(
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    roles             VARCHAR(255) NOT NULL,
    login_type        VARCHAR(255) NOT NULL,
    o_auth_login_uid  VARCHAR(255) NOT NULL UNIQUE,
    nickname          VARCHAR(7)   NOT NULL UNIQUE,
    profile_image_url VARCHAR(255) NOT NULL,
    gender            VARCHAR(255) NOT NULL,
    instagram_id      VARCHAR(30),
    created_at        DATETIME     NOT NULL,
    updated_at        DATETIME     NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx__user__o_auth_login_uid ON `user` (o_auth_login_uid);

CREATE TABLE user_profile_image
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL COMMENT '(FK) id of user',
    name       VARCHAR(255) NOT NULL UNIQUE,
    url        VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    deleted_at DATETIME,
    created_by BIGINT       NOT NULL,
    updated_by BIGINT       NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx__user_profile_image__url ON user_profile_image (url);

CREATE TABLE promotion
(
    id                        BIGINT       NOT NULL AUTO_INCREMENT,
    author_id                 BIGINT COMMENT '(FK) id of user(author)',
    type                      VARCHAR(255) NOT NULL,
    event_type                VARCHAR(255) NOT NULL,
    title                     VARCHAR(20)  NOT NULL,
    content                   VARCHAR(500) NOT NULL,
    sido                      VARCHAR(255) NOT NULL,
    sigungu                   VARCHAR(255) NOT NULL,
    road_address              VARCHAR(255) NOT NULL,
    jibun_address             VARCHAR(255) NOT NULL,
    external_link             VARCHAR(255),
    started_at                DATE,
    ended_at                  DATE,
    photographer_name         VARCHAR(255),
    photographer_instagram_id VARCHAR(255),
    created_at                DATETIME     NOT NULL,
    updated_at                DATETIME     NOT NULL,
    deleted_at                DATETIME,
    created_by                BIGINT       NOT NULL,
    updated_by                BIGINT       NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE promotion_image
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    promotion_id BIGINT       NOT NULL COMMENT '(FK) id of promotion',
    name         VARCHAR(255) NOT NULL UNIQUE,
    url          VARCHAR(255) NOT NULL UNIQUE,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    created_by   BIGINT       NOT NULL,
    updated_by   BIGINT       NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE promotion_active_region
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    promotion_id BIGINT       NOT NULL COMMENT '(FK) id of promotion',
    category     VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX fk_idx__promotion_active_region__promotion_id ON promotion_active_region (promotion_id);

CREATE TABLE promotion_hashtag
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    promotion_id BIGINT       NOT NULL COMMENT '(FK) id of promotion',
    content      VARCHAR(255) NOT NULL,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX fk_idx__promotion_hashtag__promotion_id ON promotion_hashtag (promotion_id);
