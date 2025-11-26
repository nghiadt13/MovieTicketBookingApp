# API Danh Sách Rạp Chiếu Phim Theo Phim

## Endpoint

```
GET /api/cinemas
```

## Mô Tả

Lấy danh sách các rạp chiếu phim đang chiếu một bộ phim cụ thể. API này được sử dụng khi người dùng bấm "Đặt vé" từ trang chi tiết phim để xem các rạp đang có suất chiếu cho phim đó.

## Request

### Query Parameters

| Tham số | Kiểu   | Bắt buộc | Mô tả                                           |
| ------- | ------ | -------- | ----------------------------------------------- |
| movieId | Long   | Có       | ID của phim cần tìm rạp chiếu                   |
| city    | String | Không    | Lọc rạp theo thành phố (VD: "Hà Nội", "TP.HCM") |

### Ví Dụ Request

```
GET /api/cinemas?movieId=1
GET /api/cinemas?movieId=1&city=Hà Nội
GET /api/cinemas?movieId=5&city=TP.HCM
```

## Response

### Success Response (200 OK)

```json
[
  {
    "id": 1,
    "name": "CGV Vincom Bà Triệu",
    "address": "191 Bà Triệu, Hai Bà Trưng",
    "city": "Hà Nội",
    "district": "Hai Bà Trưng",
    "phoneNumber": "1900-6017",
    "email": "cgv.batrieu@cgv.vn",
    "latitude": 21.0123,
    "longitude": 105.849,
    "imageUrl": "https://example.com/images/cgv-batrieu.jpg"
  },
  {
    "id": 2,
    "name": "CGV Vincom Metropolis",
    "address": "29 Liễu Giai, Ba Đình",
    "city": "Hà Nội",
    "district": "Ba Đình",
    "phoneNumber": "1900-6017",
    "email": "cgv.metropolis@cgv.vn",
    "latitude": null,
    "longitude": null,
    "imageUrl": null
  },
  {
    "id": 5,
    "name": "CGV Vincom Center Đồng Khởi",
    "address": "72 Lê Thánh Tôn, Quận 1",
    "city": "TP.HCM",
    "district": "Quận 1",
    "phoneNumber": "1900-6017",
    "email": "cgv.dongkhoi@cgv.vn",
    "latitude": 10.7769,
    "longitude": 106.7009,
    "imageUrl": "https://example.com/images/cgv-dongkhoi.jpg"
  }
]
```

### Response khi không có rạp chiếu phim này (200 OK)

```json
[]
```

### Error Response (400 Bad Request)

Khi không truyền `movieId`:

```json
{
  "error": "Bad Request",
  "message": "Required request parameter 'movieId' is not present"
}
```

## Cấu Trúc Dữ Liệu

### CinemaDto

| Field       | Kiểu       | Nullable | Mô tả                                            |
| ----------- | ---------- | -------- | ------------------------------------------------ |
| id          | Long       | Không    | ID duy nhất của rạp chiếu phim                   |
| name        | String     | Không    | Tên rạp chiếu phim                               |
| address     | String     | Không    | Địa chỉ chi tiết của rạp                         |
| city        | String     | Không    | Thành phố (VD: "Hà Nội", "TP.HCM")               |
| district    | String     | Có       | Quận/Huyện (VD: "Hai Bà Trưng", "Quận 1")        |
| phoneNumber | String     | Có       | Số điện thoại liên hệ                            |
| email       | String     | Có       | Email liên hệ                                    |
| latitude    | BigDecimal | Có       | Vĩ độ (dùng cho bản đồ, precision 10, scale 8)   |
| longitude   | BigDecimal | Có       | Kinh độ (dùng cho bản đồ, precision 11, scale 8) |
| imageUrl    | String     | Có       | URL hình ảnh của rạp chiếu phim                  |

### Lưu Ý Về Kiểu Dữ Liệu

- **id**: Long - Số nguyên (VD: `1`, `2`, `100`)
- **name, address, city, district, phoneNumber, email**: String - Chuỗi ký tự
- **latitude, longitude**: BigDecimal - Số thập phân (VD: `21.01234567`, `105.84901234`)
- **imageUrl**: String - URL hình ảnh (VD: `"https://example.com/images/cinema.jpg"`)
- Các trường `district`, `phoneNumber`, `email`, `latitude`, `longitude`, `imageUrl` có thể là `null`

## Đặc Điểm API

- Danh sách rạp được sắp xếp theo tên (A-Z)
- Bắt buộc phải truyền `movieId` để lấy danh sách rạp đang chiếu phim đó
- Sử dụng query parameter `city` để lọc thêm theo thành phố (tùy chọn)
- API chỉ trả về các rạp có suất chiếu:
  - Rạp đang hoạt động (`cinemas.is_active = true`)
  - Phòng chiếu đang hoạt động (`screens.is_active = true`)
  - Suất chiếu đang hoạt động (`showtimes.is_active = true`)
  - Suất chiếu trong tương lai (`start_time >= NOW()`)
- Không yêu cầu xác thực (public endpoint)

## Luồng Hoạt Động

1. User xem chi tiết phim và bấm "Đặt vé"
2. Frontend gọi API với `movieId` của phim đó
3. Backend query các suất chiếu của phim → lấy danh sách rạp tương ứng
4. Trả về danh sách rạp đang chiếu phim đó

## Danh Sách Rạp Hiện Có

Hệ thống hiện có các rạp sau:

### Hà Nội

1. CGV Vincom Bà Triệu
2. CGV Vincom Metropolis
3. Lotte Cinema Đống Đa
4. Beta Cinemas Mỹ Đình

### TP.HCM

1. CGV Vincom Center Đồng Khởi
2. Galaxy Nguyễn Du
3. BHD Star Vincom 3/2
