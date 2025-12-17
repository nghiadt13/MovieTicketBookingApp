-- ============================================
-- V22: Add format_id column to showtimes table
-- ============================================

ALTER TABLE showtimes ADD COLUMN format_id BIGINT REFERENCES formats(id);
