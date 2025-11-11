# Login API Documentation

## Tổng quan

API Login cho phép người dùng đăng nhập vào hệ thống bằng email hoặc số điện thoại kèm theo password. API sẽ xác thực thông tin và trả về thông tin user cùng token để sử dụng cho các request tiếp theo.

---

## Endpoint

```
POST /api/auth/login
```

**Base URL**: `http://localhost:8080` (development)

**Content-Type**: `application/json`

---

## Request Body

### Schema

```json
{
  "username": "string (required)",
  "password": "string (required)"
}
```

### Fields

| Field      | Type   | Required | Description                       |
| ---------- | ------ | -------- | --------------------------------- |
| `username` | string | ✅ Yes   | Email hoặc số điện thoại của user |
| `password` | string | ✅ Yes   | Mật khẩu của user (plain text)    |

### Validation Rules

- `username`: Không được để trống
- `password`: Không được để trống

### Example Request

```json
{
  "username": "alice.johnson@example.com",
  "password": "password123"
}
```

hoặc

```json
{
  "username": "0912345678",
  "password": "password123"
}
```

---

## Response

### Success Response (200 OK)

Khi đăng nhập thành công:

```json
{
  "success": true,
  "message": "Login successful",
  "user": {
    "id": 1,
    "email": "alice.johnson@example.com",
    "phoneNumber": "0912345678",
    "displayName": "Alice Johnson",
    "avatarUrl": "https://example.com/avatars/alice.png",
    "isActive": true,
    "emailVerifiedAt": "2025-01-15T10:30:00",
    "phoneNumberVerifiedAt": "2025-01-15T10:30:00",
    "lastLoginAt": "2025-01-20T14:25:30",
    "roles": ["ROLE_USER", "ROLE_ADMIN"],
    "createdAt": "2025-01-15T10:00:00",
    "updatedAt": "2025-01-20T14:25:30"
  },
  "token": "temporary-token-1"
}
```

### Failed Response (200 OK)

Khi đăng nhập thất bại:

```json
{
  "success": false,
  "message": "Invalid username or password",
  "user": null,
  "token": null
}
```

hoặc khi tài khoản bị vô hiệu hóa:

```json
{
  "success": false,
  "message": "Account is inactive",
  "user": null,
  "token": null
}
```

### Validation Error (400 Bad Request)

Khi request body không hợp lệ:

```json
{
  "timestamp": "2025-01-20T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/auth/login",
  "fieldErrors": {
    "username": "Username (email or phone) is required",
    "password": "Password is required"
  }
}
```

---

## Response Fields

### LoginResponse Object

| Field     | Type            | Description                                                    |
| --------- | --------------- | -------------------------------------------------------------- |
| `success` | boolean         | `true` nếu login thành công, `false` nếu thất bại              |
| `message` | string          | Thông báo kết quả (success/error message)                      |
| `user`    | UserDto \| null | Thông tin user nếu login thành công, `null` nếu thất bại       |
| `token`   | string \| null  | Authentication token nếu login thành công, `null` nếu thất bại |

### UserDto Object

| Field                   | Type           | Description                                                |
| ----------------------- | -------------- | ---------------------------------------------------------- |
| `id`                    | number         | ID của user trong database                                 |
| `email`                 | string \| null | Email của user (có thể null nếu user đăng ký bằng phone)   |
| `phoneNumber`           | string \| null | Số điện thoại (có thể null nếu user đăng ký bằng email)    |
| `displayName`           | string         | Tên hiển thị của user                                      |
| `avatarUrl`             | string \| null | URL của avatar (có thể null)                               |
| `isActive`              | boolean        | Trạng thái tài khoản (true = active, false = inactive)     |
| `emailVerifiedAt`       | string \| null | Thời điểm verify email (ISO 8601 format)                   |
| `phoneNumberVerifiedAt` | string \| null | Thời điểm verify phone (ISO 8601 format)                   |
| `lastLoginAt`           | string \| null | Thời điểm login gần nhất (ISO 8601 format)                 |
| `roles`                 | string[]       | Danh sách roles của user (VD: ["ROLE_USER", "ROLE_ADMIN"]) |
| `createdAt`             | string         | Thời điểm tạo tài khoản (ISO 8601 format)                  |
| `updatedAt`             | string         | Thời điểm cập nhật gần nhất (ISO 8601 format)              |

---

## Flow hoạt động

### 1. Client gửi request

```
Mobile App → POST /api/auth/login
{
  "username": "alice@example.com",
  "password": "password123"
}
```

### 2. Server xử lý

**Bước 1**: Validate request body

- Kiểm tra `username` và `password` không được rỗng
- Nếu validation fail → trả về 400 Bad Request

**Bước 2**: Tìm user trong database

- Tìm user theo `email` trước
- Nếu không tìm thấy → tìm theo `phone_number`
- Nếu không tìm thấy → trả về "Invalid username or password"

**Bước 3**: Kiểm tra trạng thái tài khoản

- Kiểm tra `is_active = true`
- Nếu `is_active = false` → trả về "Account is inactive"

**Bước 4**: Verify password

- So sánh password với `password_hash` trong database bằng BCrypt
- Nếu không khớp → trả về "Invalid username or password"

**Bước 5**: Cập nhật last login

- Set `last_login_at = NOW()`
- Lưu vào database

**Bước 6**: Tạo response

- Load thông tin user kèm roles
- Generate token (hiện tại là temporary token)
- Trả về success response

### 3. Client nhận response

```
Mobile App ← 200 OK
{
  "success": true,
  "message": "Login successful",
  "user": { ... },
  "token": "temporary-token-1"
}
```

---

## Xử lý ở Mobile App

### 1. Gửi Login Request

Gửi POST request đến `/api/auth/login` với body:

```json
{
  "username": "email hoặc phone",
  "password": "mật khẩu"
}
```

### 2. Xử lý Response

Khi nhận được response, kiểm tra field `success`:

**Nếu `success = true`**:

1. Lưu `token` vào storage an toàn (Keychain/EncryptedSharedPreferences)
2. Lưu thông tin `user` để sử dụng trong app
3. Navigate đến màn hình chính
4. Hiển thị thông báo thành công

**Nếu `success = false`**:

1. Hiển thị `message` cho user
2. Giữ nguyên màn hình login
3. Clear password field

### 3. Lưu trữ Token

Token cần được lưu trữ an toàn:

- **iOS**: Sử dụng Keychain
- **Android**: Sử dụng EncryptedSharedPreferences hoặc DataStore với encryption
- **KHÔNG** lưu trong storage không mã hóa

### 4. Sử dụng Token cho các request tiếp theo

Thêm token vào header của mọi API request:

```
Authorization: Bearer {token}
```

### 5. Xử lý các trường hợp đặc biệt

#### Case 1: User chưa verify email/phone

Kiểm tra các field:

- `emailVerifiedAt = null` → Hiển thị banner yêu cầu verify email
- `phoneNumberVerifiedAt = null` → Hiển thị banner yêu cầu verify phone

#### Case 2: Kiểm tra roles

Dựa vào array `roles` để hiển thị features phù hợp:

- `ROLE_ADMIN` → Hiển thị admin panel
- `ROLE_STAFF` → Hiển thị staff features
- `ROLE_USER` → User features thông thường

#### Case 3: Hiển thị thông tin user

Sử dụng các field trong `user` object:

- `displayName` → Tên hiển thị
- `avatarUrl` → Load avatar (có placeholder nếu null)
- `email` / `phoneNumber` → Thông tin liên hệ (có thể null)

---

## Error Handling

### Các loại lỗi có thể xảy ra

| Error Type          | HTTP Status | Response             | Xử lý                                  |
| ------------------- | ----------- | -------------------- | -------------------------------------- |
| Validation Error    | 400         | `fieldErrors` object | Hiển thị lỗi từng field                |
| Invalid Credentials | 200         | `success: false`     | Show "Sai tên đăng nhập hoặc mật khẩu" |
| Account Inactive    | 200         | `success: false`     | Show "Tài khoản đã bị vô hiệu hóa"     |
| Network Error       | -           | Exception            | Show "Không thể kết nối đến server"    |
| Server Error        | 500         | Error response       | Show "Lỗi server, vui lòng thử lại"    |

### Example Error Handling Flow

**Bước 1**: Kiểm tra HTTP status code

- `200 OK` → Kiểm tra field `success` trong response
- `400 Bad Request` → Validation error, hiển thị `fieldErrors`
- `500 Server Error` → Hiển thị "Lỗi server, vui lòng thử lại sau"
- Network error → Hiển thị "Không có kết nối internet"

**Bước 2**: Nếu status = 200, kiểm tra `success` field

- `success = true` → Xử lý login thành công
- `success = false` → Kiểm tra `message`:
  - Chứa "inactive" → "Tài khoản đã bị vô hiệu hóa"
  - Khác → "Tên đăng nhập hoặc mật khẩu không đúng"

---

## Security Notes

### 1. Password Security

- ❌ **KHÔNG** lưu password dưới dạng plain text
- ❌ **KHÔNG** log password ra console/log files
- ✅ Chỉ gửi password qua HTTPS
- ✅ Clear password từ memory/input field sau khi sử dụng

### 2. Token Security

- ✅ Lưu token trong Keychain (iOS) hoặc EncryptedSharedPreferences (Android)
- ✅ Gửi token qua Authorization header
- ✅ Implement token refresh mechanism
- ❌ **KHÔNG** lưu token trong UserDefaults/SharedPreferences không mã hóa

### 3. HTTPS Only

- ✅ Chỉ gọi API qua HTTPS trong production
- ✅ Implement certificate pinning nếu cần bảo mật cao

---

## Testing

### cURL Examples

**Success case**:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice.johnson@example.com","password":"password123"}'
```

**Invalid credentials**:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice@example.com","password":"wrongpassword"}'
```

**Validation error**:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"","password":""}'
```

---

## Changelog

### Version 1.0 (Current)

- ✅ Login với email hoặc phone number
- ✅ BCrypt password verification
- ✅ Account active check
- ✅ Last login tracking
- ✅ Role-based access control support
- ⚠️ Temporary token (cần implement JWT)

### Future Improvements

- [ ] Implement JWT token với expiration
- [ ] Add refresh token mechanism
- [ ] Add rate limiting để prevent brute force
- [ ] Add 2FA support
- [ ] Add "Remember me" functionality
- [ ] Add login history tracking

---

## Support

Nếu có vấn đề hoặc câu hỏi, vui lòng liên hệ backend team.
