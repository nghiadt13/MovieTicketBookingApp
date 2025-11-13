# Membership Tiers API Documentation

## Tổng quan

API Membership Tiers cho phép lấy danh sách tất cả các hạng thành viên (membership tiers) trong hệ thống. API trả về thông tin cơ bản về các hạng thành viên để hiển thị trên homepage, bao gồm id, tên, hình ảnh và mô tả.

---

## Endpoint

```
GET /api/membership-tiers
```

**Base URL**: `http://localhost:8080` (development)

**Content-Type**: `application/json`

---

## Request

Không cần request body hoặc parameters. Đây là GET request đơn giản.

### Example Request

```bash
curl -X GET http://localhost:8080/api/membership-tiers
```

---

## Response

### Success Response (200 OK)

Trả về danh sách tất cả các hạng thành viên, được sắp xếp theo thứ tự rank (từ thấp đến cao):

```json
[
  {
    "id": 1,
    "name": "Bronze",
    "imageUrl": "https://example.com/bronze.png",
    "description": "Hạng thành viên cơ bản dành cho người mới bắt đầu"
  },
  {
    "id": 2,
    "name": "Silver",
    "imageUrl": "https://example.com/silver.png",
    "description": "Hạng thành viên bạc với nhiều ưu đãi hơn"
  },
  {
    "id": 3,
    "name": "Gold",
    "imageUrl": "https://example.com/gold.png",
    "description": "Hạng thành viên vàng với quyền lợi cao cấp"
  },
  {
    "id": 4,
    "name": "Platinum",
    "imageUrl": "https://example.com/platinum.png",
    "description": "Hạng thành viên cao nhất với đặc quyền VIP"
  }
]
```

### Response Fields

| Field         | Type   | Description                                    |
| ------------- | ------ | ---------------------------------------------- |
| `id`          | number | ID của hạng thành viên                         |
| `name`        | string | Tên hạng thành viên                            |
| `imageUrl`    | string | URL hình ảnh đại diện cho hạng thành viên      |
| `description` | string | Mô tả chi tiết về hạng thành viên và quyền lợi |

---

## Flow hoạt động

### 1. Client gửi request

```
Mobile App → GET /api/membership-tiers
```

### 2. Server xử lý

**Bước 1**: Truy vấn database

- Query tất cả membership tiers từ bảng `membership_tiers`
- Sắp xếp theo `rank_order ASC` (từ thấp đến cao)

**Bước 2**: Map entity sang DTO

- Convert từ MembershipTier entity sang MembershipTierDto
- Chỉ lấy các fields cần thiết: id, name, imageUrl, description
- Các fields khác như spending_required, discount_percent, points_multiplier không được trả về

**Bước 3**: Trả về response

- HTTP 200 OK
- Body: Array of MembershipTierDto

### 3. Client nhận response

```
Mobile App ← 200 OK
[
  { "id": 1, "name": "Bronze", "imageUrl": "...", "description": "..." },
  { "id": 2, "name": "Silver", "imageUrl": "...", "description": "..." },
  ...
]
```

---

## Xử lý ở Mobile App

### 1. Gửi Request

Gửi GET request đến `/api/membership-tiers`:

```kotlin
// Android example
val response = apiService.getMembershipTiers()
```


### 2. Xử lý Response

Khi nhận được response, parse JSON array và hiển thị:

```kotlin
// Android example
response.forEach { tier ->
    // Hiển thị tier.name, tier.imageUrl, tier.description
    // trong RecyclerView hoặc LazyColumn
}
```

### 3. Hiển thị UI

**Gợi ý hiển thị**:

1. Hiển thị danh sách các hạng thành viên theo thứ tự từ thấp đến cao
2. Mỗi item hiển thị:
   - Hình ảnh đại diện (imageUrl)
   - Tên hạng (name)
   - Mô tả ngắn (description)
3. Có thể thêm indicator để highlight hạng hiện tại của user
4. Cho phép user tap vào để xem chi tiết quyền lợi

---

## Database Schema

```sql
CREATE TABLE membership_tiers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    rank_order INTEGER NOT NULL UNIQUE,
    spending_required DECIMAL(10, 2) NOT NULL,
    discount_percent DECIMAL(5, 2) DEFAULT 0,
    points_multiplier DECIMAL(3, 2) DEFAULT 1.0,
    image_url VARCHAR(500),
    description TEXT
);
```

**Lưu ý**: API chỉ trả về id, name, image_url, description. Các thông tin về spending_required, discount_percent, points_multiplier sẽ được sử dụng cho các API khác hoặc logic backend.

---

## Use Cases

### Use Case 1: Hiển thị membership tiers trên homepage

User mở app → Homepage load → Gọi API `/api/membership-tiers` → Hiển thị danh sách các hạng thành viên mà user có thể đạt được 
Danh sách này cũng sẽ được hiển thị dưới dạng carousel, nằm bên dưới phần news.



---

## Notes

- API này không yêu cầu authentication
- Dữ liệu được cache ở client side vì ít thay đổi
- Nếu cần thông tin chi tiết hơn (spending_required, discount_percent, etc.), cần tạo API riêng hoặc mở rộng API này
- Thứ tự trả về luôn theo rank_order từ thấp đến cao
