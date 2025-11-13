# Membership System Module

## Overview

Hệ thống thành viên với các hạng dựa trên tổng chi tiêu, cung cấp:

- Giảm giá theo % (discount_percent)
- Tích điểm với hệ số nhân (points_multiplier)
- Tự động nâng hạng khi đạt spending_required

---

## 1. membership_tiers Table

**Mục đích:** Định nghĩa các hạng thành viên

| Column            | Type          | Description                               |
| ----------------- | ------------- | ----------------------------------------- |
| id                | SERIAL        | ID hạng                                   |
| name              | VARCHAR(50)   | Tên hạng (Bronze, Silver, Gold, Platinum) |
| rank_order        | INTEGER       | Thứ tự hạng (1, 2, 3, 4)                  |
| spending_required | DECIMAL(10,2) | Chi tiêu yêu cầu để đạt hạng              |
| discount_percent  | DECIMAL(5,2)  | % giảm giá (0-100)                        |
| points_multiplier | DECIMAL(3,2)  | Hệ số nhân điểm (1.0, 1.5, 2.0)           |
| image_url         | VARCHAR(500)  | URL hình ảnh hạng                         |
| description       | TEXT          | Mô tả quyền lợi                           |

**Constraints:**

- UNIQUE: name
- UNIQUE: rank_order

### Example Data

```sql
INSERT INTO membership_tiers (name, rank_order, spending_required, discount_percent, points_multiplier) VALUES
('Bronze', 1, 0, 0, 1.0),
('Silver', 2, 1000000, 5, 1.2),
('Gold', 3, 5000000, 10, 1.5),
('Platinum', 4, 10000000, 15, 2.0);
```

---

## 2. user_memberships Table

**Mục đích:** Thông tin thành viên của từng user

| Column         | Type          | Description            |
| -------------- | ------------- | ---------------------- |
| id             | SERIAL        | ID                     |
| user_id        | BIGINT        | FK → users (UNIQUE)    |
| tier_id        | INTEGER       | FK → membership_tiers  |
| total_spending | DECIMAL(10,2) | Tổng chi tiêu tích lũy |
| points         | INTEGER       | Điểm tích lũy          |
| joined_at      | TIMESTAMP     | Thời điểm tham gia     |
| updated_at     | TIMESTAMP     | Thời điểm cập nhật     |

**Constraints:**

- UNIQUE: user_id (mỗi user chỉ có 1 membership)

---

## Business Logic

### Tính điểm khi booking

```
points_earned = booking_amount * tier.points_multiplier
```

### Áp dụng giảm giá

```
discount_amount = booking_amount * (tier.discount_percent / 100)
final_amount = booking_amount - discount_amount
```

### Tự động nâng hạng

```sql
-- Sau mỗi booking thành công
UPDATE user_memberships um
SET tier_id = (
    SELECT id FROM membership_tiers
    WHERE spending_required <= um.total_spending
    ORDER BY rank_order DESC
    LIMIT 1
)
WHERE user_id = ?;
```

---

## API Usage

### GET /api/membership-tiers

Trả về: id, name, image_url, description (cho homepage)

### GET /api/users/{id}/membership

Trả về: Thông tin membership hiện tại + progress đến hạng tiếp theo

---

## Relationships

```
users (1) ──── (1) user_memberships (N) ──── (1) membership_tiers
```
