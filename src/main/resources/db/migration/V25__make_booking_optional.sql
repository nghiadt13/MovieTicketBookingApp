-- ============================================
-- V25: Make booking_id optional for reviews
-- Cho phép user viết review mà không cần booking
-- ============================================

ALTER TABLE movie_reviews ALTER COLUMN booking_id DROP NOT NULL;
