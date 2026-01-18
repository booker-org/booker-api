-- Add additional performance indexes
-- Books table - additional index for sorting
CREATE INDEX IF NOT EXISTS idx_books_created_at ON books(created_at DESC);

-- Users table indexes
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at DESC);

-- Refresh tokens table - additional index for cleanup queries
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_revoked ON refresh_tokens(revoked)
WHERE revoked = false;