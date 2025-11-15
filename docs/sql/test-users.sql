-- Test Users với BCrypt Password Hash
-- Password cho tất cả users: "password123"
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- Xóa test users cũ nếu có (optional)
-- DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE email LIKE '%test%');
-- DELETE FROM users WHERE email LIKE '%test%';

-- 1. User với email và phone (đã verify cả 2)
INSERT INTO
    users (
        email,
        phone_number,
        password_hash,
        display_name,
        avatar_url,
        is_active,
        email_verified_at,
        phone_number_verified_at,
        created_at,
        updated_at
    )
VALUES (
        'test1@example.com',
        '0901234567',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Test User 1',
        'https://i.pravatar.cc/150?img=1',
        true,
        NOW(),
        NOW(),
        NOW(),
        NOW()
    );

-- 2. User chỉ có email (chưa verify phone)
INSERT INTO
    users (
        email,
        phone_number,
        password_hash,
        display_name,
        avatar_url,
        is_active,
        email_verified_at,
        phone_number_verified_at,
        created_at,
        updated_at
    )
VALUES (
        'test2@example.com',
        '0902345678',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Test User 2',
        'https://i.pravatar.cc/150?img=2',
        true,
        NOW(),
        NULL,
        NOW(),
        NOW()
    );

-- 3. User chỉ có phone (không có email)
INSERT INTO
    users (
        email,
        phone_number,
        password_hash,
        display_name,
        avatar_url,
        is_active,
        email_verified_at,
        phone_number_verified_at,
        created_at,
        updated_at
    )
VALUES (
        NULL,
        '0903456789',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Test User 3',
        'https://i.pravatar.cc/150?img=3',
        true,
        NULL,
        NOW(),
        NOW(),
        NOW()
    );

-- 4. User inactive (để test login fail)
INSERT INTO
    users (
        email,
        phone_number,
        password_hash,
        display_name,
        avatar_url,
        is_active,
        email_verified_at,
        phone_number_verified_at,
        created_at,
        updated_at
    )
VALUES (
        'inactive@example.com',
        '0904567890',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Inactive User',
        NULL,
        false,
        NOW(),
        NOW(),
        NOW(),
        NOW()
    );

-- 5. Admin user (có nhiều roles)
INSERT INTO
    users (
        email,
        phone_number,
        password_hash,
        display_name,
        avatar_url,
        is_active,
        email_verified_at,
        phone_number_verified_at,
        created_at,
        updated_at
    )
VALUES (
        'admin@example.com',
        '0905678901',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Admin User',
        'https://i.pravatar.cc/150?img=5',
        true,
        NOW(),
        NOW(),
        NOW(),
        NOW()
    );

-- Gán roles cho users (cần có roles table trước)
-- Giả sử roles table đã có: ROLE_USER (id=1), ROLE_ADMIN (id=2), ROLE_STAFF (id=3)

-- Nếu chưa có roles, tạo trước:
INSERT INTO
    roles (name)
VALUES ('ROLE_USER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO
    roles (name)
VALUES ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO
    roles (name)
VALUES ('ROLE_STAFF')
ON CONFLICT (name) DO NOTHING;

-- Gán ROLE_USER cho tất cả test users
INSERT INTO
    user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
    CROSS JOIN roles r
WHERE
    u.email IN (
        'test1@example.com',
        'test2@example.com',
        'inactive@example.com',
        'admin@example.com'
    )
    AND r.name = 'ROLE_USER'
ON CONFLICT DO NOTHING;

-- Gán ROLE_USER cho user chỉ có phone
INSERT INTO
    user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
    CROSS JOIN roles r
WHERE
    u.phone_number = '0903456789'
    AND r.name = 'ROLE_USER'
ON CONFLICT DO NOTHING;

-- Gán ROLE_ADMIN cho admin user
INSERT INTO
    user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
    CROSS JOIN roles r
WHERE
    u.email = 'admin@example.com'
    AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- Verify data
SELECT
    u.id,
    u.email,
    u.phone_number,
    u.display_name,
    u.is_active,
    u.email_verified_at IS NOT NULL as email_verified,
    u.phone_number_verified_at IS NOT NULL as phone_verified,
    array_agg(r.name) as roles
FROM
    users u
    LEFT JOIN user_roles ur ON u.id = ur.user_id
    LEFT JOIN roles r ON ur.role_id = r.id
WHERE
    u.email LIKE '%test%'
    OR u.email = 'admin@example.com'
    OR u.phone_number = '0903456789'
    OR u.email = 'inactive@example.com'
GROUP BY
    u.id,
    u.email,
    u.phone_number,
    u.display_name,
    u.is_active,
    u.email_verified_at,
    u.phone_number_verified_at
ORDER BY u.id;