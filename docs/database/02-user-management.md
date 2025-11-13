# User Management Module

## 1. users Table

**Mục đích:** Lưu trữ thông tin người dùng cơ bản

### Schema

| Column                   | Type        | Constraints             | Description                                  |
| ------------------------ | ----------- | ----------------------- | -------------------------------------------- |
| id                       | BIGINT      | PRIMARY KEY, IDENTITY   | ID tự động tăng                              |
| email                    | CITEXT      | UNIQUE                  | Email đăng nhập (nullable)                   |
| phone_number             | CITEXT      | UNIQUE                  | Số điện thoại đăng nhập (nullable)           |
| password_hash            | TEXT        |                         | Mật khẩu đã hash (nullable cho social login) |
| display_name             | TEXT        | NOT NULL                | Tên hiển thị                                 |
| avatar_url               | TEXT        |                         | URL ảnh đại diện                             |
| is_active                | BOOLEAN     | NOT NULL, DEFAULT TRUE  | Trạng thái tài khoản                         |
| email_verified_at        | TIMESTAMPTZ |                         | Thời điểm verify email                       |
| phone_number_verified_at | TIMESTAMPTZ |                         | Thời điểm verify phone                       |
| last_login_at            | TIMESTAMPTZ |                         | Lần đăng nhập cuối                           |
| created_at               | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Thời điểm tạo                                |
| updated_at               | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Thời điểm cập nhật                           |

### Constraints

- CHECK: `email IS NOT NULL OR phone_number IS NOT NULL` (phải có ít nhất email hoặc phone)

### Triggers

- `trg_users_updated_at`: Tự động cập nhật `updated_at`

### Use Cases

- Đăng ký bằng email hoặc phone number
- Đăng nhập bằng email/phone + password
- Social login (password_hash có thể NULL)
- Verify email/phone qua OTP
- Soft delete bằng is_active flag

---

## 2. roles Table

**Mục đích:** Định nghĩa các vai trò trong hệ thống (RBAC)

### Schema

| Column | Type           | Constraints           | Description |
| ------ | -------------- | --------------------- | ----------- |
| id     | BIGINT         | PRIMARY KEY, IDENTITY | ID vai trò  |
| name   | user_role_enum | NOT NULL, UNIQUE      | Tên vai trò |

### Seed Data

```sql
INSERT INTO roles(name) VALUES
    ('ROLE_USER'),
    ('ROLE_ADMIN'),
    ('ROLE_STAFF');
```

### Role Descriptions

- **ROLE_USER**: Người dùng thông thường (đặt vé, xem phim)
- **ROLE_ADMIN**: Quản trị viên (full access)
- **ROLE_STAFF**: Nhân viên (quản lý rạp, suất chiếu)

---

## 3. user_roles Table

**Mục đích:** Junction table liên kết users và roles (Many-to-Many)

### Schema

| Column  | Type   | Constraints              | Description   |
| ------- | ------ | ------------------------ | ------------- |
| user_id | BIGINT | FK → users(id), CASCADE  | ID người dùng |
| role_id | BIGINT | FK → roles(id), RESTRICT | ID vai trò    |

**Primary Key:** (user_id, role_id)

### Indexes

- `idx_user_roles_role_id` on role_id

### Use Cases

- Một user có thể có nhiều roles
- Query tất cả users có role cụ thể
- Check permission dựa trên roles

---

## 4. social_accounts Table

**Mục đích:** Liên kết tài khoản với social providers (Google, Facebook, etc.)

### Schema

| Column           | Type                 | Constraints             | Description        |
| ---------------- | -------------------- | ----------------------- | ------------------ |
| id               | BIGINT               | PRIMARY KEY, IDENTITY   | ID                 |
| user_id          | BIGINT               | FK → users(id), CASCADE | ID người dùng      |
| provider         | social_provider_enum | NOT NULL                | Nhà cung cấp       |
| provider_user_id | TEXT                 | NOT NULL                | ID từ provider     |
| created_at       | TIMESTAMPTZ          | NOT NULL, DEFAULT now() | Thời điểm liên kết |

### Constraints

- UNIQUE: (user_id, provider) - Mỗi user chỉ link 1 account/provider
- UNIQUE: (provider, provider_user_id) - Mỗi social account chỉ link 1 user

### Indexes

- `idx_social_accounts_user_id` on user_id

### Use Cases

- Đăng nhập bằng Google/Facebook
- Link social account vào tài khoản hiện có
- Unlink social account

### Example Flow

```
1. User login với Google
2. Nhận provider_user_id từ Google
3. Tìm trong social_accounts:
   - Nếu có → Login vào user_id tương ứng
   - Nếu không → Tạo user mới + social_account record
```

---

## 5. user_otps Table

**Mục đích:** Quản lý OTP cho các mục đích khác nhau

### Schema

| Column        | Type             | Constraints             | Description          |
| ------------- | ---------------- | ----------------------- | -------------------- |
| id            | BIGINT           | PRIMARY KEY, IDENTITY   | ID                   |
| user_id       | BIGINT           | FK → users(id), CASCADE | ID người dùng        |
| purpose       | otp_purpose_enum | NOT NULL                | Mục đích OTP         |
| channel       | otp_channel_enum | NOT NULL                | Kênh gửi (EMAIL/SMS) |
| contact_value | TEXT             | NOT NULL                | Email/Phone nhận OTP |
| code_hash     | TEXT             | NOT NULL                | Hash của OTP code    |
| expires_at    | TIMESTAMPTZ      | NOT NULL                | Thời điểm hết hạn    |
| consumed_at   | TIMESTAMPTZ      |                         | Thời điểm sử dụng    |
| attempt_count | SMALLINT         | NOT NULL, DEFAULT 0     | Số lần thử           |
| created_at    | TIMESTAMPTZ      | NOT NULL, DEFAULT now() | Thời điểm tạo        |

### Constraints

- CHECK: `expires_at > created_at`

### Indexes

- `idx_user_otps_lookup` on (user_id, purpose) WHERE consumed_at IS NULL

### OTP Purposes

- **PASSWORD_RESET**: Reset mật khẩu
- **TWO_FACTOR_AUTH**: Xác thực 2 yếu tố
- **ACCOUNT_VERIFICATION**: Verify email/phone khi đăng ký

### Security Best Practices

1. Luôn hash OTP code trước khi lưu
2. Set expires_at (thường 5-10 phút)
3. Giới hạn attempt_count (max 3-5 lần)
4. Mark consumed_at sau khi sử dụng
5. Cleanup expired OTPs định kỳ

### Example Flow

```
1. User request OTP
2. Generate random 6-digit code
3. Hash code và lưu vào user_otps
4. Gửi code (plain) qua email/SMS
5. User nhập code
6. Hash input và so sánh với code_hash
7. Nếu đúng → Mark consumed_at
8. Nếu sai → Tăng attempt_count
```

---

## Relationships

```
users (1) ──── (N) user_roles (N) ──── (1) roles
users (1) ──── (N) social_accounts
users (1) ──── (N) user_otps
```

---

## Common Queries

### Get user with roles

```sql
SELECT u.*, array_agg(r.name) as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
WHERE u.id = ?
GROUP BY u.id;
```

### Find user by email or phone

```sql
SELECT * FROM users
WHERE email = ? OR phone_number = ?
LIMIT 1;
```

### Check if user has role

```sql
SELECT EXISTS(
    SELECT 1 FROM user_roles ur
    JOIN roles r ON ur.role_id = r.id
    WHERE ur.user_id = ? AND r.name = 'ROLE_ADMIN'
);
```

### Get valid OTP

```sql
SELECT * FROM user_otps
WHERE user_id = ?
  AND purpose = 'PASSWORD_RESET'
  AND consumed_at IS NULL
  AND expires_at > NOW()
  AND attempt_count < 5
ORDER BY created_at DESC
LIMIT 1;
```
