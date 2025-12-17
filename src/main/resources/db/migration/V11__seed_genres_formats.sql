-- ============================================
-- V11: Seed Genres & Formats
-- ============================================

-- Genres
INSERT INTO genres (name, slug) VALUES
    ('Hành Động', 'hanh-dong'),
    ('Kinh Dị', 'kinh-di'),
    ('Hài Hước', 'hai-huoc'),
    ('Tình Cảm', 'tinh-cam'),
    ('Hoạt Hình', 'hoat-hinh'),
    ('Khoa Học Viễn Tưởng', 'khoa-hoc-vien-tuong'),
    ('Phiêu Lưu', 'phieu-luu'),
    ('Tâm Lý', 'tam-ly'),
    ('Gia Đình', 'gia-dinh'),
    ('Tội Phạm', 'toi-pham'),
    ('Chiến Tranh', 'chien-tranh'),
    ('Lịch Sử', 'lich-su'),
    ('Âm Nhạc', 'am-nhac'),
    ('Thể Thao', 'the-thao'),
    ('Tài Liệu', 'tai-lieu')
ON CONFLICT (name) DO NOTHING;

-- Formats
INSERT INTO formats (code, label) VALUES
    ('2D', '2D'),
    ('3D', '3D'),
    ('IMAX', 'IMAX'),
    ('4DX', '4DX'),
    ('SCREENX', 'ScreenX'),
    ('DOLBY', 'Dolby Cinema')
ON CONFLICT (code) DO NOTHING;
