DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    roles            VARCHAR(255) NOT NULL,
    o_auth_login_uid VARCHAR(255) NOT NULL,
    login_type       VARCHAR(255) NOT NULL,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL,
    deleted_at       DATETIME,
    PRIMARY KEY (id)
)
