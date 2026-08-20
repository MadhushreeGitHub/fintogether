-- V2__refresh_tokens.sql
-- refresh_tokens table for storing refresh tokens associated with users.

CREATE TABLE refresh_tokens
(
    id         UUID PRIMARY KEY,
    user_id    UUID        NOT NULL REFERENCES users (id),
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    family_id  UUID        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ
);

CREATE INDEX idx_refresh_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_family_id ON refresh_tokens (family_id);


COMMENT
ON TABLE refresh_tokens IS
        'Rotating refresh tokens with family tracking for RTR attack detection';
COMMENT
ON COLUMN refresh_tokens.token_hash IS
        'SHA-256 hex digest of raw token; raw never stored';
COMMENT
ON COLUMN refresh_tokens.family_id IS
        'Groups tokens issued from a single login; enables family invalidation on reuse';
COMMENT
ON COLUMN refresh_tokens.revoked_at IS
        'NULL = live token; set when rotated or explicitly revoked; row retained for audit';