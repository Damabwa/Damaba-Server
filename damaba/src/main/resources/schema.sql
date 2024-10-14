DROP TABLE IF EXISTS `user`;

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
