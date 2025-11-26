# Review & Comment System Documentation

## Tổng Quan

Module Review & Comment cho phép người dùng đánh giá và bình luận về phim sau khi đã xem. Hệ thống đảm bảo chỉ những người dùng đã có booking CONFIRMED và showtime đã kết thúc mới được phép review.

---

## Database Schema

### 1. Enum Types

```sql
CREATE TYPE review_status_enum AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'HIDDEN');
```

**Mô tả các trạng thái:**

- `PENDING`: Review đang chờ kiểm duyệt (nếu bật moderation)
- `APPROVED`: Review đã được duyệt và hiển thị công khai
- `REJECTED`: Review bị từ chối (vi phạm quy định)
- `HIDDEN`: Review bị ẩn (do admin hoặc user báo cáo)

---

### 2. Table: movie_reviews

Bảng chính lưu trữ đánh giá và bình luận của người dùng về phim.

```sql
CREATE TABLE movie_reviews (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies (id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    booking_id BIGINT NOT NULL REFERENCES bookings (id) ON DELETE RESTRICT,
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 10),
    comment_text TEXT,
    status review_status_enum NOT NULL DEFAULT 'APPROVED',
    is_spoiler BOOLEAN NOT NULL DEFAULT FALSE,
    helpful_count INTEGER NOT NULL DEFAULT 0,
    is_edited BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_movie_reviews_user_movie UNIQUE (user_id, movie_id)
);
```

**Các trường quan trọng:**

| Field         | Type        | Mô tả                                 |
| ------------- | ----------- | ------------------------------------- |
| id            | BIGINT      | Primary key, auto increment           |
| movie_id      | BIGINT      | ID phim được review (FK → movies)     |
| user_id       | BIGINT      | ID người dùng review (FK → users)     |
| booking_id    | BIGINT      | ID booking để verify user đã xem phim |
| rating        | SMALLINT    | Điểm đánh giá từ 1-10                 |
| comment_text  | TEXT        | Nội dung bình luận (optional)         |
| status        | ENUM        | Trạng thái kiểm duyệt                 |
| is_spoiler    | BOOLEAN     | Đánh dấu review có spoiler hay không  |
| helpful_count | INTEGER     | Số lượt vote "hữu ích"                |
| is_edited     | BOOLEAN     | Đánh dấu review đã được chỉnh sửa     |
| deleted_at    | TIMESTAMPTZ | Timestamp khi user xóa (soft delete)  |
| created_at    | TIMESTAMPTZ | Thời điểm tạo review                  |
| updated_at    | TIMESTAMPTZ | Thời điểm cập nhật gần nhất           |

**Constraints:**

- `uq_movie_reviews_user_movie`: Một user chỉ được review một phim một lần
- `CHECK (rating BETWEEN 1 AND 10)`: Rating phải từ 1-10

---

### 3. Table: review_images

Bảng lưu trữ hình ảnh đính kèm trong review.

```sql
CREATE TABLE review_images (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    review_id BIGINT NOT NULL REFERENCES movie_reviews (id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    display_order SMALLINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_review_images_review_order UNIQUE (review_id, display_order)
);
```

**Các trường:**

| Field         | Type        | Mô tả                          |
| ------------- | ----------- | ------------------------------ |
| id            | BIGINT      | Primary key                    |
| review_id     | BIGINT      | ID review (FK → movie_reviews) |
| image_url     | TEXT        | URL hình ảnh đã upload         |
| display_order | SMALLINT    | Thứ tự hiển thị (0, 1, 2...)   |
| created_at    | TIMESTAMPTZ | Thời điểm upload               |

**Constraints:**

- `uq_review_images_review_order`: Đảm bảo không trùng thứ tự trong cùng review
- `ON DELETE CASCADE`: Xóa review → xóa tất cả ảnh

---

### 4. Table: review_helpful_votes

Bảng tracking user nào đã vote review là "hữu ích".

```sql
CREATE TABLE review_helpful_votes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    review_id BIGINT NOT NULL REFERENCES movie_reviews (id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_review_helpful_votes_review_user UNIQUE (review_id, user_id)
);
```

**Các trường:**

| Field      | Type        | Mô tả                                    |
| ---------- | ----------- | ---------------------------------------- |
| id         | BIGINT      | Primary key                              |
| review_id  | BIGINT      | ID review được vote (FK → movie_reviews) |
| user_id    | BIGINT      | ID user vote (FK → users)                |
| created_at | TIMESTAMPTZ | Thời điểm vote                           |

**Constraints:**

- `uq_review_helpful_votes_review_user`: Một user chỉ vote một review một lần

---

## Nghiệp Vụ (Business Logic)

### 1. Tạo Review

**Điều kiện:**

- User phải đã đăng nhập
- User phải có booking với status = 'CONFIRMED'
- Showtime của booking đó phải đã kết thúc (end_time < NOW())
- User chưa review phim này trước đó

**Flow:**

1. Client gửi request với: movie_id, booking_id, rating, comment_text, images[], is_spoiler
2. Backend validate booking thuộc về user và đã hoàn thành
3. Insert vào `movie_reviews`
4. Nếu có images → insert vào `review_images`
5. Update `rating_avg` và `rating_count` trong bảng `movies`

### 2. Sửa Review

**Điều kiện:**

- User phải là chủ sở hữu review
- Review chưa bị xóa (deleted_at IS NULL)

**Flow:**

1. Client gửi request với review_id và dữ liệu mới
2. Backend verify ownership
3. Update rating, comment_text, is_spoiler
4. Set is_edited = TRUE
5. updated_at tự động update bởi trigger
6. Recalculate rating_avg của movie nếu rating thay đổi

### 3. Xóa Review (Soft Delete)

**Điều kiện:**

- User phải là chủ sở hữu review

**Flow:**

1. Client gửi DELETE request với review_id
2. Backend verify ownership
3. Set deleted_at = NOW()
4. Review không hiển thị nhưng vẫn còn trong DB
5. Recalculate rating_avg của movie

### 4. Vote "Hữu ích"

**Flow:**

1. User click "Hữu ích" trên một review
2. Insert vào `review_helpful_votes`
3. Increment `helpful_count` trong `movie_reviews`
4. Nếu user click lại → remove vote và decrement count

---

## Indexes

```sql
CREATE INDEX idx_movie_reviews_movie_id ON movie_reviews (movie_id);
CREATE INDEX idx_movie_reviews_user_id ON movie_reviews (user_id);
CREATE INDEX idx_movie_reviews_status ON movie_reviews (status) WHERE status = 'APPROVED' AND deleted_at IS NULL;
CREATE INDEX idx_movie_reviews_created_at ON movie_reviews (created_at DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_review_images_review_id ON review_images (review_id);
CREATE INDEX idx_review_helpful_votes_review_id ON review_helpful_votes (review_id);
CREATE INDEX idx_review_helpful_votes_user_id ON review_helpful_votes (user_id);
```

**Mục đích:**

- Tối ưu query lấy reviews theo movie
- Tối ưu query lấy reviews của user
- Partial index cho reviews đang active
- Sắp xếp theo thời gian mới nhất

---

## API Endpoints (Gợi ý)

### GET /api/movies/{movieId}/reviews

Lấy danh sách reviews của phim (chỉ APPROVED và chưa xóa)

### POST /api/movies/{movieId}/reviews

Tạo review mới (yêu cầu authentication)

### PUT /api/reviews/{reviewId}

Sửa review của mình

### DELETE /api/reviews/{reviewId}

Xóa review của mình (soft delete)

### POST /api/reviews/{reviewId}/helpful

Vote review là hữu ích

---

## UI Display Requirements

### Hiển thị Review:

- Avatar user (từ users.avatar_url)
- Tên user (từ users.display_name)
- Rating (stars hoặc số điểm)
- Comment text
- Hình ảnh đính kèm (nếu có)
- Badge "Spoiler" nếu is_spoiler = TRUE
- Badge "Đã chỉnh sửa" nếu is_edited = TRUE
- Số lượt "Hữu ích" (helpful_count)
- Thời gian đăng (created_at)

### Sắp xếp:

- Mặc định: Mới nhất trước (created_at DESC)
- Tùy chọn: Hữu ích nhất (helpful_count DESC)

---

## Notes

- Review có thể không có comment_text (chỉ rating)
- Hệ thống hỗ trợ moderation bằng field `status`
- Soft delete giúp giữ lại dữ liệu cho audit
- Rating của movie được tính lại mỗi khi có review mới/sửa/xóa
