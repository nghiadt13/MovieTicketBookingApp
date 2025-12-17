-- ============================================
-- V5: Cinema & Screening Tables
-- ============================================

-- Cinemas table
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

-- Screens table
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

-- Seats table
CREATE TABLE seats (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    screen_id BIGINT NOT NULL REFERENCES screens (id) ON DELETE CASCADE,
    row_name TEXT NOT NULL,
    seat_number SMALLINT NOT NULL,
    seat_type seat_type_enum NOT NULL DEFAULT 'STANDARD',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_seats_screen_row_number UNIQUE (screen_id, row_name, seat_number)
);
