# Database Overview

## Extensions & Utilities

### CITEXT Extension

```sql
CREATE EXTENSION IF NOT EXISTS citext;
```

- Cho phép so sánh text không phân biệt hoa thường
- Sử dụng cho: email, phone_number, slug

### set_updated_at() Function

```sql
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

- Tự động cập nhật trường `updated_at` khi record được update
- Được sử dụng bởi nhiều trigger trong hệ thống

---

## ENUM Types

### Movie Status

```sql
CREATE TYPE movie_status_enum AS ENUM (
    'COMING_SOON',
    'NOW_SHOWING',
    'ENDED'
);
```

### User Roles

```sql
CREATE TYPE user_role_enum AS ENUM (
    'ROLE_USER',
    'ROLE_ADMIN',
    'ROLE_STAFF'
);
```

### Social Providers

```sql
CREATE TYPE social_provider_enum AS ENUM (
    'GOOGLE',
    'FACEBOOK',
    'X',
    'INSTAGRAM'
);
```

### OTP Related

```sql
CREATE TYPE otp_channel_enum AS ENUM ('EMAIL', 'SMS');

CREATE TYPE otp_purpose_enum AS ENUM (
    'PASSWORD_RESET',
    'TWO_FACTOR_AUTH',
    'ACCOUNT_VERIFICATION'
);
```

### Seat & Booking

```sql
CREATE TYPE seat_type_enum AS ENUM (
    'STANDARD',
    'VIP',
    'COUPLE',
    'DELUXE'
);

CREATE TYPE showtime_status_enum AS ENUM (
    'SCHEDULED',
    'SELLING',
    'FULL',
    'COMPLETED',
    'CANCELLED'
);

CREATE TYPE booking_status_enum AS ENUM (
    'PENDING',      -- Đang chờ thanh toán
    'CONFIRMED',    -- Đã thanh toán thành công
    'CANCELLED',    -- Đã hủy
    'EXPIRED'       -- Hết hạn
);
```

---

## Database Modules

### 1. User Management

- users
- roles
- user_roles
- social_accounts
- user_otps

### 2. Movie Catalog

- movies
- genres
- formats
- movie_genres
- movie_formats

### 3. Cinema & Screening

- cinemas
- screens
- seats

### 4. Showtime & Pricing

- showtimes
- ticket_prices

### 5. Booking & Payment

- bookings
- booking_items
- seat_locks

### 6. Membership System

- membership_tiers
- user_memberships

### 7. Content Management

- news
- carousel_items

---

## Best Practices

### Security

- Passwords luôn được hash (BCrypt)
- OTP codes được hash trước khi lưu
- Sử dụng CITEXT cho email/phone để tránh duplicate case-sensitive

### Performance

- Indexes được tạo cho các query thường xuyên
- Partial indexes (WHERE clause) cho filtered queries
- Composite indexes cho multi-column queries

### Data Integrity

- Foreign keys với CASCADE/RESTRICT phù hợp
- CHECK constraints cho business rules
- UNIQUE constraints cho dữ liệu không trùng lặp

### Soft Delete

- Sử dụng `is_active` flag thay vì xóa thật
- Giữ lại dữ liệu cho audit và history

### Timestamps

- Tất cả tables quan trọng có `created_at`
- Tables có thể update có `updated_at` với trigger
- Sử dụng TIMESTAMPTZ (timezone-aware)
