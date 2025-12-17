-- ============================================
-- V15: Seed Movies
-- ============================================

INSERT INTO movies (title, synopsis, duration_min, release_date, status, poster_url, trailer_url, rating_avg, rating_count) VALUES
    ('Avengers: Doomsday', 'Các siêu anh hùng Avengers tập hợp lại để đối đầu với mối đe dọa lớn nhất từ trước đến nay - Doctor Doom.', 180, '2026-05-01', 'COMING_SOON', 'https://example.com/avengers-doomsday.jpg', 'https://youtube.com/watch?v=avengers', 0, 0),
    ('Deadpool & Wolverine', 'Deadpool và Wolverine hợp tác trong cuộc phiêu lưu đa vũ trụ điên rồ nhất.', 127, '2024-07-26', 'NOW_SHOWING', 'https://example.com/deadpool-wolverine.jpg', 'https://youtube.com/watch?v=deadpool', 8.5, 15420),
    ('Inside Out 2', 'Riley bước vào tuổi teen với những cảm xúc mới: Lo Âu, Ghen Tị, Chán Nản và Xấu Hổ.', 96, '2024-06-14', 'NOW_SHOWING', 'https://example.com/inside-out-2.jpg', 'https://youtube.com/watch?v=insideout2', 8.8, 28500),
    ('Dune: Part Two', 'Paul Atreides hợp tác với Chani và người Fremen để trả thù những kẻ đã hủy diệt gia đình anh.', 166, '2024-03-01', 'NOW_SHOWING', 'https://example.com/dune2.jpg', 'https://youtube.com/watch?v=dune2', 8.9, 32100),
    ('Godzilla x Kong: Đế Chế Mới', 'Godzilla và Kong phải hợp tác để đối đầu với mối đe dọa từ Hollow Earth.', 115, '2024-03-29', 'NOW_SHOWING', 'https://example.com/godzilla-kong.jpg', 'https://youtube.com/watch?v=godzillakong', 7.2, 18900),
    ('Kung Fu Panda 4', 'Po phải tìm và huấn luyện Chiến binh Rồng mới trước khi trở thành Lãnh đạo Tinh thần.', 94, '2024-03-08', 'NOW_SHOWING', 'https://example.com/kungfupanda4.jpg', 'https://youtube.com/watch?v=kfp4', 7.5, 12300),
    ('Furiosa: A Mad Max Saga', 'Câu chuyện về nguồn gốc của Furiosa trước khi cô gặp Max Rockatansky.', 148, '2024-05-24', 'NOW_SHOWING', 'https://example.com/furiosa.jpg', 'https://youtube.com/watch?v=furiosa', 8.1, 9800),
    ('Venom: The Last Dance', 'Eddie Brock và Venom đối mặt với cuộc chiến cuối cùng.', 120, '2024-10-25', 'COMING_SOON', 'https://example.com/venom3.jpg', 'https://youtube.com/watch?v=venom3', 0, 0),
    ('Moana 2', 'Moana nhận được lời kêu gọi từ tổ tiên và lên đường đến vùng biển xa xôi của Châu Đại Dương.', 100, '2024-11-27', 'COMING_SOON', 'https://example.com/moana2.jpg', 'https://youtube.com/watch?v=moana2', 0, 0),
    ('Joker: Folie à Deux', 'Arthur Fleck gặp gỡ tình yêu của đời mình trong bệnh viện tâm thần Arkham.', 138, '2024-10-04', 'COMING_SOON', 'https://example.com/joker2.jpg', 'https://youtube.com/watch?v=joker2', 0, 0),
    ('Wicked', 'Câu chuyện chưa kể về các phù thủy xứ Oz - Elphaba và Glinda.', 150, '2024-11-22', 'COMING_SOON', 'https://example.com/wicked.jpg', 'https://youtube.com/watch?v=wicked', 0, 0),
    ('Gladiator II', 'Lucius, con trai của Lucilla, bước vào đấu trường La Mã.', 155, '2024-11-22', 'COMING_SOON', 'https://example.com/gladiator2.jpg', 'https://youtube.com/watch?v=gladiator2', 0, 0),
    ('Mai', 'Câu chuyện về Mai - một người phụ nữ với quá khứ đau buồn tìm kiếm hạnh phúc.', 131, '2024-02-10', 'NOW_SHOWING', 'https://example.com/mai.jpg', 'https://youtube.com/watch?v=mai', 8.3, 45000),
    ('Lật Mặt 7: Một Điều Ước', 'Phần tiếp theo của series Lật Mặt với câu chuyện gia đình cảm động.', 132, '2024-04-26', 'NOW_SHOWING', 'https://example.com/latmat7.jpg', 'https://youtube.com/watch?v=latmat7', 8.0, 38000),
    ('Cô Dâu Hào Môn', 'Câu chuyện hài hước về cô gái bình thường bước vào gia đình giàu có.', 115, '2024-06-28', 'NOW_SHOWING', 'https://example.com/codauhaumon.jpg', 'https://youtube.com/watch?v=cdhn', 7.1, 22000);

-- Link movies to genres
INSERT INTO movie_genres (movie_id, genre_id) VALUES
    (1, 1), (1, 7), -- Avengers: Hành động, Phiêu lưu
    (2, 1), (2, 3), -- Deadpool: Hành động, Hài hước
    (3, 5), (3, 9), -- Inside Out: Hoạt hình, Gia đình
    (4, 6), (4, 7), -- Dune: Khoa học viễn tưởng, Phiêu lưu
    (5, 1), (5, 6), -- Godzilla: Hành động, Khoa học viễn tưởng
    (6, 5), (6, 3), (6, 9), -- Kung Fu Panda: Hoạt hình, Hài hước, Gia đình
    (7, 1), (7, 7), -- Furiosa: Hành động, Phiêu lưu
    (8, 1), (8, 6), -- Venom: Hành động, Khoa học viễn tưởng
    (9, 5), (9, 9), (9, 13), -- Moana: Hoạt hình, Gia đình, Âm nhạc
    (10, 8), (10, 10), -- Joker: Tâm lý, Tội phạm
    (11, 13), (11, 4), -- Wicked: Âm nhạc, Tình cảm
    (12, 1), (12, 12), -- Gladiator: Hành động, Lịch sử
    (13, 4), (13, 8), -- Mai: Tình cảm, Tâm lý
    (14, 9), (14, 4), -- Lật Mặt: Gia đình, Tình cảm
    (15, 3), (15, 4); -- Cô Dâu Hào Môn: Hài hước, Tình cảm

-- Link movies to formats
INSERT INTO movie_formats (movie_id, format_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), -- Avengers: 2D, 3D, IMAX, 4DX
    (2, 1), (2, 2), (2, 3), -- Deadpool: 2D, 3D, IMAX
    (3, 1), (3, 2), -- Inside Out: 2D, 3D
    (4, 1), (4, 2), (4, 3), -- Dune: 2D, 3D, IMAX
    (5, 1), (5, 2), (5, 3), (5, 4), -- Godzilla: 2D, 3D, IMAX, 4DX
    (6, 1), (6, 2), -- Kung Fu Panda: 2D, 3D
    (7, 1), (7, 2), (7, 3), -- Furiosa: 2D, 3D, IMAX
    (8, 1), (8, 2), (8, 3), -- Venom: 2D, 3D, IMAX
    (9, 1), (9, 2), -- Moana: 2D, 3D
    (10, 1), (10, 3), -- Joker: 2D, IMAX
    (11, 1), (11, 3), -- Wicked: 2D, IMAX
    (12, 1), (12, 2), (12, 3), -- Gladiator: 2D, 3D, IMAX
    (13, 1), -- Mai: 2D
    (14, 1), -- Lật Mặt: 2D
    (15, 1); -- Cô Dâu Hào Môn: 2D
