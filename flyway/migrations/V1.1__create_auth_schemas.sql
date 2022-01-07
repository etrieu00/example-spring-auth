CREATE TABLE example_auth.app_user
(
    id            BIGSERIAL PRIMARY KEY,
    uuid          VARCHAR(36) UNIQUE NOT NULL,
    user_email    VARCHAR(254)       NOT NULL,
    user_password VARCHAR(64)        NOT NULL,
    ufc           VARCHAR(36)        NOT NULL,
    ulm           VARCHAR(36)        NOT NULL,
    dtc           TIMESTAMP          NOT NULL,
    dtm           TIMESTAMP          NOT NULL
);

CREATE TABLE example_auth.app_role
(
    id        BIGSERIAL PRIMARY KEY,
    app_user  BIGINT REFERENCES app_user (id),
    app_level INT         NOT NULL,
    role_name VARCHAR(20) NOT NULL
);

CREATE TABLE example_auth.app_profile
(
    id            BIGSERIAL PRIMARY KEY,
    app_user      BIGINT REFERENCES app_user (id),
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    phone_number  VARCHAR(15),
    gender        VARCHAR(6) DEFAULT 'NONE',
    date_of_birth DATE
);
