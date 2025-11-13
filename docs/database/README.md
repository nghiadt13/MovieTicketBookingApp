# Database Documentation

## Tổng Quan

Hệ thống Movie Booking sử dụng PostgreSQL với schema được thiết kế để quản lý:

- Người dùng và xác thực (Users & Authentication)
- Danh mục phim (Movie Catalog)
- Rạp chiếu và phòng chiếu (Cinemas & Screens)
- Lịch chiếu và đặt vé (Showtimes & Bookings)
- Hệ thống thành viên (Membership System)
- Quản lý nội dung (Content Management)

## Cấu Trúc Tài Liệu

- [01-overview.md](01-overview.md) - Tổng quan về database
- [02-user-management.md](02-user-management.md) - Module quản lý người dùng
- [03-movie-catalog.md](03-movie-catalog.md) - Module danh mục phim
- [04-cinema-screening.md](04-cinema-screening.md) - Module rạp và phòng chiếu
- [05-booking-payment.md](05-booking-payment.md) - Module đặt vé và thanh toán
- [06-membership.md](06-membership.md) - Module hệ thống thành viên
- [07-content-management.md](07-content-management.md) - Module quản lý nội dung
- [08-relationships.md](08-relationships.md) - Sơ đồ quan hệ giữa các bảng

## File SQL

File `Project.sql` trong thư mục `src/main/resources/sql/migration/` chứa toàn bộ schema.

## Quick Start

```bash
# Chạy schema trên database mới
psql -U postgres -d MobileApp -f src/main/resources/sql/migration/Project.sql
```
