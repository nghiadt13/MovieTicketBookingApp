# 🎬 Hướng Dẫn: Chức Năng Hiển Thị & Chọn Ghế Ngồi

## 📋 Tổng Quan

Chức năng chọn ghế dựa trên **4 bảng chính**:
- `seats`: Dữ liệu tĩnh về ghế (setup 1 lần)
- `showtimes`: Các suất chiếu
- `booking_items`: Ghế đã được đặt
- `seat_locks`: Ghế đang được giữ tạm thời

**Nguyên tắc:** Trạng thái ghế được **TÍNH TOÁN ĐỘNG**, không lưu trữ.

---

## 🏗️ Bước 1: Setup Dữ Liệu Tĩnh (Chỉ làm 1 lần)

### 1.1. Tạo Rạp Chiếu

```sql
INSERT INTO cinemas (name, address, city, district) VALUES
('CGV Vincom Center', '191 Bà Triệu', 'Hà Nội', 'Hai Bà Trưng'),
('CGV Aeon Hà Đông', 'Aeon Mall', 'Hà Nội', 'Hà Đông'),
('Lotte Cinema', '54 Liễu Giai', 'Hà Nội', 'Ba Đình');
```

### 1.2. Tạo Phòng Chiếu

```sql
-- Cinema 1 có 3 phòng
INSERT INTO screens (cinema_id, name, total_seats, screen_type) VALUES
(1, 'Screen 1', 100, '2D'),
(1, 'Screen 2', 80, 'IMAX'),
(1, 'Screen 3', 60, '4DX');
```

### 1.3. Tạo Ghế (Quan trọng nhất!)

**Ví dụ: Screen 1 có layout 10 hàng x 10 ghế = 100 ghế**

```sql
-- Cách 1: Insert từng ghế (Phù hợp cho layout phức tạp)
INSERT INTO seats (screen_id, row_name, seat_number, seat_type) VALUES
-- Hàng A: 10 ghế thường
(1, 'A', 1, 'STANDARD'),
(1, 'A', 2, 'STANDARD'),
(1, 'A', 3, 'STANDARD'),
(1, 'A', 4, 'STANDARD'),
(1, 'A', 5, 'STANDARD'),
(1, 'A', 6, 'STANDARD'),
(1, 'A', 7, 'STANDARD'),
(1, 'A', 8, 'STANDARD'),
(1, 'A', 9, 'STANDARD'),
(1, 'A', 10, 'STANDARD'),

-- Hàng B: 10 ghế thường
(1, 'B', 1, 'STANDARD'),
(1, 'B', 2, 'STANDARD'),
-- ... tương tự B3 đến B10

-- Hàng C: 8 ghế thường + 2 ghế VIP
(1, 'C', 1, 'STANDARD'),
(1, 'C', 2, 'STANDARD'),
(1, 'C', 3, 'STANDARD'),
(1, 'C', 4, 'STANDARD'),
(1, 'C', 5, 'VIP'),
(1, 'C', 6, 'VIP'),
(1, 'C', 7, 'STANDARD'),
(1, 'C', 8, 'STANDARD'),
(1, 'C', 9, 'STANDARD'),
(1, 'C', 10, 'STANDARD');

-- Hàng D-J: Tương tự...
```

**Cách 2: Script tự động (Khuyến nghị)**

```sql
-- Script PostgreSQL để tạo ghế tự động
DO $$
DECLARE
    screen_id_var BIGINT := 1; -- Screen ID
    rows TEXT[] := ARRAY['A','B','C','D','E','F','G','H','I','J'];
    row_name TEXT;
    seat_num INT;
BEGIN
    FOREACH row_name IN ARRAY rows
    LOOP
        FOR seat_num IN 1..10
        LOOP
            INSERT INTO seats (screen_id, row_name, seat_number, seat_type)
            VALUES (
                screen_id_var,
                row_name,
                seat_num,
                CASE 
                    -- Hàng E, F giữa là VIP (ghế 4-7)
                    WHEN row_name IN ('E','F') AND seat_num BETWEEN 4 AND 7 THEN 'VIP'
                    -- Hàng I, J là ghế đôi
                    WHEN row_name IN ('I','J') AND seat_num % 2 = 0 THEN 'COUPLE'
                    ELSE 'STANDARD'
                END
            );
        END LOOP;
    END LOOP;
END $$;
```

---

## 🎬 Bước 2: Tạo Suất Chiếu & Giá Vé

### 2.1. Tạo Suất Chiếu

```sql
INSERT INTO showtimes (
    movie_id, 
    screen_id, 
    start_time, 
    end_time, 
    available_seats,
    status
) VALUES
-- Phim "Avengers" tại Screen 1
(1, 1, '2025-11-26 10:00:00', '2025-11-26 12:30:00', 100, 'SELLING'),
(1, 1, '2025-11-26 14:00:00', '2025-11-26 16:30:00', 100, 'SELLING'),
(1, 1, '2025-11-26 19:00:00', '2025-11-26 21:30:00', 100, 'SELLING'),

-- Ngày hôm sau
(1, 1, '2025-11-27 10:00:00', '2025-11-27 12:30:00', 100, 'SCHEDULED');
```

### 2.2. Thiết Lập Giá Vé

```sql
-- Giá vé cho suất chiếu 10:00 AM (showtime_id = 1)
INSERT INTO ticket_prices (showtime_id, seat_type, price) VALUES
(1, 'STANDARD', 70000),
(1, 'VIP', 120000),
(1, 'COUPLE', 200000);

-- Giá vé cho suất 2:00 PM (có thể khác giá)
INSERT INTO ticket_prices (showtime_id, seat_type, price) VALUES
(2, 'STANDARD', 70000),
(2, 'VIP', 120000),
(2, 'COUPLE', 200000);

-- Giá vé cho suất tối (đắt hơn)
INSERT INTO ticket_prices (showtime_id, seat_type, price) VALUES
(3, 'STANDARD', 90000),
(3, 'VIP', 150000),
(3, 'COUPLE', 250000);
```

---

## 🔄 Bước 3: Mối Quan Hệ Giữa Các Bảng

### Sơ Đồ Quan Hệ

```
┌──────────┐
│ cinemas  │
└────┬─────┘
     │ 1:N
┌────▼─────┐
│ screens  │
└────┬─────┘
     │ 1:N
┌────▼─────┐      ┌────────────┐
│  seats   │◄────┤ showtimes  │
└────┬─────┘  N:1 └──────┬─────┘
     │                   │
     │ 1:N               │ 1:N
     │              ┌────▼──────────┐
     │              │ ticket_prices │
     │              └───────────────┘
     │
     ├─────────────┐
     │ 1:N         │ 1:N
┌────▼─────────┐  ┌▼──────────┐
│ booking_items│  │seat_locks │
└──────────────┘  └───────────┘
```

### Giải Thích

1. **1 Cinema** → **Nhiều Screens**
2. **1 Screen** → **Nhiều Seats** (tĩnh, không đổi)
3. **1 Screen** → **Nhiều Showtimes** (động, thêm mới)
4. **1 Showtime** → **Nhiều Ticket Prices** (theo seat_type)
5. **1 Seat** → **Nhiều Booking Items** (qua các showtime khác nhau)
6. **1 Seat** → **Nhiều Seat Locks** (qua các showtime khác nhau)

---

## 📊 Bước 4: Query Hiển Thị Ghế

### Query Lấy Trạng Thái Ghế Cho 1 Suất Chiếu

```sql
SELECT 
    s.id AS seat_id,
    s.row_name,
    s.seat_number,
    s.row_name || s.seat_number AS seat_label,  -- "A1", "B5"...
    s.seat_type,
    tp.price,
    
    -- Tính toán trạng thái động
    CASE 
        WHEN bi.id IS NOT NULL THEN 'BOOKED'
        WHEN sl.id IS NOT NULL AND sl.locked_until > NOW() THEN 'LOCKED'
        ELSE 'AVAILABLE'
    END AS status,
    
    -- Thông tin bổ sung
    sl.locked_until,
    sl.user_id AS locked_by_user_id
    
FROM seats s
INNER JOIN screens sc ON s.screen_id = sc.id
INNER JOIN showtimes st ON st.screen_id = sc.id

-- Join giá vé
LEFT JOIN ticket_prices tp 
    ON tp.showtime_id = st.id 
    AND tp.seat_type = s.seat_type

-- Join booking items (ghế đã đặt)
LEFT JOIN booking_items bi 
    ON bi.seat_id = s.id
    AND bi.booking_id IN (
        SELECT id FROM bookings 
        WHERE showtime_id = st.id 
        AND status = 'CONFIRMED'
    )

-- Join seat locks (ghế đang giữ)
LEFT JOIN seat_locks sl 
    ON sl.seat_id = s.id 
    AND sl.showtime_id = st.id
    AND sl.locked_until > NOW()
    AND sl.user_id != :current_user_id  -- User hiện tại vẫn thấy ghế mình lock

WHERE st.id = :showtime_id
    AND s.is_active = true
    
ORDER BY s.row_name, s.seat_number;
```

**Tham số đầu vào:**
- `:showtime_id` - ID của suất chiếu
- `:current_user_id` - ID của user đang xem (để hiển thị ghế mình đang giữ)

---

## 🎯 Bước 5: Luồng Chọn Ghế

### 5.1. User Chọn Ghế (Lock Tạm Thời)

```sql
-- Kiểm tra ghế có available không
SELECT id FROM seats 
WHERE id IN (:seat_ids)  -- VD: [100, 101, 102]
AND id NOT IN (
    -- Loại ghế đã booked
    SELECT seat_id FROM booking_items bi
    JOIN bookings b ON bi.booking_id = b.id
    WHERE b.showtime_id = :showtime_id 
    AND b.status = 'CONFIRMED'
)
AND id NOT IN (
    -- Loại ghế đang bị lock bởi user khác
    SELECT seat_id FROM seat_locks
    WHERE showtime_id = :showtime_id 
    AND locked_until > NOW()
    AND user_id != :current_user_id
);

-- Nếu OK, tạo lock (giữ ghế 10 phút)
INSERT INTO seat_locks (showtime_id, seat_id, user_id, locked_until)
VALUES 
    (:showtime_id, 100, :user_id, NOW() + INTERVAL '10 minutes'),
    (:showtime_id, 101, :user_id, NOW() + INTERVAL '10 minutes'),
    (:showtime_id, 102, :user_id, NOW() + INTERVAL '10 minutes')
ON CONFLICT (showtime_id, seat_id) 
DO UPDATE SET 
    user_id = EXCLUDED.user_id,
    locked_until = EXCLUDED.locked_until;
```

### 5.2. User Thanh Toán Thành Công

```sql
BEGIN TRANSACTION;

-- 1. Tạo booking
INSERT INTO bookings (
    booking_code, 
    user_id, 
    showtime_id, 
    status, 
    total_amount, 
    expires_at
) VALUES (
    'BK' || TO_CHAR(NOW(), 'YYYYMMDDHH24MISS') || LPAD(:user_id::TEXT, 4, '0'),
    :user_id,
    :showtime_id,
    'CONFIRMED',
    :total_amount,
    NOW() + INTERVAL '2 hours'
) RETURNING id INTO :booking_id;

-- 2. Lưu chi tiết ghế đã đặt
INSERT INTO booking_items (booking_id, seat_id, price)
SELECT :booking_id, seat_id, price
FROM UNNEST(
    ARRAY[:seat_ids],    -- [100, 101, 102]
    ARRAY[:prices]       -- [70000, 70000, 120000]
) AS t(seat_id, price);

-- 3. Xóa lock (không cần nữa)
DELETE FROM seat_locks 
WHERE showtime_id = :showtime_id 
AND seat_id = ANY(:seat_ids)
AND user_id = :user_id;

-- 4. Giảm số ghế còn trống
UPDATE showtimes 
SET available_seats = available_seats - :seat_count
WHERE id = :showtime_id;

COMMIT;
```

### 5.3. User Hủy/Hết Thời Gian

```sql
-- Xóa lock khi user hủy hoặc timeout
DELETE FROM seat_locks 
WHERE showtime_id = :showtime_id 
AND user_id = :user_id;

-- Hoặc dùng scheduled job để xóa lock hết hạn
DELETE FROM seat_locks 
WHERE locked_until < NOW();
```

---

## 📝 Ví Dụ Dữ Liệu Thực Tế

### Trạng thái ghế qua các suất chiếu

**Suất 10:00 AM (showtime_id = 1):**
```
User A đặt: A1, A2, A3
→ booking_items: [(1, A1), (1, A2), (1, A3)] cho booking_id = 1

Query ghế:
- A1: BOOKED (có trong booking_items cho showtime 1)
- A2: BOOKED
- A3: BOOKED  
- A4: AVAILABLE
- B1: LOCKED (User B đang giữ, chưa thanh toán)
```

**Suất 2:00 PM (showtime_id = 2):**
```
Chưa có ai đặt

Query ghế:
- A1: AVAILABLE (không có booking nào cho showtime 2)
- A2: AVAILABLE
- A3: AVAILABLE
- A4: AVAILABLE
- B1: AVAILABLE
```

**Suất 7:00 PM (showtime_id = 3):**
```
User C đặt: A1, B5

Query ghế:
- A1: BOOKED (có trong booking_items cho showtime 3)
- A2: AVAILABLE
- A3: AVAILABLE
- B5: BOOKED
```

---

## ✅ Checklist Setup

- [ ] Tạo cinemas
- [ ] Tạo screens cho mỗi cinema
- [ ] Tạo seats cho mỗi screen (chỉ 1 lần!)
- [ ] Tạo showtimes cho movies
- [ ] Tạo ticket_prices cho mỗi showtime
- [ ] Test query hiển thị ghế
- [ ] Implement API lock ghế
- [ ] Implement API booking ghế
- [ ] Setup scheduled job để cleanup seat_locks hết hạn

---

## 🚨 Lưu Ý Quan Trọng

1. **Bảng `seats` chỉ insert 1 lần** khi setup phòng chiếu
2. **KHÔNG có cột `status`** trong bảng seats
3. **Trạng thái được tính toán** dựa trên `showtime_id` + `booking_items` + `seat_locks`
4. **Ghế tự động "available"** cho suất chiếu mới vì không có booking
5. **Dùng `seat_locks`** để tránh race condition khi nhiều user chọn cùng ghế
6. **Lock timeout** nên là 10-15 phút để giải phóng ghế nếu user không thanh toán

---

## 🔧 Tips Optimization

- Index trên `(showtime_id, seat_id)` trong `seat_locks`
- Index trên `booking_id` trong `booking_items`
- Cleanup `seat_locks` hết hạn mỗi 5 phút
- Cache layout ghế của screen (không đổi)
- Chỉ query lại trạng thái khi cần (polling mỗi 10-30s)
