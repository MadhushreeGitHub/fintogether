-- V1__init.sql
-- Initial schema baseline.


CREATE EXTENSION IF NOT EXISTS citext;
CREATE TABLE users (
    id              UUID        PRIMARY KEY,
    email           CITEXT      NOT NULL UNIQUE,
    phone           VARCHAR(16) NOT NULL UNIQUE,
    user_name       VARCHAR(50) NOT NULL UNIQUE,
    password_hash   VARCHAR(60) NOT NULL,
    role            VARCHAR(20) NOT NULL CHECK (role IN ('EARNER', 'SAVER')),
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    deleted_at      TIMESTAMPTZ
);