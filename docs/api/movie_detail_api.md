# API Chi Tiết Phim

## Endpoint

```
GET /api/movies/{id}
```

## Mô Tả

Lấy thông tin chi tiết của một bộ phim theo ID.

## Request

### Path Parameters

| Tham số | Kiểu | Bắt buộc | Mô tả                         |
| ------- | ---- | -------- | ----------------------------- |
| id      | Long | Có       | ID của phim cần lấy thông tin |

### Ví Dụ Request

```
GET /api/movies/1
```

## Response

### Success Response (200 OK)

```json
{
  "id": 1,
  "title": "Avengers: Endgame",
  "synopsis": "Sau sự kiện của Infinity War, các Avengers tập hợp lại một lần nữa để đảo ngược hành động của Thanos và khôi phục trật tự vũ trụ.",
  "durationMin": 181,
  "releaseDate": "2019-04-26",
  "status": "NOW_SHOWING",
  "posterUrl": "https://example.com/posters/avengers-endgame.jpg",
  "trailerUrl": "https://youtube.com/watch?v=example",
  "ratingAvg": 8.5,
  "ratingCount": 1250,
  "genres": [
    {
      "id": 1,
      "name": "Action",
      "slug": "action"
    },
    {
      "id": 2,
      "name": "Adventure",
      "slug": "adventure"
    }
  ],
  "formats": [
    {
      "id": 1,
      "code": "2D",
      "label": "2D Standard"
    },
    {
      "id": 2,
      "code": "IMAX",
      "label": "IMAX"
    }
  ]
}
```
### UI Display 
Phải hiển thị được toàn bộ thông tin phim từ response trả về lên UI, và sắp xếp , code UI 1 cách đẹp mắt, bố cục sắp xếp các thành phần đẹp mắt để người dùng có thể xem được toàn bộ thông tin phim.

### Error Response (404 Not Found)

Phim không tồn tại hoặc đã bị xóa mềm.

```json
{
  "message": "Movie not found"
}
```

### Error Response (400 Bad Request)

ID không hợp lệ.

```json
{
  "message": "Invalid movie ID"
}
```

### Error Response (500 Internal Server Error)

Lỗi server không xác định.

```json
{
  "message": "Internal server error"
}
```

## Cấu Trúc Dữ Liệu

### MovieDto

| Field       | Kiểu            | Nullable | Mô tả                                 |
| ----------- | --------------- | -------- | ------------------------------------- |
| id          | Long            | Không    | ID duy nhất của phim                  |
| title       | String          | Không    | Tên phim                              |
| synopsis    | String          | Có       | Tóm tắt nội dung phim                 |
| durationMin | Short           | Có       | Thời lượng phim (phút)                |
| releaseDate | LocalDate       | Có       | Ngày phát hành (YYYY-MM-DD)           |
| status      | MovieStatus     | Không    | Trạng thái phim                       |
| posterUrl   | String          | Có       | URL ảnh poster                        |
| trailerUrl  | String          | Có       | URL trailer                           |
| ratingAvg   | BigDecimal      | Không    | Điểm đánh giá trung bình (0.0 - 10.0) |
| ratingCount | Int             | Không    | Số lượng đánh giá                     |
| genres      | List<GenreDto>  | Không    | Danh sách thể loại                    |
| formats     | List<FormatDto> | Không    | Danh sách định dạng chiếu             |

### MovieStatus (Enum)

- `COMING_SOON` - Sắp chiếu
- `NOW_SHOWING` - Đang chiếu
- `ENDED` - Đã kết thúc

### GenreDto

| Field | Kiểu   | Mô tả             |
| ----- | ------ | ----------------- |
| id    | Long   | ID thể loại       |
| name  | String | Tên thể loại      |
| slug  | String | Slug URL-friendly |

### FormatDto

| Field | Kiểu   | Mô tả                           |
| ----- | ------ | ------------------------------- |
| id    | Long   | ID định dạng                    |
| code  | String | Mã định dạng (VD: 2D, 3D, IMAX) |
| label | String | Nhãn hiển thị                   |

## Cách Sử Dụng

### cURL

```bash
curl -X GET "http://localhost:8080/api/movies/1" \
  -H "Accept: application/json"
```

## Lưu Ý

- API chỉ trả về phim đang hoạt động (active = true)
- Phim đã bị xóa mềm sẽ trả về 404 Not Found
- Không yêu cầu xác thực (public endpoint)
- Dữ liệu genres và formats được load đầy đủ trong response
- API có xử lý ngoại lệ cho các trường hợp lỗi phổ biến
