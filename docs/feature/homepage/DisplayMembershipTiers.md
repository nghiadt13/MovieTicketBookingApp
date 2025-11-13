sql :
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

This table is now first using for displaying all tiers that a user can reach in homepage, so the api : /api/membership-tiers will return id, name, image_url , description when being called.
