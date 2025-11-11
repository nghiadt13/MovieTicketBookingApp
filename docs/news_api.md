# News API Documentation

## Tổng quan

API News cho phép lấy danh sách 10 tin tức mới nhất được cập nhật trong hệ thống. API trả về các tin tức đang active (is_active = true) và sắp xếp theo thời gian cập nhật gần nhất.

---

## Endpoint

```
GET /api/news
```

**Base URL**: `http://localhost:8080` (development)

**Content-Type**: `application/json`

---

## Request

Không cần request body hoặc parameters. Đây là GET request đơn giản.

### Example Request

```bash
curl -X GET http://localhost:8080/api/news
```

---

## Response

### Success Response (200 OK)

Trả về danh sách 10 tin tức mới nhất:

```json
[
  {
    "id": 11,
    "title": "Behind the Scenes",
    "content": "Go behind the scenes of our latest blockbuster...",
    "imageUrl": "https://example.com/news11.jpg",
    "author": "Content Team",
    "isActive": true,
    "createdAt": "2025-01-01T10:00:00",
    "updatedAt": "2025-01-11T14:30:00"
  },
  {
    "id": 1,
    "title": "Breaking: New Movie Release",
    "content": "Exciting new movie coming to theaters this weekend...",
    "imageUrl": "https://example.com/news1.jpg",
    "author": "John Doe",
    "isActive": true,
    "createdAt": "2025-01-01T10:00:00",
    "updatedAt": "2025-01-10T14:30:00"
  }
  // ... 8 more items
]
```

### Response Fields

| Field       | Type    | Description                                   |
| ----------- | ------- | --------------------------------------------- |
| `id`        | number  | ID duy nhất của tin tức                       |
| `title`     | string  | Tiêu đề tin tức                               |
| `content`   | string  | Nội dung chi tiết (có thể null)               |
| `imageUrl`  | string  | URL hình ảnh đại diện (có thể null)           |
| `author`    | string  | Tác giả/người đăng tin (có thể null)          |
| `isActive`  | boolean | Trạng thái active của tin tức                 |
| `createdAt` | string  | Thời điểm tạo tin tức (ISO 8601 format)       |
| `updatedAt` | string  | Thời điểm cập nhật gần nhất (ISO 8601 format) |

---

## Flow hoạt động

### 1. Client gửi request

```
Mobile App → GET /api/news
```

### 2. Server xử lý

**Bước 1**: Truy vấn database

- Query 10 tin tức có `is_active = true`
- Sắp xếp theo `updated_at DESC` (mới nhất trước)
- Limit 10 records

**Bước 2**: Map entity sang DTO

- Convert từ News entity sang NewsDto
- Đảm bảo tất cả fields được map đúng

**Bước 3**: Trả về response

- HTTP 200 OK
- Body: Array of NewsDto

### 3. Client nhận response

```
Mobile App ← 200 OK
[
  { "id": 11, "title": "...", ... },
  { "id": 1, "title": "...", ... },
  ...
]
```

---

## Xử lý ở Mobile App

### 1. Gửi Request

Gửi GET request đến `/api/news`:

```kotlin
// Android example
val response = apiService.getLatestNews()
```

```swift
// iOS example
let response = try await apiService.getLatestNews()
```

### 2. Xử lý Response

Khi nhận được response:

1. Parse JSON array thành list of News objects
2. Hiển thị trong RecyclerView/UITableView
3. Sắp xếp theo `updatedAt` (đã được sort từ server)

### 3. Hiển thị UI

**News Item Layout**:

- Hiển thị `imageUrl` (với placeholder nếu null)
- Hiển thị `title` (bold, prominent)
- Hiển thị `content` preview (truncate nếu quá dài)
- Hiển thị `author` và `updatedAt` (secondary text)

**Example UI**:

```
┌─────────────────────────────────────┐
│ [Image]  Behind the Scenes          │
│          Go behind the scenes...    │
│          By Content Team • 2h ago   │
└─────────────────────────────────────┘
```

### 4. Xử lý các trường hợp đặc biệt

#### Case 1: Empty list

- Nếu API trả về array rỗng `[]`
- Hiển thị "Chưa có tin tức nào"

#### Case 2: Null fields

- `content = null` → Không hiển thị content preview
- `imageUrl = null` → Hiển thị placeholder image
- `author = null` → Không hiển thị author

#### Case 3: Refresh data

- Implement pull-to-refresh
- Gọi lại API để lấy tin tức mới nhất

---

## Error Handling

### Các loại lỗi có thể xảy ra

| Error Type    | HTTP Status | Response       | Xử lý                               |
| ------------- | ----------- | -------------- | ----------------------------------- |
| Network Error | -           | Exception      | Show "Không thể kết nối đến server" |
| Server Error  | 500         | Error response | Show "Lỗi server, vui lòng thử lại" |
| Empty Result  | 200         | `[]`           | Show "Chưa có tin tức"              |

### Example Error Handling Flow

```kotlin
try {
    val news = apiService.getLatestNews()
    if (news.isEmpty()) {
        showEmptyState()
    } else {
        displayNews(news)
    }
} catch (e: IOException) {
    showError("Không có kết nối internet")
} catch (e: Exception) {
    showError("Đã có lỗi xảy ra")
}
```

---

## Security Notes

### 1. Public Endpoint

- ✅ Endpoint này là public, không cần authentication
- ✅ Chỉ trả về tin tức đã được approve (`is_active = true`)

### 2. Data Validation

- ✅ Server tự động filter inactive news
- ✅ Limit 10 records để tránh overload

### 3. HTTPS Only

- ✅ Chỉ gọi API qua HTTPS trong production

---

## Testing

### cURL Examples

**Get latest news**:

```bash
curl -X GET http://localhost:8080/api/news
```

**With headers**:

```bash
curl -X GET http://localhost:8080/api/news \
  -H "Accept: application/json"
```

### Expected Response

```json
[
  {
    "id": 11,
    "title": "Behind the Scenes",
    "content": "Go behind the scenes of our latest blockbuster...",
    "imageUrl": "https://example.com/news11.jpg",
    "author": "Content Team",
    "isActive": true,
    "createdAt": "2025-01-01T10:00:00",
    "updatedAt": "2025-01-11T14:30:00"
  }
  // ... more items
]
```

---

## Database Schema

```sql
CREATE TABLE news (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    image_url VARCHAR(500),
    author VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_news_updated_at ON news(updated_at DESC);
CREATE INDEX idx_news_is_active ON news(is_active);
```

---

## Performance Notes

### Optimization

- ✅ Index trên `updated_at` để sort nhanh
- ✅ Index trên `is_active` để filter nhanh
- ✅ Limit 10 records để giảm data transfer
- ✅ Lazy loading nếu cần load thêm

### Caching Strategy

**Client-side**:

- Cache response trong 5-10 phút
- Implement pull-to-refresh để update
- Clear cache khi user logout (nếu có auth)

**Server-side** (future):

- Có thể implement Redis cache
- Cache invalidation khi có news mới

---

## Changelog

### Version 1.0 (Current)

- ✅ Get 10 latest active news
- ✅ Sort by updated_at DESC
- ✅ Filter only active news
- ✅ Full field mapping

### Future Improvements

- [ ] Add pagination support
- [ ] Add category filter
- [ ] Add search functionality
- [ ] Add news detail endpoint
- [ ] Add view count tracking
