-- ============================================
-- V16: Seed Users
-- ============================================
-- Password: 123456 (BCrypt hash)

INSERT INTO users (email, phone_number, password_hash, display_name, avatar_url, is_active, email_verified_at) VALUES
    ('admin@cinestar.com', '0901234567', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Admin Cinestar', 'https://example.com/avatars/admin.jpg', true, now()),
    ('staff1@cinestar.com', '0901234568', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Nhân viên Quốc Thanh', 'https://example.com/avatars/staff1.jpg', true, now()),
    ('staff2@cinestar.com', '0901234569', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Nhân viên Hai Bà Trưng', 'https://example.com/avatars/staff2.jpg', true, now()),
    ('nguyenvana@gmail.com', '0912345678', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Nguyễn Văn A', 'https://example.com/avatars/user1.jpg', true, now()),
    ('tranthib@gmail.com', '0912345679', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Trần Thị B', 'https://example.com/avatars/user2.jpg', true, now()),
    ('levanc@gmail.com', '0912345680', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Lê Văn C', 'https://example.com/avatars/user3.jpg', true, now()),
    ('phamthid@gmail.com', '0912345681', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Phạm Thị D', 'https://example.com/avatars/user4.jpg', true, now()),
    ('hoangvane@gmail.com', '0912345682', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Hoàng Văn E', 'https://example.com/avatars/user5.jpg', true, now()),
    ('vuthif@gmail.com', '0912345683', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Vũ Thị F', 'https://example.com/avatars/user6.jpg', true, now()),
    ('dangvang@gmail.com', '0912345684', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Đặng Văn G', 'https://example.com/avatars/user7.jpg', true, now()),
    ('buithih@gmail.com', '0912345685', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Bùi Thị H', 'https://example.com/avatars/user8.jpg', true, now()),
    ('dovani@gmail.com', '0912345686', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Đỗ Văn I', 'https://example.com/avatars/user9.jpg', true, now()),
    ('ngothik@gmail.com', '0912345687', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQb9tLGjPsZf5/54Yd2.Wd5OQARS', 'Ngô Thị K', 'https://example.com/avatars/user10.jpg', true, now());

-- Assign roles
INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 2), -- Admin -> ROLE_ADMIN
    (2, 3), -- Staff1 -> ROLE_STAFF
    (3, 3), -- Staff2 -> ROLE_STAFF
    (4, 1), -- User -> ROLE_USER
    (5, 1),
    (6, 1),
    (7, 1),
    (8, 1),
    (9, 1),
    (10, 1),
    (11, 1),
    (12, 1),
    (13, 1);

-- Assign memberships
INSERT INTO user_memberships (user_id, tier_id, total_spending, points) VALUES
    (4, 3, 5500000, 5500),   -- Gold
    (5, 4, 12000000, 14400), -- Platinum
    (6, 2, 2500000, 3000),   -- Silver
    (7, 1, 500000, 500),     -- Member
    (8, 5, 25000000, 62500), -- Diamond
    (9, 2, 3000000, 3600),   -- Silver
    (10, 1, 800000, 800),    -- Member
    (11, 3, 7000000, 10500), -- Gold
    (12, 1, 100000, 100),    -- Member
    (13, 2, 4000000, 4800);  -- Silver
