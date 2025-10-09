-- V3: Optimized and Comprehensive Schema for Users, Roles, Social Logins, and OTPs.
-- This version includes account verification status and a dedicated OTP purpose for registration.

-- Define ENUM types for clarity and data integrity
CREATE TYPE user_role_enum AS ENUM ('ROLE_USER', 'ROLE_ADMIN', 'ROLE_STAFF');
CREATE TYPE social_provider_enum AS ENUM ('GOOGLE', 'FACEBOOK', 'X', 'INSTAGRAM');
CREATE TYPE otp_channel_enum AS ENUM ('EMAIL', 'SMS');

-- ENHANCEMENT: Added 'ACCOUNT_VERIFICATION' for new user sign-ups.
CREATE TYPE otp_purpose_enum AS ENUM ('PASSWORD_RESET', 'TWO_FACTOR_AUTH', 'ACCOUNT_VERIFICATION');

-- 1. Core 'users' table
-- Flexible enough to support registration/login via email or phone number.
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email CITEXT UNIQUE,
    phone_number CITEXT UNIQUE,
    password_hash TEXT, -- Can be NULL for users who only use social login
    display_name TEXT NOT NULL,
    avatar_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- ENHANCEMENT: Track verification status for email and phone.
    -- This is useful for restricting access to certain features until the user is verified.
    email_verified_at TIMESTAMPTZ,
    phone_number_verified_at TIMESTAMPTZ,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    -- A user must have at least an email or a phone number to register
    CHECK (email IS NOT NULL OR phone_number IS NOT NULL)
);

-- Function to automatically update the 'updated_at' timestamp
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to execute the function on update
CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- 2. 'roles' and 'user_roles' tables for Role-Based Access Control (RBAC)
-- Essential for differentiating between user types (e.g., customer vs. admin).
CREATE TABLE roles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name user_role_enum NOT NULL,
    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
    PRIMARY KEY (user_id, role_id)
);

-- 3. 'social_accounts' table
-- Securely links user accounts to third-party providers for authentication purposes only.
CREATE TABLE social_accounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider social_provider_enum NOT NULL,
    provider_user_id TEXT NOT NULL, -- The unique ID from the social provider (e.g., Google's 'sub' claim)
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    -- A user can only link one account per provider
    CONSTRAINT uq_social_accounts_user_provider UNIQUE (user_id, provider),
    -- A specific social account can only be linked to one user in our system
    CONSTRAINT uq_social_accounts_provider_user_id UNIQUE (provider, provider_user_id)
);

-- 4. 'user_otps' table
-- A flexible and secure table for handling One-Time Passwords for various purposes.
CREATE TABLE user_otps (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    purpose otp_purpose_enum NOT NULL,
    channel otp_channel_enum NOT NULL,
    contact_value TEXT NOT NULL,          -- The email or phone number where the OTP was sent
    code_hash TEXT NOT NULL,              -- For security, always store a hash of the OTP
    expires_at TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ,              -- Timestamp when the OTP was used, preventing reuse
    attempt_count SMALLINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (expires_at > created_at)
);

-- 5. Indexes for better query performance
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_social_accounts_user_id ON social_accounts(user_id);
CREATE INDEX idx_user_otps_lookup ON user_otps (user_id, purpose) WHERE consumed_at IS NULL;

-- 6. Seed the 'roles' table with initial, essential roles
INSERT INTO roles(name) VALUES ('ROLE_USER'), ('ROLE_ADMIN'), ('ROLE_STAFF');