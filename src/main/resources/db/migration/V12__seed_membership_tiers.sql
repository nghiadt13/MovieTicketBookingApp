-- ============================================
-- V12: Seed Membership Tiers
-- ============================================

INSERT INTO membership_tiers (name, rank_order, spending_required, discount_percent, points_multiplier, description) VALUES
    ('Member', 1, 0, 0, 1.0, 'Thành viên mới - Tích điểm 1x'),
    ('Silver', 2, 2000000, 5, 1.2, 'Thành viên Bạc - Giảm 5%, tích điểm 1.2x'),
    ('Gold', 3, 5000000, 10, 1.5, 'Thành viên Vàng - Giảm 10%, tích điểm 1.5x'),
    ('Platinum', 4, 10000000, 15, 2.0, 'Thành viên Bạch Kim - Giảm 15%, tích điểm 2x'),
    ('Diamond', 5, 20000000, 20, 2.5, 'Thành viên Kim Cương - Giảm 20%, tích điểm 2.5x')
ON CONFLICT (name) DO NOTHING;
