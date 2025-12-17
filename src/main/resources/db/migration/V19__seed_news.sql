-- ============================================
-- V19: Seed News
-- ============================================

INSERT INTO news (title, content, image_url, published_at) VALUES
    ('Deadpool & Wolverine phá kỷ lục phòng vé Việt Nam', 
     'Bộ phim Deadpool & Wolverine đã chính thức phá vỡ kỷ lục phòng vé tại Việt Nam với doanh thu mở màn đạt 50 tỷ đồng chỉ trong 3 ngày đầu công chiếu. Đây là thành tích ấn tượng nhất của một bộ phim Marvel tại thị trường Việt Nam.

Phim quy tụ hai ngôi sao Ryan Reynolds và Hugh Jackman trong vai Deadpool và Wolverine, mang đến những pha hành động mãn nhãn và những câu thoại hài hước đặc trưng.

Cinestar hiện đang chiếu phim tại tất cả các cụm rạp trên toàn quốc với nhiều suất chiếu đặc biệt IMAX và 4DX.',
     'https://example.com/news/deadpool-record.jpg',
     now() - INTERVAL '2 days'),

    ('Khuyến mãi tháng 12: Mua 2 tặng 1 cho thành viên Gold trở lên',
     'Nhân dịp cuối năm, Cinestar triển khai chương trình khuyến mãi đặc biệt dành cho các thành viên hạng Gold, Platinum và Diamond.

Từ ngày 01/12 đến 31/12/2024, khi mua 2 vé xem phim bất kỳ, bạn sẽ được tặng 1 vé miễn phí cho suất chiếu tiếp theo.

Điều kiện áp dụng:
- Áp dụng cho thành viên Gold, Platinum, Diamond
- Vé tặng có giá trị trong vòng 7 ngày
- Không áp dụng cho suất chiếu đặc biệt và ngày lễ',
     'https://example.com/news/promo-dec.jpg',
     now() - INTERVAL '5 days'),

    ('Inside Out 2 - Bộ phim hoạt hình hay nhất năm 2024',
     'Inside Out 2 tiếp tục câu chuyện về Riley khi cô bé bước vào tuổi teen với những cảm xúc mới: Lo Âu, Ghen Tị, Chán Nản và Xấu Hổ.

Phim đã nhận được đánh giá tích cực từ giới phê bình với điểm số 91% trên Rotten Tomatoes và 8.8/10 trên IMDb.

Đặc biệt, phim có phiên bản lồng tiếng Việt với sự tham gia của nhiều nghệ sĩ nổi tiếng, phù hợp cho cả gia đình thưởng thức.',
     'https://example.com/news/inside-out-2.jpg',
     now() - INTERVAL '7 days'),

    ('Cinestar khai trương rạp mới tại Đà Lạt',
     'Cinestar chính thức khai trương cụm rạp chiếu phim hiện đại tại thành phố Đà Lạt, đánh dấu bước mở rộng quan trọng tại khu vực Tây Nguyên.

Cụm rạp mới có 2 phòng chiếu với công nghệ âm thanh Dolby 7.1 và màn hình 4K, mang đến trải nghiệm xem phim chất lượng cao cho khán giả Đà Lạt.

Nhân dịp khai trương, Cinestar Đà Lạt áp dụng giá vé ưu đãi 50% cho tất cả các suất chiếu trong tuần đầu tiên.',
     'https://example.com/news/dalat-opening.jpg',
     now() - INTERVAL '10 days'),

    ('Lịch chiếu phim Tết Nguyên Đán 2025',
     'Cinestar công bố lịch chiếu phim Tết Nguyên Đán 2025 với nhiều bom tấn được mong đợi:

1. Avengers: Doomsday - Khởi chiếu mùng 1 Tết
2. Lật Mặt 8 - Khởi chiếu mùng 1 Tết
3. Trạng Tí 2 - Khởi chiếu mùng 2 Tết

Vé sẽ được mở bán từ ngày 15/01/2025. Thành viên Cinestar được ưu tiên đặt vé trước 24 giờ.',
     'https://example.com/news/tet-2025.jpg',
     now() - INTERVAL '1 day'),

    ('Hướng dẫn đặt vé online trên app Cinestar',
     'Để đặt vé xem phim trên ứng dụng Cinestar, bạn thực hiện theo các bước sau:

Bước 1: Tải ứng dụng Cinestar từ App Store hoặc Google Play
Bước 2: Đăng ký tài khoản hoặc đăng nhập
Bước 3: Chọn phim và suất chiếu
Bước 4: Chọn ghế ngồi
Bước 5: Thanh toán qua MoMo, VNPay hoặc ZaloPay
Bước 6: Nhận mã QR vé điện tử

Lưu ý: Vé đã mua không được đổi hoặc hoàn tiền.',
     'https://example.com/news/booking-guide.jpg',
     now() - INTERVAL '14 days'),

    ('Review phim Mai - Tác phẩm xuất sắc của Trấn Thành',
     'Mai là bộ phim điện ảnh thứ hai của đạo diễn Trấn Thành, kể về câu chuyện của Mai - một người phụ nữ với quá khứ đau buồn đang tìm kiếm hạnh phúc.

Phim có sự tham gia của Phương Anh Đào, Tuấn Trần, NSND Việt Anh và nhiều nghệ sĩ nổi tiếng khác.

Với doanh thu vượt 500 tỷ đồng, Mai trở thành phim Việt có doanh thu cao nhất mọi thời đại.',
     'https://example.com/news/mai-review.jpg',
     now() - INTERVAL '30 days'),

    ('Cinestar hợp tác với MoMo - Giảm 20% khi thanh toán',
     'Cinestar chính thức hợp tác với ví điện tử MoMo, mang đến ưu đãi giảm 20% cho tất cả giao dịch mua vé xem phim.

Chương trình áp dụng từ nay đến hết 31/12/2024 cho tất cả người dùng MoMo.

Cách thức tham gia:
- Mở ứng dụng MoMo
- Tìm kiếm "Cinestar"
- Chọn phim và thanh toán
- Mã giảm giá sẽ được áp dụng tự động',
     'https://example.com/news/momo-partnership.jpg',
     now() - INTERVAL '3 days');
