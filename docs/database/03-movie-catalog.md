# Movie Catalog Module

## 1. movies Table

**Mục đích:** Lưu trữ thông tin phim

### Schema

| Column       | Type              | Constraints                     | Description        |
| ------------ | ----------------- | ------------------------------- | ------------------ |
| id           | BIGINT            | PRIMARY KEY, IDENTITY           | ID phim            |
| title        | TEXT              | NOT NULL                        | Tên phim           |
| synopsis     | TEXT              |                                 | Tóm tắt nội dung   |
| duration_min | SMALLINT          | CHECK > 0                       | Thời lượng (phút)  |
| release_date | DATE              |                                 | Ngày phát hành     |
| status       | movie_status_enum | NOT NULL, DEFAULT 'COMING_SOON' | Trạng thái         |
| poster_url   | TEXT              |                                 | URL poster         |
| trailer_url  | TEXT              |                                 | URL trailer        |
| rating_avg   | NUMERIC(3,1)      | NOT NULL, DEFAULT 0, CHECK 0-10 | Điểm trung bình    |
| rating_count | INTEGER           | NOT NULL, DEFAULT 0, CHECK >= 0 | Số lượt đánh giá   |
| is_active    | BOOLEAN           | NOT NULL, DEFAULT TRUE          | Trạng thái active  |
| created_at   | TIMESTAMPTZ       | NOT NULL, DEFAULT now()         | Thời điểm tạo      |
| updated_at   | TIMESTAMPTZ       | NOT NULL, DEFAULT now()         | Thời điểm cập nhật |

### Movie Status

- **COMING_SOON**: Sắp chiếu
- **NOW_SHOWING**: Đang chiếu
- **ENDED**: Đã kết thúc

### Triggers

- `trg_movies_updated_at`: Tự động cập nhật `updated_at`

### Indexes

- `idx_movies_status` on status
- `idx_movies_active` on is_active WHERE is_active = TRUE

---

## 2. genres Table

**Mục đích:** Danh mục thể loại phim

### Schema

| Column | Type   | Constraints           | Description         |
| ------ | ------ | --------------------- | ------------------- |
| id     | BIGINT | PRIMARY KEY, IDENTITY | ID thể loại         |
| name   | TEXT   | NOT NULL, UNIQUE      | Tên thể loại        |
| slug   | CITEXT | NOT NULL, UNIQUE      | Slug (URL-friendly) |

### Example Data

```sql
INSERT INTO genres (name, slug) VALUES
    ('Action', 'action'),
    ('Comedy', 'comedy'),
    ('Drama', 'drama'),
    ('Horror', 'horror'),
    ('Sci-Fi', 'sci-fi');
```

---

## 3. formats Table

**Mục đích:** Danh mục định dạng chiếu

### Schema

| Column | Type   | Constraints           | Description   |
| ------ | ------ | --------------------- | ------------- |
| id     | BIGINT | PRIMARY KEY, IDENTITY | ID định dạng  |
| code   | TEXT   | NOT NULL, UNIQUE      | Mã định dạng  |
| label  | TEXT   | NOT NULL              | Nhãn hiển thị |

### Example Data

```sql
INSERT INTO formats (code, label) VALUES
    ('2D', '2D'),
    ('3D', '3D'),
    ('IMAX', 'IMAX'),
    ('4DX', '4DX'),
    ('DOLBY_ATMOS', 'Dolby Atmos');
```

---

## 4. movie_genres Table

**Mục đích:** Junction table liên kết movies và genres (Many-to-Many)

### Schema

| Column   | Type   | Constraints               | Description |
| -------- | ------ | ------------------------- | ----------- |
| movie_id | BIGINT | FK → movies(id), CASCADE  | ID phim     |
| genre_id | BIGINT | FK → genres(id), RESTRICT | ID thể loại |

**Primary Key:** (movie_id, genre_id)

### Indexes

- `idx_movie_genres_genre` on genre_id

### Use Cases

- Một phim có nhiều thể loại
- Tìm tất cả phim theo thể loại
- Filter phim theo multiple genres

---

## 5. movie_formats Table

**Mục đích:** Junction table liên kết movies và formats (Many-to-Many)

### Schema

| Column    | Type   | Constraints                | Description  |
| --------- | ------ | -------------------------- | ------------ |
| movie_id  | BIGINT | FK → movies(id), CASCADE   | ID phim      |
| format_id | BIGINT | FK → formats(id), RESTRICT | ID định dạng |

**Primary Key:** (movie_id, format_id)

### Indexes

- `idx_movie_formats_format` on format_id

### Use Cases

- Một phim có nhiều định dạng chiếu
- Tìm phim theo định dạng (VD: tất cả phim IMAX)
- Hiển thị available formats cho mỗi phim

---

## Relationships

```
movies (1) ──── (N) movie_genres (N) ──── (1) genres
movies (1) ──── (N) movie_formats (N) ──── (1) formats
movies (1) ──── (N) showtimes
```

---

## Common Queries

### Get movie with genres and formats

```sql
SELECT
    m.*,
    array_agg(DISTINCT g.name) as genres,
    array_agg(DISTINCT f.label) as formats
FROM movies m
LEFT JOIN movie_genres mg ON m.id = mg.movie_id
LEFT JOIN genres g ON mg.genre_id = g.id
LEFT JOIN movie_formats mf ON m.id = mf.movie_id
LEFT JOIN formats f ON mf.format_id = f.id
WHERE m.id = ?
GROUP BY m.id;
```

### Get movies by status

```sql
SELECT * FROM movies
WHERE status = 'NOW_SHOWING'
  AND is_active = TRUE
ORDER BY release_date DESC;
```

### Search movies by genre

```sql
SELECT DISTINCT m.*
FROM movies m
JOIN movie_genres mg ON m.id = mg.movie_id
JOIN genres g ON mg.genre_id = g.id
WHERE g.slug = 'action'
  AND m.is_active = TRUE;
```

### Get top rated movies

```sql
SELECT * FROM movies
WHERE rating_count >= 10
  AND is_active = TRUE
ORDER BY rating_avg DESC, rating_count DESC
LIMIT 10;
```

### Update movie rating

```sql
-- Sau khi user rate phim
UPDATE movies
SET rating_avg = (
    SELECT AVG(rating) FROM user_ratings WHERE movie_id = ?
),
rating_count = (
    SELECT COUNT(*) FROM user_ratings WHERE movie_id = ?
)
WHERE id = ?;
```
