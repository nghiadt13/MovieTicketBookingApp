-- ============================================
-- MOVIE BOOKING SYSTEM - COMPLETE DATABASE SCHEMA
-- ============================================
-- Version: 1.0
-- Description: Tổng hợp schema cho hệ thống đặt vé xem phim
-- Includes: Movies, Users, Cinemas, Bookings, News, Membership Tiers, Carousel
-- ============================================

-- ============================================
-- PHẦN 1: EXTENSIONS & UTILITY FUNCTIONS
-- ============================================

-- Create extension for case-insensitive text
CREATE EXTENSION IF NOT EXISTS citext;

-- Function to automatically update the 'updated_at' timestamp
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- PHẦN 2: ENUM TYPES
-- ============================================

-- Movie related enums
CREATE TYPE movie_status_enum AS ENUM ('COMING_SOON','NOW_SHOWING','ENDED');

-- User related enums
CREATE TYPE user_role_enum AS ENUM ('ROLE_USER', 'ROLE_ADMIN', 'ROLE_STAFF');

CREATE TYPE social_provider_enum AS ENUM ('GOOGLE', 'FACEBOOK', 'X', 'INSTAGRAM');

CREATE TYPE otp_channel_enum AS ENUM ('EMAIL', 'SMS');

CREATE TYPE otp_purpose_enum AS ENUM ('PASSWORD_RESET', 'TWO_FACTOR_AUTH', 'ACCOUNT_VERIFICATION');

-- Cinema & Booking related enums
CREATE TYPE seat_type_enum AS ENUM ('STANDARD', 'VIP', 'COUPLE', 'DELUXE');

CREATE TYPE showtime_status_enum AS ENUM ('SCHEDULED', 'SELLING', 'FULL', 'COMPLETED', 'CANCELLED');

CREATE TYPE booking_status_enum AS ENUM (
    'PENDING',      -- Đang chờ thanh toán
    'CONFIRMED',    -- Đã thanh toán thành công
    'CANCELLED',    -- Đã hủy
    'EXPIRED'       -- Hết hạn (không thanh toán trong thời gian quy định)
);

-- ============================================
-- PHẦN 3: USER MANAGEMENT
-- ============================================

-- 3.1. Core 'users' table
-- Flexible enough to support registration/login via email or phone number.
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email CITEXT UNIQUE,
    phone_number CITEXT UNIQUE,
    password_hash TEXT, -- Can be NULL for users who only use social login
    display_name TEXT NOT NULL,
    avatar_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified_at TIMESTAMPTZ,
    phone_number_verified_at TIMESTAMPTZ,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (
        email IS NOT NULL
        OR phone_number IS NOT NULL
    )
);

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- 3.2. 'roles' and 'user_roles' tables for RBAC
CREATE TABLE roles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name user_role_enum NOT NULL,
    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles (id) ON DELETE RESTRICT,
    PRIMARY KEY (user_id, role_id)
);

-- 3.3. 'social_accounts' table
CREATE TABLE social_accounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    provider social_provider_enum NOT NULL,
    provider_user_id TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_social_accounts_user_provider UNIQUE (user_id, provider),
    CONSTRAINT uq_social_accounts_provider_user_id UNIQUE (provider, provider_user_id)
);

-- 3.4. 'user_otps' table
CREATE TABLE user_otps (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    purpose otp_purpose_enum NOT NULL,
    channel otp_channel_enum NOT NULL,
    contact_value TEXT NOT NULL,
    code_hash TEXT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ,
    attempt_count SMALLINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (expires_at > created_at)
);

-- ============================================
-- PHẦN 4: MOVIE CATALOG
-- ============================================

-- 4.1. Movies table
CREATE TABLE movies (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title TEXT NOT NULL,
    synopsis TEXT,
    duration_min SMALLINT CHECK (duration_min > 0),
    release_date DATE,
    status movie_status_enum NOT NULL DEFAULT 'COMING_SOON',
    poster_url TEXT,
    trailer_url TEXT,
    rating_avg NUMERIC(3, 1) NOT NULL DEFAULT 0 CHECK (rating_avg BETWEEN 0 AND 10),
    rating_count INTEGER NOT NULL DEFAULT 0 CHECK (rating_count >= 0),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TRIGGER trg_movies_updated_at
    BEFORE UPDATE ON movies
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- 4.2. Genres table
CREATE TABLE genres (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    slug CITEXT NOT NULL,
    CONSTRAINT uq_genres_name UNIQUE (name),
    CONSTRAINT uq_genres_slug UNIQUE (slug)
);

-- 4.3. Formats table
CREATE TABLE formats (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code TEXT NOT NULL,
    label TEXT NOT NULL,
    CONSTRAINT uq_formats_code UNIQUE (code)
);

-- 4.4. Movie-Genres junction table
CREATE TABLE movie_genres (
    movie_id BIGINT NOT NULL REFERENCES movies (id) ON DELETE CASCADE,
    genre_id BIGINT NOT NULL REFERENCES genres (id) ON DELETE RESTRICT,
    PRIMARY KEY (movie_id, genre_id)
);

-- 4.5. Movie-Formats junction table
CREATE TABLE movie_formats (
    movie_id BIGINT NOT NULL REFERENCES movies (id) ON DELETE CASCADE,
    format_id BIGINT NOT NULL REFERENCES formats (id) ON DELETE RESTRICT,
    PRIMARY KEY (movie_id, format_id)
);

-- ============================================
-- PHẦN 5: CINEMA & SCREENING
-- ============================================

-- 5.1. Cinemas table
CREATE TABLE cinemas (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    city TEXT NOT NULL,
    district TEXT,
    phone_number TEXT,
    email TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TRIGGER trg_cinemas_updated_at
    BEFORE UPDATE ON cinemas
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- 5.2. Screens table
CREATE TABLE screens (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cinema_id BIGINT NOT NULL REFERENCES cinemas (id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    total_seats SMALLINT NOT NULL CHECK (total_seats > 0),
    screen_type TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_screens_cinema_name UNIQUE (cinema_id, name)
);

CREATE TRIGGER trg_screens_updated_at
    BEFORE UPDATE ON screens
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- 5.3. Seats table
CREATE TABLE seats (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    screen_id BIGINT NOT NULL REFERENCES screens (id) ON DELETE CASCADE,
    row_name TEXT NOT NULL,
    seat_number SMALLINT NOT NULL,
    seat_type seat_type_enum NOT NULL DEFAULT 'STANDARD',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_seats_screen_row_number UNIQUE (
        screen_id,
        row_name,
        seat_number
    )
);

-- ============================================
-- PHẦN 6: SHOWTIME & PRICING
-- ============================================

-- 6.1. Showtimes table
CREATE TABLE showtimes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies (id) ON DELETE RESTRICT,
    screen_id BIGINT NOT NULL REFERENCES screens (id) ON DELETE RESTRICT,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    status showtime_status_enum NOT NULL DEFAULT 'SCHEDULED',
    available_seats SMALLINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (end_time > start_time),
    CHECK (available_seats >= 0)
);

CREATE TRIGGER trg_showtimes_updated_at
    BEFORE UPDATE ON showtimes
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- 6.2. Ticket Prices table
CREATE TABLE ticket_prices (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    showtime_id BIGINT NOT NULL REFERENCES showtimes (id) ON DELETE CASCADE,
    seat_type seat_type_enum NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_ticket_prices_showtime_seat_type UNIQUE (showtime_id, seat_type)
);

-- ============================================
-- PHẦN 7: BOOKING & PAYMENT
-- ============================================

-- 7.1. Bookings table
CREATE TABLE bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_code TEXT NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    showtime_id BIGINT NOT NULL REFERENCES showtimes (id) ON DELETE RESTRICT,
    status booking_status_enum NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    payment_method TEXT,
    payment_transaction_id TEXT,
    paid_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (expires_at > created_at)
);

CREATE TRIGGER trg_bookings_updated_at
    BEFORE UPDATE ON bookings
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- 7.2. Booking Items table
CREATE TABLE booking_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings (id) ON DELETE CASCADE,
    seat_id BIGINT NOT NULL REFERENCES seats (id) ON DELETE RESTRICT,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_booking_items_booking_seat UNIQUE (booking_id, seat_id)
);

-- 7.3. Seat Locks table
CREATE TABLE seat_locks (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    showtime_id BIGINT NOT NULL REFERENCES showtimes (id) ON DELETE CASCADE,
    seat_id BIGINT NOT NULL REFERENCES seats (id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    locked_until TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_seat_locks_showtime_seat UNIQUE (showtime_id, seat_id)
);

-- ============================================
-- PHẦN 8: MEMBERSHIP SYSTEM
-- ============================================

-- 8.1. Membership Tiers table
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

-- 8.2. User Memberships table
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

-- ============================================
-- PHẦN 9: CONTENT MANAGEMENT
-- ============================================

-- 9.1. News table
CREATE TABLE news (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    image_url TEXT,
    published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9.2. Carousel Items table
CREATE TABLE carousel_items (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    content TEXT,
    target_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================
-- PHẦN 10: INDEXES FOR PERFORMANCE
-- ============================================

-- User indexes
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

CREATE INDEX idx_social_accounts_user_id ON social_accounts (user_id);

CREATE INDEX idx_user_otps_lookup ON user_otps (user_id, purpose)
WHERE
    consumed_at IS NULL;

-- Movie indexes
CREATE INDEX idx_movies_status ON movies (status);

CREATE INDEX idx_movies_active ON movies (is_active)
WHERE
    is_active = TRUE;

CREATE INDEX idx_movie_genres_genre ON movie_genres (genre_id);

CREATE INDEX idx_movie_formats_format ON movie_formats (format_id);

-- Showtime indexes
CREATE INDEX idx_showtimes_movie_start_time ON showtimes (movie_id, start_time);

CREATE INDEX idx_showtimes_screen_start_time ON showtimes (screen_id, start_time);

CREATE INDEX idx_showtimes_status ON showtimes (status)
WHERE
    is_active = TRUE;

-- Booking indexes
CREATE INDEX idx_bookings_user_id ON bookings (user_id);

CREATE INDEX idx_bookings_status ON bookings (status);

CREATE INDEX idx_bookings_created_at ON bookings (created_at DESC);

CREATE INDEX idx_booking_items_seat_id ON booking_items (seat_id);

-- Seat lock indexes
CREATE INDEX idx_seat_locks_locked_until ON seat_locks (locked_until);

-- ============================================
-- PHẦN 11: SEED DATA
-- ============================================

-- Seed roles
INSERT INTO
    roles (name)
VALUES ('ROLE_USER'),
    ('ROLE_ADMIN'),
    ('ROLE_STAFF')
ON CONFLICT (name) DO NOTHING; 

-- ============================================
-- END OF SCHEMA
-- ============================================