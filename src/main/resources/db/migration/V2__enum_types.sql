-- ============================================
-- V2: Enum Types
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
CREATE TYPE booking_status_enum AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED');
