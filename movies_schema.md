# Cấu trúc Database: Movies App

## 1. Bảng `movies`

- **id**: BIGINT, PK, auto increment
- **title**: TEXT, NOT NULL – tên phim
- **synopsis**: TEXT – mô tả phim
- **duration_min**: SMALLINT – thời lượng (phút)
- **release_date**: DATE – ngày phát hành
- **status**: ENUM (COMING_SOON | NOW_SHOWING | ENDED)
- **poster_url**: TEXT – link poster
- **trailer_url**: TEXT – link trailer
- **rating_avg**: NUMERIC(3,1), default 0 – điểm trung bình
- **rating_count**: INTEGER, default 0 – số lượt đánh giá
- **is_active**: BOOLEAN, default TRUE – có hiển thị hay không
- **created_at**: TIMESTAMPTZ, default now()
- **updated_at**: TIMESTAMPTZ, default now()

## 2. Bảng `genres`

- **id**: BIGINT, PK, auto increment
- **name**: TEXT – tên thể loại (VD: Action, Drama)
- **slug**: TEXT – mã định danh ngắn, dùng trong URL/API
- Ràng buộc: UNIQUE(name), UNIQUE(slug)

## 3. Bảng `formats`

- **id**: BIGINT, PK, auto increment
- **code**: TEXT – mã (VD: 2D, 3D, IMAX)
- **label**: TEXT – tên hiển thị (VD: 'IMAX 2D')
- Ràng buộc: UNIQUE(code)

## 4. Bảng `movie_genres`

- **movie_id**: FK → movies.id (ON DELETE CASCADE)
- **genre_id**: FK → genres.id (ON DELETE RESTRICT)
- PK (movie_id, genre_id)
- Mối quan hệ: Một phim có thể có nhiều thể loại; một thể loại có thể thuộc nhiều phim.

## 5. Bảng `movie_formats`

- **movie_id**: FK → movies.id (ON DELETE CASCADE)
- **format_id**: FK → formats.id (ON DELETE RESTRICT)
- PK (movie_id, format_id)
- Mối quan hệ: Một phim có thể có nhiều định dạng (2D, 3D); một định dạng áp dụng cho nhiều phim.

---

# Sơ đồ quan hệ (ERD đơn giản)

```
movies (1) ────< movie_genres >──── (N) genres
movies (1) ────< movie_formats >─── (N) formats
```

- **movies** là bảng trung tâm.
- **genres** & **formats** là bảng danh mục.
- **movie_genres** & **movie_formats** là bảng nối N-N.

#Query

CREATE EXTENSION IF NOT EXISTS citext;

-- Enums trạng thái phim (giữ đơn giản)
CREATE TYPE movie_status_enum AS ENUM ('COMING_SOON','NOW_SHOWING','ENDED');

-- ===== CORE TABLE =====
CREATE TABLE movies (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
title TEXT NOT NULL, -- tên hiển thị
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

-- Trigger cập nhật updated_at
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$
BEGIN NEW.updated_at = now(); RETURN NEW; END;

$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_movies_updated_at
BEFORE UPDATE ON movies
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ===== LOOKUP TABLES =====
CREATE TABLE genres (
  id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name    TEXT NOT NULL,
  slug    CITEXT NOT NULL,
  CONSTRAINT uq_genres_name UNIQUE (name),
  CONSTRAINT uq_genres_slug UNIQUE (slug)
);

CREATE TABLE formats (
  id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code    TEXT NOT NULL,
  label   TEXT NOT NULL,
  CONSTRAINT uq_formats_code UNIQUE (code)
);

-- ===== JOIN TABLES (N-N) =====
CREATE TABLE movie_genres (
  movie_id  BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
  genre_id  BIGINT NOT NULL REFERENCES genres(id) ON DELETE RESTRICT,
  PRIMARY KEY (movie_id, genre_id)
);

CREATE TABLE movie_formats (
  movie_id  BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
  format_id BIGINT NOT NULL REFERENCES formats(id) ON DELETE RESTRICT,
  PRIMARY KEY (movie_id, format_id)
);

-- ===== INDEXES THƯỜNG DÙNG =====
CREATE INDEX idx_movies_status ON movies(status);
CREATE INDEX idx_movies_active ON movies(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_movie_genres_genre ON movie_genres(genre_id);
CREATE INDEX idx_movie_formats_format ON movie_formats(format_id);


$$
