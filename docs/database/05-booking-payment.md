# Booking & Payment Module

## Overview

Module này quản lý toàn bộ quy trình đặt vé:

1. User chọn suất chiếu và ghế
2. Ghế được lock tạm thời (seat_locks)
3. Tạo booking với status PENDING
4. User thanh toán
5. Booking chuyển sang CONFIRMED
6. Tạo booking_items cho từng ghế

---

## 1. showtimes Table

| Column          | Type                 | Description                                |
| --------------- | -------------------- | ------------------------------------------ |
| id              | BIGINT               | ID suất chiếu                              |
| movie_id        | BIGINT               | FK → movies                                |
| screen_id       | BIGINT               | FK → screens                               |
| start_time      | TIMESTAMPTZ          | Thời gian bắt đầu                          |
| end_time        | TIMESTAMPTZ          | Thời gian kết thúc                         |
| status          | showtime_status_enum | SCHEDULED/SELLING/FULL/COMPLETED/CANCELLED |
| available_seats | SMALLINT             | Số ghế còn trống                           |
| is_active       | BOOLEAN              | Trạng thái                                 |

**Indexes:**

- (movie_id, start_time)
- (screen_id, start_time)
- status WHERE is_active = TRUE

---

## 2. ticket_prices Table

| Column      | Type           | Description                |
| ----------- | -------------- | -------------------------- |
| id          | BIGINT         | ID                         |
| showtime_id | BIGINT         | FK → showtimes             |
| seat_type   | seat_type_enum | STANDARD/VIP/COUPLE/DELUXE |
| price       | DECIMAL(10,2)  | Giá vé                     |

**Constraints:** UNIQUE (showtime_id, seat_type)

---

## 3. bookings Table

| Column                 | Type                | Description                         |
| ---------------------- | ------------------- | ----------------------------------- |
| id                     | BIGINT              | ID đơn                              |
| booking_code           | TEXT                | Mã đặt vé (unique)                  |
| user_id                | BIGINT              | FK → users                          |
| showtime_id            | BIGINT              | FK → showtimes                      |
| status                 | booking_status_enum | PENDING/CONFIRMED/CANCELLED/EXPIRED |
| total_amount           | DECIMAL(10,2)       | Tổng tiền                           |
| payment_method         | TEXT                | VNPAY/MOMO/ZALOPAY/etc              |
| payment_transaction_id | TEXT                | ID giao dịch                        |
| paid_at                | TIMESTAMPTZ         | Thời điểm thanh toán                |
| expires_at             | TIMESTAMPTZ         | Hết hạn (10-15 phút)                |

**Indexes:**

- user_id
- status
- created_at DESC

---

## 4. booking_items Table

| Column     | Type          | Description   |
| ---------- | ------------- | ------------- |
| id         | BIGINT        | ID            |
| booking_id | BIGINT        | FK → bookings |
| seat_id    | BIGINT        | FK → seats    |
| price      | DECIMAL(10,2) | Giá vé        |

**Constraints:** UNIQUE (booking_id, seat_id)

---

## 5. seat_locks Table

| Column       | Type        | Description              |
| ------------ | ----------- | ------------------------ |
| id           | BIGINT      | ID                       |
| showtime_id  | BIGINT      | FK → showtimes           |
| seat_id      | BIGINT      | FK → seats               |
| user_id      | BIGINT      | FK → users               |
| locked_until | TIMESTAMPTZ | Khóa đến khi (5-10 phút) |

**Constraints:** UNIQUE (showtime_id, seat_id)

**Purpose:** Tránh xung đột khi nhiều người đặt cùng lúc

---

## Booking Flow

```
1. User chọn showtime và seats
2. Lock seats (INSERT INTO seat_locks)
3. Create booking (status = PENDING)
4. User thanh toán trong thời gian lock
5. Payment success:
   - Update booking (status = CONFIRMED, paid_at = NOW())
   - Create booking_items
   - Delete seat_locks
   - Update showtimes.available_seats
6. Payment fail hoặc timeout:
   - Update booking (status = EXPIRED)
   - Delete seat_locks
```
