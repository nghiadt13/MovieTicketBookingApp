-- ============================================
-- V24: Seed Movie Reviews
-- ============================================

-- Reviews cho Deadpool & Wolverine (movie_id = 2)
INSERT INTO movie_reviews (movie_id, user_id, booking_id, rating, comment_text, status, is_spoiler, helpful_count) VALUES
    (2, 4, 1, 9, 'Phim quá đỉnh! Ryan Reynolds và Hugh Jackman chemistry cực kỳ tốt. Hành động mãn nhãn, hài hước đúng chất Deadpool. Highly recommend!', 'APPROVED', false, 15),
    (2, 5, 2, 8, 'Rất hay, nhiều cảnh hành động đẹp mắt. Tuy nhiên một số joke hơi quá đà. Nhìn chung vẫn là phim Marvel đáng xem nhất năm.', 'APPROVED', false, 8),
    (2, 6, 3, 7, 'Phim giải trí tốt, nhưng cốt truyện hơi đơn giản. Wolverine xuất hiện làm fan vui lắm!', 'APPROVED', false, 5),
    (2, 8, 5, 10, 'MASTERPIECE! Đây là phim siêu anh hùng hay nhất tôi từng xem. Cảnh chiến đấu cuối phim epic quá!', 'APPROVED', true, 22);

-- Reviews cho Inside Out 2 (movie_id = 3)
INSERT INTO movie_reviews (movie_id, user_id, booking_id, rating, comment_text, status, is_spoiler, helpful_count) VALUES
    (3, 9, 6, 9, 'Pixar lại làm tôi khóc! Phim rất ý nghĩa về việc trưởng thành và đối mặt với cảm xúc mới. Anxiety được xây dựng rất hay.', 'APPROVED', false, 18);

-- Reviews cho Dune: Part Two (movie_id = 4)
INSERT INTO movie_reviews (movie_id, user_id, booking_id, rating, comment_text, status, is_spoiler, helpful_count) VALUES
    (4, 11, 8, 10, 'Tuyệt phẩm điện ảnh! Denis Villeneuve đã tạo ra một kiệt tác. Hình ảnh đẹp mê hồn, âm thanh hoành tráng. Timothée Chalamet diễn xuất thần.', 'APPROVED', false, 30);

-- Reviews cho Godzilla x Kong (movie_id = 5)
INSERT INTO movie_reviews (movie_id, user_id, booking_id, rating, comment_text, status, is_spoiler, helpful_count) VALUES
    (5, 4, 9, 7, 'Phim giải trí thuần túy, xem cho vui. Cảnh đánh nhau giữa Godzilla và Kong rất mãn nhãn. Cốt truyện thì đừng kỳ vọng nhiều.', 'APPROVED', false, 6);

-- Reviews cho Mai (movie_id = 13)
INSERT INTO movie_reviews (movie_id, user_id, booking_id, rating, comment_text, status, is_spoiler, helpful_count) VALUES
    (13, 5, 10, 8, 'Phim Việt chất lượng! Phương Anh Đào diễn xuất rất tốt. Câu chuyện cảm động, nhiều cảnh khiến tôi rơi nước mắt.', 'APPROVED', false, 12);

-- Thêm một số helpful votes
INSERT INTO review_helpful_votes (review_id, user_id) VALUES
    (1, 5), (1, 6), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13),
    (2, 4), (2, 6), (2, 7), (2, 8),
    (3, 4), (3, 5), (3, 7),
    (4, 4), (4, 5), (4, 6), (4, 7), (4, 9), (4, 10), (4, 11), (4, 12), (4, 13),
    (5, 4), (5, 6), (5, 7), (5, 8), (5, 10), (5, 11), (5, 12),
    (6, 4), (6, 5), (6, 6), (6, 7), (6, 8), (6, 9), (6, 10), (6, 12), (6, 13),
    (7, 5), (7, 6), (7, 8), (7, 9), (7, 10),
    (8, 4), (8, 6), (8, 7), (8, 8), (8, 9), (8, 10), (8, 11), (8, 12), (8, 13);

-- Update movie rating_avg và rating_count dựa trên reviews
UPDATE movies SET rating_avg = 8.5, rating_count = 4 WHERE id = 2;  -- Deadpool
UPDATE movies SET rating_avg = 9.0, rating_count = 1 WHERE id = 3;  -- Inside Out 2
UPDATE movies SET rating_avg = 10.0, rating_count = 1 WHERE id = 4; -- Dune
UPDATE movies SET rating_avg = 7.0, rating_count = 1 WHERE id = 5;  -- Godzilla
UPDATE movies SET rating_avg = 8.0, rating_count = 1 WHERE id = 13; -- Mai
