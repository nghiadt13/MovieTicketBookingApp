-- ============================================
-- V10: Seed Roles
-- ============================================

INSERT INTO roles (name) VALUES 
    ('ROLE_USER'),
    ('ROLE_ADMIN'),
    ('ROLE_STAFF')
ON CONFLICT (name) DO NOTHING;
