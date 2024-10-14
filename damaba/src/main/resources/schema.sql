DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS user_profile_image;

CREATE TABLE `user`
(
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    roles             VARCHAR(255) NOT NULL,
    login_type        VARCHAR(255) NOT NULL,
    o_auth_login_uid  VARCHAR(255) NOT NULL UNIQUE,
    nickname          VARCHAR(255) NOT NULL UNIQUE,
    profile_image_url VARCHAR(255) NOT NULL,
    gender            VARCHAR(255) NOT NULL,
    age               INT          NOT NULL,
    instagram_id      VARCHAR(255),
    created_at        DATETIME     NOT NULL,
    updated_at        DATETIME     NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx__user__o_auth_login_uid ON `user` (o_auth_login_uid);

CREATE TABLE user_profile_image
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    url         VARCHAR(255) NOT NULL UNIQUE,
    stored_name VARCHAR(255) NOT NULL,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,
    deleted_at  DATETIME     NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx__user_profile_image__url ON user_profile_image (url);
