-- ============================================
-- V17: Seed Showtimes & Ticket Prices
-- ============================================

-- Showtimes for today and next 7 days
-- Cinema 1 (Quốc Thanh), Screen 1-4

-- Deadpool & Wolverine (movie_id = 2)
INSERT INTO showtimes (movie_id, screen_id, start_time, end_time, status, available_seats) VALUES
    (2, 1, CURRENT_DATE + INTERVAL '9 hours', CURRENT_DATE + INTERVAL '11 hours 7 minutes', 'SELLING', 100),
    (2, 1, CURRENT_DATE + INTERVAL '13 hours', CURRENT_DATE + INTERVAL '15 hours 7 minutes', 'SELLING', 115),
    (2, 1, CURRENT_DATE + INTERVAL '17 hours', CURRENT_DATE + INTERVAL '19 hours 7 minutes', 'SELLING', 120),
    (2, 1, CURRENT_DATE + INTERVAL '21 hours', CURRENT_DATE + INTERVAL '23 hours 7 minutes', 'SELLING', 118),
    (2, 2, CURRENT_DATE + INTERVAL '10 hours', CURRENT_DATE + INTERVAL '12 hours 7 minutes', 'SELLING', 95),
    (2, 2, CURRENT_DATE + INTERVAL '14 hours', CURRENT_DATE + INTERVAL '16 hours 7 minutes', 'SELLING', 100),
    (2, 2, CURRENT_DATE + INTERVAL '18 hours', CURRENT_DATE + INTERVAL '20 hours 7 minutes', 'SELLING', 98);

-- Inside Out 2 (movie_id = 3)
INSERT INTO showtimes (movie_id, screen_id, start_time, end_time, status, available_seats) VALUES
    (3, 1, CURRENT_DATE + INTERVAL '10 hours 30 minutes', CURRENT_DATE + INTERVAL '12 hours 6 minutes', 'SELLING', 110),
    (3, 1, CURRENT_DATE + INTERVAL '14 hours 30 minutes', CURRENT_DATE + INTERVAL '16 hours 6 minutes', 'SELLING', 115),
    (3, 3, CURRENT_DATE + INTERVAL '9 hours', CURRENT_DATE + INTERVAL '10 hours 36 minutes', 'SELLING', 75),
    (3, 3, CURRENT_DATE + INTERVAL '13 hours', CURRENT_DATE + INTERVAL '14 hours 36 minutes', 'SELLING', 80),
    (3, 3, CURRENT_DATE + INTERVAL '17 hours', CURRENT_DATE + INTERVAL '18 hours 36 minutes', 'SELLING', 78);

-- Dune: Part Two (movie_id = 4)
INSERT INTO showtimes (movie_id, screen_id, start_time, end_time, status, available_seats) VALUES
    (4, 3, CURRENT_DATE + INTERVAL '11 hours', CURRENT_DATE + INTERVAL '13 hours 46 minutes', 'SELLING', 70),
    (4, 3, CURRENT_DATE + INTERVAL '15 hours', CURRENT_DATE + INTERVAL '17 hours 46 minutes', 'SELLING', 75),
    (4, 3, CURRENT_DATE + INTERVAL '19 hours', CURRENT_DATE + INTERVAL '21 hours 46 minutes', 'SELLING', 80);

-- Godzilla x Kong (movie_id = 5)
INSERT INTO showtimes (movie_id, screen_id, start_time, end_time, status, available_seats) VALUES
    (5, 4, CURRENT_DATE + INTERVAL '10 hours', CURRENT_DATE + INTERVAL '11 hours 55 minutes', 'SELLING', 55),
    (5, 4, CURRENT_DATE + INTERVAL '14 hours', CURRENT_DATE + INTERVAL '15 hours 55 minutes', 'SELLING', 58),
    (5, 4, CURRENT_DATE + INTERVAL '18 hours', CURRENT_DATE + INTERVAL '19 hours 55 minutes', 'SELLING', 60),
    (5, 4, CURRENT_DATE + INTERVAL '21 hours', CURRENT_DATE + INTERVAL '22 hours 55 minutes', 'SELLING', 60);

-- Mai (movie_id = 13)
INSERT INTO showtimes (movie_id, screen_id, start_time, end_time, status, available_seats) VALUES
    (13, 5, CURRENT_DATE + INTERVAL '9 hours 30 minutes', CURRENT_DATE + INTERVAL '11 hours 41 minutes', 'SELLING', 90),
    (13, 5, CURRENT_DATE + INTERVAL '14 hours', CURRENT_DATE + INTERVAL '16 hours 11 minutes', 'SELLING', 95),
    (13, 5, CURRENT_DATE + INTERVAL '19 hours', CURRENT_DATE + INTERVAL '21 hours 11 minutes', 'SELLING', 100);

-- Lật Mặt 7 (movie_id = 14)
INSERT INTO showtimes (movie_id, screen_id, start_time, end_time, status, available_seats) VALUES
    (14, 6, CURRENT_DATE + INTERVAL '10 hours', CURRENT_DATE + INTERVAL '12 hours 12 minutes', 'SELLING', 95),
    (14, 6, CURRENT_DATE + INTERVAL '15 hours', CURRENT_DATE + INTERVAL '17 hours 12 minutes', 'SELLING', 100),
    (14, 6, CURRENT_DATE + INTERVAL '20 hours', CURRENT_DATE + INTERVAL '22 hours 12 minutes', 'SELLING', 98);

-- Tomorrow's showtimes
INSERT INTO showtimes (movie_id, screen_id, start_time, end_time, status, available_seats) VALUES
    (2, 1, CURRENT_DATE + INTERVAL '1 day 9 hours', CURRENT_DATE + INTERVAL '1 day 11 hours 7 minutes', 'SCHEDULED', 120),
    (2, 1, CURRENT_DATE + INTERVAL '1 day 13 hours', CURRENT_DATE + INTERVAL '1 day 15 hours 7 minutes', 'SCHEDULED', 120),
    (2, 1, CURRENT_DATE + INTERVAL '1 day 17 hours', CURRENT_DATE + INTERVAL '1 day 19 hours 7 minutes', 'SCHEDULED', 120),
    (3, 3, CURRENT_DATE + INTERVAL '1 day 10 hours', CURRENT_DATE + INTERVAL '1 day 11 hours 36 minutes', 'SCHEDULED', 80),
    (3, 3, CURRENT_DATE + INTERVAL '1 day 14 hours', CURRENT_DATE + INTERVAL '1 day 15 hours 36 minutes', 'SCHEDULED', 80),
    (4, 3, CURRENT_DATE + INTERVAL '1 day 18 hours', CURRENT_DATE + INTERVAL '1 day 20 hours 46 minutes', 'SCHEDULED', 80);

-- Ticket prices for all showtimes
INSERT INTO ticket_prices (showtime_id, seat_type, price)
SELECT s.id, 'STANDARD'::seat_type_enum, 
    CASE 
        WHEN sc.screen_type = 'IMAX' THEN 150000
        WHEN sc.screen_type = '4DX' THEN 180000
        WHEN sc.screen_type = '3D' THEN 100000
        ELSE 75000
    END
FROM showtimes s
JOIN screens sc ON s.screen_id = sc.id;

INSERT INTO ticket_prices (showtime_id, seat_type, price)
SELECT s.id, 'VIP'::seat_type_enum, 
    CASE 
        WHEN sc.screen_type = 'IMAX' THEN 180000
        WHEN sc.screen_type = '4DX' THEN 220000
        WHEN sc.screen_type = '3D' THEN 130000
        ELSE 100000
    END
FROM showtimes s
JOIN screens sc ON s.screen_id = sc.id;

INSERT INTO ticket_prices (showtime_id, seat_type, price)
SELECT s.id, 'COUPLE'::seat_type_enum, 
    CASE 
        WHEN sc.screen_type = 'IMAX' THEN 350000
        WHEN sc.screen_type = '4DX' THEN 400000
        WHEN sc.screen_type = '3D' THEN 250000
        ELSE 180000
    END
FROM showtimes s
JOIN screens sc ON s.screen_id = sc.id;

INSERT INTO ticket_prices (showtime_id, seat_type, price)
SELECT s.id, 'DELUXE'::seat_type_enum, 
    CASE 
        WHEN sc.screen_type = 'IMAX' THEN 220000
        WHEN sc.screen_type = '4DX' THEN 280000
        WHEN sc.screen_type = '3D' THEN 160000
        ELSE 130000
    END
FROM showtimes s
JOIN screens sc ON s.screen_id = sc.id;
