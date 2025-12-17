-- ============================================
-- V8: Membership System Tables
-- ============================================

-- Membership Tiers table
CREATE TABLE membership_tiers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    rank_order INTEGER NOT NULL UNIQUE,
    spending_required DECIMAL(10, 2) NOT NULL,
    discount_percent DECIMAL(5, 2) DEFAULT 0,
    points_multiplier DECIMAL(3, 2) DEFAULT 1.0,
    image_url VARCHAR(500),
    description TEXT
);

-- User Memberships table
CREATE TABLE user_memberships (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id),
    tier_id INTEGER NOT NULL REFERENCES membership_tiers (id),
    total_spending DECIMAL(10, 2) DEFAULT 0,
    points INTEGER DEFAULT 0,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id)
);
