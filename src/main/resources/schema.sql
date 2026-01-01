DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS photographer;
DROP TABLE IF EXISTS photographer_photography_type;
DROP TABLE IF EXISTS photographer_active_region;
DROP TABLE IF EXISTS photographer_portfolio_image;
DROP TABLE IF EXISTS user_profile_image;
DROP TABLE IF EXISTS photographer_save;
DROP TABLE IF EXISTS promotion;
DROP TABLE IF EXISTS promotion_photography_type;
DROP TABLE IF EXISTS promotion_image;
DROP TABLE IF EXISTS promotion_active_region;
DROP TABLE IF EXISTS promotion_hashtag;
DROP TABLE IF EXISTS promotion_save;

CREATE TABLE `user`
(
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    type               VARCHAR(255) NOT NULL,
    roles              VARCHAR(255) NOT NULL,
    login_type         VARCHAR(255) NOT NULL,
    o_auth_login_uid   VARCHAR(255) NOT NULL UNIQUE,
    nickname           VARCHAR(18)  NOT NULL UNIQUE,
    profile_image_name VARCHAR(255),
    profile_image_url  VARCHAR(255),
    gender             VARCHAR(255) NOT NULL,
    instagram_id       VARCHAR(30),
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx__user__o_auth_login_uid ON `user` (o_auth_login_uid);

CREATE TABLE photographer
(
    user_id       BIGINT    NOT NULL COMMENT '(FK) id of user',
    contact_link  VARCHAR(255),
    description   VARCHAR(500),
    sido          VARCHAR(255),
    sigungu       VARCHAR(255),
    road_address  VARCHAR(255),
    jibun_address VARCHAR(255),
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id)
);
CREATE INDEX fk_idx__photographer__user_id ON photographer (user_id);

CREATE TABLE photographer_photography_type
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    photographer_id  BIGINT       NOT NULL COMMENT '(FK) id of photographer',
    photography_type VARCHAR(255) NOT NULL,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX fk_idx__photographer_photography_type__photographer_id ON photographer_photography_type (photographer_id);

CREATE TABLE photographer_active_region
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    photographer_id BIGINT COMMENT '(FK) id of photographer',
    category        VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX fk_idx__photographer_active_region__photographer_id ON photographer_active_region (photographer_id);

CREATE TABLE photographer_portfolio_image
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    photographer_id BIGINT COMMENT '(FK) id of photographer',
    name            VARCHAR(255) NOT NULL,
    url             VARCHAR(255) NOT NULL UNIQUE,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    deleted_at      TIMESTAMP,
    created_by      BIGINT       NOT NULL,
    updated_by      BIGINT       NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX fk_idx__photographer_portfolio__photographer_id ON photographer_active_region (photographer_id);

CREATE TABLE user_profile_image
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL COMMENT '(FK) id of user',
    name       VARCHAR(255) NOT NULL,
    url        VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    deleted_at TIMESTAMP,
    created_by BIGINT       NOT NULL,
    updated_by BIGINT       NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx__user_profile_image__url ON user_profile_image (url);

CREATE TABLE photographer_save
(
    id              BIGINT    NOT NULL AUTO_INCREMENT,
    user_id         BIGINT    NOT NULL COMMENT '(FK) id of request user',
    photographer_id BIGINT    NOT NULL COMMENT '(FK) id of photographer(user)',
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx__photographer_save__user_id ON photographer_save (user_id);
CREATE INDEX idx__photographer_save__photographer_id ON photographer_save (photographer_id);

CREATE TABLE promotion
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    author_id        BIGINT COMMENT '(FK) id of user(author)',
    is_author_hidden BOOLEAN      NOT NULL DEFAULT FALSE,
    promotion_type   VARCHAR(255) NOT NULL,
    title            VARCHAR(20)  NOT NULL,
    content          VARCHAR(500) NOT NULL,
    external_link    VARCHAR(255),
    started_at       DATE,
    ended_at         DATE,
    view_count       BIGINT       NOT NULL DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL,
    deleted_at       TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE promotion_photography_type
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    promotion_id BIGINT       NOT NULL COMMENT '(FK) id of promotion',
    type         VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX fk_idx__promotion_photography_type__promotion_id ON promotion_photography_type (promotion_id);

CREATE TABLE promotion_image
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    promotion_id BIGINT       NOT NULL COMMENT '(FK) id of promotion',
    name         VARCHAR(255) NOT NULL,
    url          VARCHAR(255) NOT NULL UNIQUE,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    deleted_at   TIMESTAMP,
    created_by   BIGINT       NOT NULL,
    updated_by   BIGINT       NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX fk_idx__promotion_image__promotion_id ON promotion_image (promotion_id);

CREATE TABLE promotion_active_region
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    promotion_id BIGINT       NOT NULL COMMENT '(FK) id of promotion',
    category     VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX fk_idx__promotion_active_region__promotion_id ON promotion_active_region (promotion_id);

CREATE TABLE promotion_hashtag
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    promotion_id BIGINT       NOT NULL COMMENT '(FK) id of promotion',
    content      VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX fk_idx__promotion_hashtag__promotion_id ON promotion_hashtag (promotion_id);

CREATE TABLE promotion_save
(
    id           BIGINT    NOT NULL AUTO_INCREMENT,
    user_id      BIGINT    NOT NULL COMMENT '(FK) id of user',
    promotion_id BIGINT    NOT NULL COMMENT '(FK) id of promotion',
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX fk_idx__promotion_save__user_id ON promotion_save (user_id);
CREATE INDEX fk_idx__promotion_save__promotion_id ON promotion_save (promotion_id);
