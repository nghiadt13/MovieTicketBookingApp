-- ============================================
-- V14: Seed Screens & Seats
-- ============================================

-- Screens for Cinestar Quốc Thanh (cinema_id = 1)
INSERT INTO screens (cinema_id, name, total_seats, screen_type) VALUES
    (1, 'Screen 1', 120, '2D'),
    (1, 'Screen 2', 100, '3D'),
    (1, 'Screen 3', 80, 'IMAX'),
    (1, 'Screen 4', 60, '4DX');

-- Screens for Cinestar Hai Bà Trưng (cinema_id = 2)
INSERT INTO screens (cinema_id, name, total_seats, screen_type) VALUES
    (2, 'Screen 1', 100, '2D'),
    (2, 'Screen 2', 100, '3D'),
    (2, 'Screen 3', 80, '2D');

-- Screens for other cinemas
INSERT INTO screens (cinema_id, name, total_seats, screen_type) VALUES
    (3, 'Screen 1', 80, '2D'),
    (3, 'Screen 2', 80, '3D'),
    (4, 'Screen 1', 100, '2D'),
    (4, 'Screen 2', 80, '3D'),
    (5, 'Screen 1', 80, '2D'),
    (5, 'Screen 2', 60, '3D'),
    (6, 'Screen 1', 100, '2D'),
    (6, 'Screen 2', 80, '3D'),
    (7, 'Screen 1', 100, '2D'),
    (7, 'Screen 2', 80, '3D');

-- Generate seats for Screen 1 (id=1) - 120 seats (10 rows x 12 seats)
INSERT INTO seats (screen_id, row_name, seat_number, seat_type)
SELECT 1, chr(64 + row_num), seat_num,
    CASE 
        WHEN row_num <= 2 THEN 'STANDARD'::seat_type_enum
        WHEN row_num <= 7 THEN 'VIP'::seat_type_enum
        WHEN row_num <= 9 THEN 'DELUXE'::seat_type_enum
        ELSE 'COUPLE'::seat_type_enum
    END
FROM generate_series(1, 10) AS row_num, generate_series(1, 12) AS seat_num;

-- Generate seats for Screen 2 (id=2) - 100 seats (10 rows x 10 seats)
INSERT INTO seats (screen_id, row_name, seat_number, seat_type)
SELECT 2, chr(64 + row_num), seat_num,
    CASE 
        WHEN row_num <= 2 THEN 'STANDARD'::seat_type_enum
        WHEN row_num <= 7 THEN 'VIP'::seat_type_enum
        ELSE 'DELUXE'::seat_type_enum
    END
FROM generate_series(1, 10) AS row_num, generate_series(1, 10) AS seat_num;

-- Generate seats for Screen 3 (id=3) - 80 seats (8 rows x 10 seats)
INSERT INTO seats (screen_id, row_name, seat_number, seat_type)
SELECT 3, chr(64 + row_num), seat_num,
    CASE 
        WHEN row_num <= 2 THEN 'STANDARD'::seat_type_enum
        WHEN row_num <= 6 THEN 'VIP'::seat_type_enum
        ELSE 'DELUXE'::seat_type_enum
    END
FROM generate_series(1, 8) AS row_num, generate_series(1, 10) AS seat_num;

-- Generate seats for Screen 4 (id=4) - 60 seats (6 rows x 10 seats)
INSERT INTO seats (screen_id, row_name, seat_number, seat_type)
SELECT 4, chr(64 + row_num), seat_num,
    CASE 
        WHEN row_num <= 2 THEN 'STANDARD'::seat_type_enum
        WHEN row_num <= 5 THEN 'VIP'::seat_type_enum
        ELSE 'COUPLE'::seat_type_enum
    END
FROM generate_series(1, 6) AS row_num, generate_series(1, 10) AS seat_num;

-- Generate seats for remaining screens (simplified - 80 seats each)
INSERT INTO seats (screen_id, row_name, seat_number, seat_type)
SELECT screen_id, chr(64 + row_num), seat_num,
    CASE 
        WHEN row_num <= 2 THEN 'STANDARD'::seat_type_enum
        WHEN row_num <= 6 THEN 'VIP'::seat_type_enum
        ELSE 'DELUXE'::seat_type_enum
    END
FROM generate_series(5, 17) AS screen_id, generate_series(1, 8) AS row_num, generate_series(1, 10) AS seat_num;
