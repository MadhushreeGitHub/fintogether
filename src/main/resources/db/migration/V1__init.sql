-- V1__init.sql
-- Initial schema baseline.

CREATE TABLE schema_meta (
                             id          SERIAL PRIMARY KEY,
                             created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                             note        TEXT
);

INSERT INTO schema_meta (note) VALUES ('Initial baseline for FinTogether schema');