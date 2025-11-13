# Content Management Module

## Overview

Module quản lý nội dung hiển thị trên app:

- News: Tin tức, khuyến mãi
- Carousel: Banner slides trên homepage

---

## 1. news Table

**Mục đích:** Tin tức và thông báo

| Column       | Type      | Description             |
| ------------ | --------- | ----------------------- |
| id           | SERIAL    | ID tin tức              |
| title        | TEXT      | Tiêu đề                 |
| content      | TEXT      | Nội dung chi tiết       |
| image_url    | TEXT      | URL hình ảnh (nullable) |
| published_at | TIMESTAMP | Thời điểm xuất bản      |

### API Usage

- GET /api/news - Trả về 10 tin mới nhất
- Sắp xếp theo published_at DESC

### Use Cases

- Tin tức về phim mới
- Thông báo khuyến mãi
- Sự kiện đặc biệt

---

## 2. carousel_items Table

**Mục đích:** Banner carousel trên homepage

| Column     | Type         | Description                   |
| ---------- | ------------ | ----------------------------- |
| id         | BIGSERIAL    | ID                            |
| title      | VARCHAR(255) | Tiêu đề                       |
| image_url  | VARCHAR(255) | URL hình ảnh banner           |
| content    | TEXT         | Nội dung (nullable)           |
| target_url | VARCHAR(255) | URL đích khi click (nullable) |
| is_active  | BOOLEAN      | Trạng thái hiển thị           |
| created_at | TIMESTAMPTZ  | Thời điểm tạo                 |
| updated_at | TIMESTAMPTZ  | Thời điểm cập nhật            |

### API Usage

- GET /api/carousel - Trả về tất cả items active
- Sắp xếp theo created_at DESC

### Use Cases

- Banner quảng cáo phim hot
- Khuyến mãi đặc biệt
- Link đến trang chi tiết phim/sự kiện

### Example Data

```sql
INSERT INTO carousel_items (title, image_url, target_url, is_active) VALUES
('Avatar 3 - Coming Soon', 'https://cdn.example.com/avatar3.jpg', '/movies/123', true),
('50% Off This Weekend', 'https://cdn.example.com/promo.jpg', '/promotions/weekend', true);
```

---

## Content Management Flow

### Admin Panel

1. Admin tạo/edit news và carousel items
2. Set is_active = true để publish
3. Mobile app fetch và hiển thị

### Caching Strategy

- Cache news và carousel ở client (5-10 phút)
- Refresh khi user pull-to-refresh
- Push notification khi có tin mới quan trọng

---

## Relationships

```
news (standalone)
carousel_items (standalone)
```

Cả 2 tables đều độc lập, không có foreign key đến tables khác.
