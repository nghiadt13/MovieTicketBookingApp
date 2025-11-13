# Cinema & Screening Module

## 1. cinemas Table

**Mục đích:** Thông tin rạp chiếu phim

### Schema

| Column       | Type          | Constraints             | Description        |
| ------------ | ------------- | ----------------------- | ------------------ |
| id           | BIGINT        | PRIMARY KEY, IDENTITY   | ID rạp             |
| name         | TEXT          | NOT NULL                | Tên rạp            |
| address      | TEXT          | NOT NULL                | Địa chỉ            |
| city         | TEXT          | NOT NULL                | Thành phố          |
| district     | TEXT          |                         | Quận/Huyện         |
| phone_number | TEXT          |                         | Số điện thoại      |
| email        | TEXT          |                         | Email              |
| latitude     | DECIMAL(10,8) |                         | Vĩ độ              |
| longitude    | DECIMAL(11,8) |                         | Kinh độ            |
| is_active    | BOOLEAN       | NOT NULL, DEFAULT TRUE  | Trạng thái         |
| created_at   | TIMESTAMPTZ   | NOT NULL, DEFAULT now() | Thời điểm tạo      |
| updated_at   | TIMESTAMPTZ   | NOT NULL, DEFAULT now() | Thời điểm cập nhật |

### Triggers

- `trg_cinemas_updated_at`: Tự động cập nhật `updated_at`

---

## 2. screens Table

**Mục đích:** Phòng chiếu trong rạp

### Schema

| Column      | Type        | Constraints               | Description        |
| ----------- | ----------- | ------------------------- | ------------------ |
| id          | BIGINT      | PRIMARY KEY, IDENTITY     | ID phòng           |
| cinema_id   | BIGINT      | FK → cinemas(id), CASCADE | ID rạp             |
| name        | TEXT        | NOT NULL                  | Tên phòng          |
| total_seats | SMALLINT    | NOT NULL, CHECK > 0       | Tổng số ghế        |
| screen_type | TEXT        |                           | Loại phòng         |
| is_active   | BOOLEAN     | NOT NULL, DEFAULT TRUE    | Trạng thái         |
| created_at  | TIMESTAMPTZ | NOT NULL, DEFAULT now()   | Thời điểm tạo      |
| updated_at  | TIMESTAMPTZ | NOT NULL, DEFAULT now()   | Thời điểm cập nhật |

### Screen Types

- STANDARD: Phòng thường
- 3D: Phòng 3D
- IMAX: Phòng IMAX
- 4DX: Phòng 4DX

### Constraints

- UNIQUE: (cinema_id, name)

### Triggers

- `trg_screens_updated_at`: Tự động cập nhật `updated_at`

---

## 3. seats Table

**Mục đích:** Ghế ngồi trong phòng chiếu

### Schema

| Column      | Type           | Constraints                  | Description           |
| ----------- | -------------- | ---------------------------- | --------------------- |
| id          | BIGINT         | PRIMARY KEY, IDENTITY        | ID ghế                |
| screen_id   | BIGINT         | FK → screens(id), CASCADE    | ID phòng              |
| row_name    | TEXT           | NOT NULL                     | Hàng ghế (A, B, C...) |
| seat_number | SMALLINT       | NOT NULL                     | Số ghế (1, 2, 3...)   |
| seat_type   | seat_type_enum | NOT NULL, DEFAULT 'STANDARD' | Loại ghế              |
| is_active   | BOOLEAN        | NOT NULL, DEFAULT TRUE       | Trạng thái            |
| created_at  | TIMESTAMPTZ    | NOT NULL, DEFAULT now()      | Thời điểm tạo         |

### Seat Types

- STANDARD: Ghế thường
- VIP: Ghế VIP
- COUPLE: Ghế đôi
- DELUXE: Ghế cao cấp

### Constraints

- UNIQUE: (screen_id, row_name, seat_number)

---

## Relationships

```
cinemas (1) ──── (N) screens (1) ──── (N) seats
```
