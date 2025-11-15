-- Migration script for news table
-- Create news table to store news articles

CREATE TABLE IF NOT EXISTS news (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    image_url VARCHAR(500),
    author VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on updated_at for faster sorting
CREATE INDEX idx_news_updated_at ON news (updated_at DESC);

-- Create index on is_active for filtering
CREATE INDEX idx_news_is_active ON news (is_active);

-- Sample data for testing
INSERT INTO
    news (
        title,
        content,
        image_url,
        author,
        is_active,
        created_at,
        updated_at
    )
VALUES (
        'Breaking: New Movie Release',
        'Exciting new movie coming to theaters this weekend...',
        'https://example.com/news1.jpg',
        'John Doe',
        true,
        NOW() - INTERVAL '10 days',
        NOW() - INTERVAL '1 day'
    ),
    (
        'Cinema Reopening Announcement',
        'Our cinema is reopening with new safety measures...',
        'https://example.com/news2.jpg',
        'Jane Smith',
        true,
        NOW() - INTERVAL '9 days',
        NOW() - INTERVAL '2 days'
    ),
    (
        'Special Discount This Week',
        'Get 50% off on all tickets this week...',
        'https://example.com/news3.jpg',
        'Admin',
        true,
        NOW() - INTERVAL '8 days',
        NOW() - INTERVAL '3 days'
    ),
    (
        'New IMAX Screen Installed',
        'Experience movies like never before with our new IMAX screen...',
        'https://example.com/news4.jpg',
        'Tech Team',
        true,
        NOW() - INTERVAL '7 days',
        NOW() - INTERVAL '4 days'
    ),
    (
        'Food Menu Update',
        'Check out our new food and beverage menu...',
        'https://example.com/news5.jpg',
        'Manager',
        true,
        NOW() - INTERVAL '6 days',
        NOW() - INTERVAL '5 days'
    ),
    (
        'VIP Lounge Opening',
        'Introducing our premium VIP lounge experience...',
        'https://example.com/news6.jpg',
        'Marketing',
        true,
        NOW() - INTERVAL '5 days',
        NOW() - INTERVAL '6 days'
    ),
    (
        'Student Discount Program',
        'Students get special rates every Tuesday...',
        'https://example.com/news7.jpg',
        'Admin',
        true,
        NOW() - INTERVAL '4 days',
        NOW() - INTERVAL '7 days'
    ),
    (
        'Mobile App Launch',
        'Download our new mobile app for easy booking...',
        'https://example.com/news8.jpg',
        'Dev Team',
        true,
        NOW() - INTERVAL '3 days',
        NOW() - INTERVAL '8 days'
    ),
    (
        'Holiday Schedule',
        'Check our special holiday screening schedule...',
        'https://example.com/news9.jpg',
        'Manager',
        true,
        NOW() - INTERVAL '2 days',
        NOW() - INTERVAL '9 days'
    ),
    (
        'Loyalty Program',
        'Join our loyalty program and earn rewards...',
        'https://example.com/news10.jpg',
        'Marketing',
        true,
        NOW() - INTERVAL '1 day',
        NOW() - INTERVAL '10 days'
    ),
    (
        'Behind the Scenes',
        'Go behind the scenes of our latest blockbuster...',
        'https://example.com/news11.jpg',
        'Content Team',
        true,
        NOW() - INTERVAL '11 days',
        NOW()
    ),
    (
        'Director Interview',
        'Exclusive interview with renowned director...',
        'https://example.com/news12.jpg',
        'Editor',
        true,
        NOW() - INTERVAL '12 days',
        NOW() - INTERVAL '11 days'
    );

-- Note: The query will return the 10 most recently updated news items
-- based on the updated_at column in descending order