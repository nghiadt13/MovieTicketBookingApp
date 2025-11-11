# Test Accounts

## Password chung cho tất cả accounts

**Password**: `password123`

**BCrypt Hash**: `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy`

---

## Danh sách Test Accounts

### 1. Test User 1 (Full verified)

- **Email**: `test1@example.com`
- **Phone**: `0901234567`
- **Password**: `password123`
- **Status**: Active ✅
- **Email Verified**: Yes ✅
- **Phone Verified**: Yes ✅
- **Roles**: ROLE_USER
- **Use case**: Test login thành công với email hoặc phone

**Test login với email**:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test1@example.com","password":"password123"}'
```

**Test login với phone**:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"0901234567","password":"password123"}'
```

---

### 2. Test User 2 (Email verified only)

- **Email**: `test2@example.com`
- **Phone**: `0902345678`
- **Password**: `password123`
- **Status**: Active ✅
- **Email Verified**: Yes ✅
- **Phone Verified**: No ❌
- **Roles**: ROLE_USER
- **Use case**: Test user chưa verify phone

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test2@example.com","password":"password123"}'
```

---

### 3. Test User 3 (Phone only)

- **Email**: None
- **Phone**: `0903456789`
- **Password**: `password123`
- **Status**: Active ✅
- **Email Verified**: N/A
- **Phone Verified**: Yes ✅
- **Roles**: ROLE_USER
- **Use case**: Test user đăng ký chỉ bằng phone

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"0903456789","password":"password123"}'
```

---

### 4. Inactive User

- **Email**: `inactive@example.com`
- **Phone**: `0904567890`
- **Password**: `password123`
- **Status**: Inactive ❌
- **Email Verified**: Yes ✅
- **Phone Verified**: Yes ✅
- **Roles**: ROLE_USER
- **Use case**: Test login fail với account bị vô hiệu hóa

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"inactive@example.com","password":"password123"}'
```

**Expected response**:

```json
{
  "success": false,
  "message": "Account is inactive",
  "user": null,
  "token": null
}
```

---

### 5. Admin User

- **Email**: `admin@example.com`
- **Phone**: `0905678901`
- **Password**: `password123`
- **Status**: Active ✅
- **Email Verified**: Yes ✅
- **Phone Verified**: Yes ✅
- **Roles**: ROLE_USER, ROLE_ADMIN
- **Use case**: Test user với multiple roles

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@example.com","password":"password123"}'
```

---

## Test Cases

### ✅ Success Cases

1. **Login với email**: `test1@example.com` + `password123`
2. **Login với phone**: `0901234567` + `password123`
3. **Login user chưa verify phone**: `test2@example.com` + `password123`
4. **Login user chỉ có phone**: `0903456789` + `password123`
5. **Login admin**: `admin@example.com` + `password123`

### ❌ Failure Cases

1. **Sai password**: `test1@example.com` + `wrongpassword`

   - Expected: `"Invalid username or password"`

2. **User không tồn tại**: `notexist@example.com` + `password123`

   - Expected: `"Invalid username or password"`

3. **Account inactive**: `inactive@example.com` + `password123`

   - Expected: `"Account is inactive"`

4. **Empty username**: `""` + `password123`

   - Expected: 400 Bad Request với validation error

5. **Empty password**: `test1@example.com` + `""`
   - Expected: 400 Bad Request với validation error

---

## Cách tạo password hash mới

Nếu bạn muốn tạo password hash cho password khác, có thể dùng:

### Online BCrypt Generator

- https://bcrypt-generator.com/
- Chọn rounds = 10
- Nhập password và copy hash

### Hoặc dùng Kotlin code

```kotlin
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

fun main() {
    val encoder = BCryptPasswordEncoder()
    val password = "yourpassword"
    val hash = encoder.encode(password)
    println("Password: $password")
    println("Hash: $hash")
}
```

### Hoặc dùng command line (nếu có htpasswd)

```bash
htpasswd -bnBC 10 "" password123 | tr -d ':\n'
```

---

## Import vào database

Chạy file `test-users.sql` để tạo tất cả test accounts:

```bash
psql -U postgres -d MobileApp -f test-users.sql
```

Hoặc copy nội dung file và paste vào DB client của bạn.
