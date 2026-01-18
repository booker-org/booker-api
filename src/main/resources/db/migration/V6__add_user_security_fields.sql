ALTER TABLE users
  ADD COLUMN role               VARCHAR(20) NOT NULL DEFAULT 'USER',
  ADD COLUMN enabled            BOOLEAN     NOT NULL DEFAULT TRUE,
  ADD COLUMN account_non_locked BOOLEAN     NOT NULL DEFAULT TRUE
;

ALTER TABLE users
  ADD CONSTRAINT users_role_check CHECK(role IN ('USER', 'ADMIN'))
;