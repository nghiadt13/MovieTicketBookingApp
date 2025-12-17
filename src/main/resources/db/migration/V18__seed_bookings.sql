-- ============================================
-- V18: Seed Bookings
-- ============================================

-- Sample bookings
INSERT INTO bookings (booking_code, user_id, showtime_id, status, total_amount, payment_method, payment_transaction_id, paid_at, expires_at) VALUES
    ('BK20241201001', 4, 1, 'CONFIRMED', 200000, 'MOMO', 'MOMO123456789', now() - INTERVAL '2 hours', now() + INTERVAL '1 day'),
    ('BK20241201002', 5, 2, 'CONFIRMED', 360000, 'VNPAY', 'VNPAY987654321', now() - INTERVAL '3 hours', now() + INTERVAL '1 day'),
    ('BK20241201003', 6, 3, 'CONFIRMED', 150000, 'ZALOPAY', 'ZALO456789123', now() - INTERVAL '1 hour', now() + INTERVAL '1 day'),
    ('BK20241201004', 7, 4, 'PENDING', 300000, NULL, NULL, NULL, now() + INTERVAL '15 minutes'),
    ('BK20241201005', 8, 5, 'CONFIRMED', 520000, 'CREDIT_CARD', 'CC789456123', now() - INTERVAL '30 minutes', now() + INTERVAL '1 day'),
    ('BK20241201006', 9, 8, 'CONFIRMED', 230000, 'MOMO', 'MOMO111222333', now() - INTERVAL '4 hours', now() + INTERVAL '1 day'),
    ('BK20241201007', 10, 10, 'CANCELLED', 300000, NULL, NULL, NULL, now() + INTERVAL '1 day'),
    ('BK20241201008', 11, 13, 'CONFIRMED', 440000, 'VNPAY', 'VNPAY444555666', now() - INTERVAL '5 hours', now() + INTERVAL '1 day'),
    ('BK20241201009', 4, 17, 'CONFIRMED', 360000, 'MOMO', 'MOMO777888999', now() - INTERVAL '6 hours', now() + INTERVAL '1 day'),
    ('BK20241201010', 5, 20, 'CONFIRMED', 190000, 'ZALOPAY', 'ZALO999888777', now() - INTERVAL '2 hours', now() + INTERVAL '1 day');

-- Booking items (seats booked)
INSERT INTO booking_items (booking_id, seat_id, price) VALUES
    (1, 25, 100000), (1, 26, 100000),
    (2, 37, 120000), (2, 38, 120000), (2, 39, 120000),
    (3, 1, 75000), (3, 2, 75000),
    (4, 50, 100000), (4, 51, 100000), (4, 52, 100000),
    (5, 145, 130000), (5, 146, 130000), (5, 147, 130000), (5, 148, 130000),
    (6, 241, 75000), (6, 242, 75000), (6, 255, 80000),
    (7, 321, 150000), (7, 322, 150000),
    (8, 401, 220000), (8, 402, 220000),
    (9, 441, 100000), (9, 442, 100000), (9, 460, 160000),
    (10, 521, 95000), (10, 522, 95000);
