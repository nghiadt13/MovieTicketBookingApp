-- Create extension for case-insensitive text
CREATE EXTENSION IF NOT EXISTS citext;

-- Create enum for movie status
CREATE TYPE movie_status_enum AS ENUM ('COMING_SOON','NOW_SHOWING','ENDED');

-- Create movies table
CREATE TABLE movies (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title TEXT NOT NULL,
    synopsis TEXT,
    duration_min SMALLINT CHECK (duration_min > 0),
    release_date DATE,
    status movie_status_enum NOT NULL DEFAULT 'COMING_SOON',
    poster_url TEXT,
    trailer_url TEXT,
    rating_avg NUMERIC(3,1) NOT NULL DEFAULT 0 CHECK (rating_avg BETWEEN 0 AND 10),
    rating_count INTEGER NOT NULL DEFAULT 0 CHECK (rating_count >= 0),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$
BEGIN 
    NEW.updated_at = now(); 
    RETURN NEW; 
END;
$$ LANGUAGE plpgsql;

-- Create trigger for movies table
CREATE TRIGGER trg_movies_updated_at
    BEFORE UPDATE ON movies
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Create genres table
CREATE TABLE genres (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    slug CITEXT NOT NULL,
    CONSTRAINT uq_genres_name UNIQUE (name),
    CONSTRAINT uq_genres_slug UNIQUE (slug)
);

-- Create formats table
CREATE TABLE formats (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code TEXT NOT NULL,
    label TEXT NOT NULL,
    CONSTRAINT uq_formats_code UNIQUE (code)
);

-- Create movie_genres junction table
CREATE TABLE movie_genres (
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    genre_id BIGINT NOT NULL REFERENCES genres(id) ON DELETE RESTRICT,
    PRIMARY KEY (movie_id, genre_id)
);

-- Create movie_formats junction table
CREATE TABLE movie_formats (
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    format_id BIGINT NOT NULL REFERENCES formats(id) ON DELETE RESTRICT,
    PRIMARY KEY (movie_id, format_id)
);

-- Create indexes for better performance
CREATE INDEX idx_movies_status ON movies(status);
CREATE INDEX idx_movies_active ON movies(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_movie_genres_genre ON movie_genres(genre_id);
CREATE INDEX idx_movie_formats_format ON movie_formats(format_id);