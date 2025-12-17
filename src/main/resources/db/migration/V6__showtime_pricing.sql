-- ============================================
-- V6: Showtime & Pricing Tables
-- ============================================

-- Showtimes table
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

-- Ticket Prices table
CREATE TABLE ticket_prices (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    showtime_id BIGINT NOT NULL REFERENCES showtimes (id) ON DELETE CASCADE,
    seat_type seat_type_enum NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_ticket_prices_showtime_seat_type UNIQUE (showtime_id, seat_type)
);

-- Indexes
CREATE INDEX idx_showtimes_movie_start_time ON showtimes (movie_id, start_time);
CREATE INDEX idx_showtimes_screen_start_time ON showtimes (screen_id, start_time);
CREATE INDEX idx_showtimes_status ON showtimes (status) WHERE is_active = TRUE;
