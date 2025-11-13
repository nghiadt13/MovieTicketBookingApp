# Database Relationships

## Complete Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        USER MANAGEMENT                           │
└─────────────────────────────────────────────────────────────────┘

users ──┬── user_roles ── roles
        ├── social_accounts
        ├── user_otps
        ├── bookings
        ├── seat_locks
        └── user_memberships ── membership_tiers


┌─────────────────────────────────────────────────────────────────┐
│                        MOVIE CATALOG                             │
└─────────────────────────────────────────────────────────────────┘

movies ──┬── movie_genres ── genres
         ├── movie_formats ── formats
         └── showtimes


┌─────────────────────────────────────────────────────────────────┐
│                    CINEMA & SCREENING                            │
└─────────────────────────────────────────────────────────────────┘

cinemas ── screens ──┬── seats ──┬── booking_items
                     │           └── seat_locks
                     └── showtimes


┌─────────────────────────────────────────────────────────────────┐
│                    BOOKING & PAYMENT                             │
└─────────────────────────────────────────────────────────────────┘

showtimes ──┬── ticket_prices
            ├── bookings ── booking_items ── seats
            └── seat_locks


┌─────────────────────────────────────────────────────────────────┐
│                    CONTENT MANAGEMENT                            │
└─────────────────────────────────────────────────────────────────┘

news (standalone)
carousel_items (standalone)
```

---

## Detailed Relationships

### One-to-Many (1:N)

| Parent           | Child            | Relationship                                 |
| ---------------- | ---------------- | -------------------------------------------- |
| users            | user_roles       | 1 user có nhiều roles                        |
| users            | social_accounts  | 1 user có nhiều social accounts              |
| users            | user_otps        | 1 user có nhiều OTPs                         |
| users            | bookings         | 1 user có nhiều bookings                     |
| users            | seat_locks       | 1 user có nhiều seat locks                   |
| roles            | user_roles       | 1 role có nhiều users                        |
| cinemas          | screens          | 1 rạp có nhiều phòng                         |
| screens          | seats            | 1 phòng có nhiều ghế                         |
| screens          | showtimes        | 1 phòng có nhiều suất chiếu                  |
| movies           | showtimes        | 1 phim có nhiều suất chiếu                   |
| showtimes        | ticket_prices    | 1 suất chiếu có nhiều giá vé (theo loại ghế) |
| showtimes        | bookings         | 1 suất chiếu có nhiều bookings               |
| showtimes        | seat_locks       | 1 suất chiếu có nhiều seat locks             |
| bookings         | booking_items    | 1 booking có nhiều items (ghế)               |
| seats            | booking_items    | 1 ghế có nhiều booking items (qua thời gian) |
| membership_tiers | user_memberships | 1 tier có nhiều users                        |

### One-to-One (1:1)

| Table 1 | Table 2          | Relationship           |
| ------- | ---------------- | ---------------------- |
| users   | user_memberships | 1 user có 1 membership |

### Many-to-Many (N:M)

| Table 1 | Junction Table | Table 2 | Relationship     |
| ------- | -------------- | ------- | ---------------- |
| users   | user_roles     | roles   | Users ↔ Roles    |
| movies  | movie_genres   | genres  | Movies ↔ Genres  |
| movies  | movie_formats  | formats | Movies ↔ Formats |

---

## Foreign Key Constraints

### CASCADE (Xóa parent → xóa children)

```
users → user_roles
users → social_accounts
users → user_otps
users → seat_locks
cinemas → screens
screens → seats
screens → showtimes (via screen_id)
movies → movie_genres
movies → movie_formats
showtimes → ticket_prices
showtimes → seat_locks
bookings → booking_items
```

### RESTRICT (Không cho xóa parent nếu còn children)

```
roles → user_roles
genres → movie_genres
formats → movie_formats
movies → showtimes
users → bookings
showtimes → bookings
seats → booking_items
membership_tiers → user_memberships
```

---

## Circular Dependencies

### Booking Flow

```
users → bookings → booking_items → seats
  ↓                                   ↑
seat_locks ────────────────────────────┘
```

**Giải pháp:**

- seat_locks là temporary, tự động expire
- booking_items chỉ tạo sau khi payment success

---

## Query Patterns

### Get User với Full Info

```sql
SELECT u.*,
       array_agg(DISTINCT r.name) as roles,
       um.tier_id, mt.name as tier_name
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
LEFT JOIN user_memberships um ON u.id = um.user_id
LEFT JOIN membership_tiers mt ON um.tier_id = mt.id
WHERE u.id = ?
GROUP BY u.id, um.tier_id, mt.name;
```

### Get Movie với Genres, Formats, Showtimes

```sql
SELECT m.*,
       array_agg(DISTINCT g.name) as genres,
       array_agg(DISTINCT f.label) as formats,
       COUNT(DISTINCT s.id) as showtime_count
FROM movies m
LEFT JOIN movie_genres mg ON m.id = mg.movie_id
LEFT JOIN genres g ON mg.genre_id = g.id
LEFT JOIN movie_formats mf ON m.id = mf.movie_id
LEFT JOIN formats f ON mf.format_id = f.id
LEFT JOIN showtimes s ON m.id = s.movie_id AND s.is_active = TRUE
WHERE m.id = ?
GROUP BY m.id;
```

### Get Booking với Full Details

```sql
SELECT b.*,
       u.display_name as user_name,
       m.title as movie_title,
       c.name as cinema_name,
       sc.name as screen_name,
       s.start_time,
       array_agg(CONCAT(st.row_name, st.seat_number)) as seats
FROM bookings b
JOIN users u ON b.user_id = u.id
JOIN showtimes s ON b.showtime_id = s.id
JOIN movies m ON s.movie_id = m.id
JOIN screens sc ON s.screen_id = sc.id
JOIN cinemas c ON sc.cinema_id = c.id
JOIN booking_items bi ON b.id = bi.booking_id
JOIN seats st ON bi.seat_id = st.id
WHERE b.id = ?
GROUP BY b.id, u.display_name, m.title, c.name, sc.name, s.start_time;
```

---

## Indexes Summary

### User Module

- user_roles(role_id)
- social_accounts(user_id)
- user_otps(user_id, purpose) WHERE consumed_at IS NULL

### Movie Module

- movies(status)
- movies(is_active) WHERE is_active = TRUE
- movie_genres(genre_id)
- movie_formats(format_id)

### Booking Module

- showtimes(movie_id, start_time)
- showtimes(screen_id, start_time)
- showtimes(status) WHERE is_active = TRUE
- bookings(user_id)
- bookings(status)
- bookings(created_at DESC)
- booking_items(seat_id)
- seat_locks(locked_until)
