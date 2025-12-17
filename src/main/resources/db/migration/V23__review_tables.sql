-- ============================================
-- V23: Review System Tables
-- ============================================

-- Enum for review status
CREATE TYPE review_status_enum AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'HIDDEN');

-- Movie reviews table
CREATE TABLE movie_reviews (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    booking_id BIGINT NOT NULL REFERENCES bookings(id),
    rating SMALLINT NOT NULL CHECK (rating >= 1 AND rating <= 10),
    comment_text TEXT,
    status review_status_enum NOT NULL DEFAULT 'APPROVED',
    is_spoiler BOOLEAN NOT NULL DEFAULT FALSE,
    helpful_count INT NOT NULL DEFAULT 0,
    is_edited BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_user_movie_review UNIQUE (user_id, movie_id)
);

-- Trigger for updated_at
CREATE TRIGGER trg_movie_reviews_updated_at
    BEFORE UPDATE ON movie_reviews
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Review images table
CREATE TABLE review_images (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    review_id BIGINT NOT NULL REFERENCES movie_reviews(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    display_order SMALLINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Helpful votes table
CREATE TABLE review_helpful_votes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    review_id BIGINT NOT NULL REFERENCES movie_reviews(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_review_user_vote UNIQUE (review_id, user_id)
);

-- Indexes for performance
CREATE INDEX idx_reviews_movie_status ON movie_reviews(movie_id, status) WHERE deleted_at IS NULL;
CREATE INDEX idx_reviews_user ON movie_reviews(user_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_reviews_helpful ON movie_reviews(helpful_count DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_review_images_review ON review_images(review_id);
CREATE INDEX idx_review_votes_review ON review_helpful_votes(review_id);
