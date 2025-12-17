-- ============================================
-- V7: Booking & Payment Tables
-- ============================================

-- Bookings table
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

-- Booking Items table
CREATE TABLE booking_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings (id) ON DELETE CASCADE,
    seat_id BIGINT NOT NULL REFERENCES seats (id) ON DELETE RESTRICT,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_booking_items_booking_seat UNIQUE (booking_id, seat_id)
);

-- Seat Locks table
CREATE TABLE seat_locks (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    showtime_id BIGINT NOT NULL REFERENCES showtimes (id) ON DELETE CASCADE,
    seat_id BIGINT NOT NULL REFERENCES seats (id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    locked_until TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_seat_locks_showtime_seat UNIQUE (showtime_id, seat_id)
);

-- Indexes
CREATE INDEX idx_bookings_user_id ON bookings (user_id);
CREATE INDEX idx_bookings_status ON bookings (status);
CREATE INDEX idx_bookings_created_at ON bookings (created_at DESC);
CREATE INDEX idx_booking_items_seat_id ON booking_items (seat_id);
CREATE INDEX idx_seat_locks_locked_until ON seat_locks (locked_until);
