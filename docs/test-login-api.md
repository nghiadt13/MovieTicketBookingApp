# Test Login API

## Endpoint

```
POST http://localhost:8080/api/auth/login
```

## Test với cURL

### Test 1: Login với email

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"alice.johnson@example.com\",\"password\":\"password123\"}"
```

### Test 2: Login với phone number

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"0912345678\",\"password\":\"password123\"}"
```

### Test 3: Login thất bại - sai password

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"alice.johnson@example.com\",\"password\":\"wrongpassword\"}"
```

### Test 4: Login thất bại - user không tồn tại

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"notexist@example.com\",\"password\":\"password123\"}"
```

## Response Format

### Success Response (200 OK)

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
    "roles": ["ROLE_USER"],
    "createdAt": "2025-01-15T10:00:00",
    "updatedAt": "2025-01-20T14:25:30"
  },
  "token": "temporary-token-1"
}
```

### Failed Response (200 OK)

```json
{
  "success": false,
  "message": "Invalid username or password",
  "user": null,
  "token": null
}
```

## Lưu ý

1. **Password Hashing**: API sử dụng BCrypt để verify password. Trong database, password phải được hash bằng BCrypt.

2. **Tạo test user với BCrypt password**:

```sql
-- Password: "password123"
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO users (email, phone_number, password_hash, display_name, is_active)
VALUES (
  'test@example.com',
  '0901234567',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Test User',
  true
);
```

3. **Username có thể là**:

   - Email address
   - Phone number

4. **Security**:
   - Endpoint `/api/auth/**` đã được cấu hình public trong SecurityConfig
   - Token hiện tại là temporary, cần implement JWT sau này
