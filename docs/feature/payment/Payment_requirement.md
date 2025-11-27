# PAYMENT FEATURE - BACKEND IMPLEMENTATION REQUIREMENTS

## 1. DATABASE SCHEMA

### 1.1. Combos Table
Stores popcorn & drink combo items available for purchase.

```sql
CREATE TABLE combos (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    image_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_combos_is_active ON combos(is_active);
```

### 1.2. Cinema Combos Table (Optional - for cinema-specific combos)
Links combos to specific cinemas if needed.

```sql
CREATE TABLE cinema_combos (
    id BIGSERIAL PRIMARY KEY,
    cinema_id BIGINT NOT NULL REFERENCES cinemas(id) ON DELETE CASCADE,
    combo_id BIGINT NOT NULL REFERENCES combos(id) ON DELETE CASCADE,
    is_available BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(cinema_id, combo_id)
);

CREATE INDEX idx_cinema_combos_cinema_id ON cinema_combos(cinema_id);
```

### 1.3. Payment Methods Table
Stores available payment methods.

```sql
CREATE TYPE payment_icon_type AS ENUM ('VIETQR', 'MOMO', 'ZALOPAY', 'VNPAY', 'CREDIT_CARD');

CREATE TABLE payment_methods (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    icon_type payment_icon_type NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payment_methods_is_active ON payment_methods(is_active);
```

### 1.4. Payments Table
Stores payment transactions.

```sql
CREATE TYPE payment_status AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED');

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(100) UNIQUE NOT NULL,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE RESTRICT,
    payment_method_id VARCHAR(50) NOT NULL REFERENCES payment_methods(id),

    -- Price breakdown
    ticket_price DECIMAL(10, 2) NOT NULL CHECK (ticket_price >= 0),
    ticket_count INT NOT NULL CHECK (ticket_count >= 1),
    combo_price DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (combo_price >= 0),
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (discount_amount >= 0),
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount > 0),

    -- Discount info
    discount_code VARCHAR(50),

    -- Payment gateway info
    qr_code_url VARCHAR(500),
    gateway_transaction_id VARCHAR(100),
    gateway_response TEXT,

    -- Status tracking
    status payment_status NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMPTZ,
    failed_at TIMESTAMPTZ,
    failure_reason VARCHAR(500),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payments_booking_id ON payments(booking_id);
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);
```

### 1.5. Payment Combos Table
Stores combos selected for each payment.

```sql
CREATE TABLE payment_combos (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    combo_id BIGINT NOT NULL REFERENCES combos(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price > 0),
    subtotal DECIMAL(10, 2) NOT NULL CHECK (subtotal > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payment_combos_payment_id ON payment_combos(payment_id);
```

### 1.6. Discount Codes Table (Optional - for discount functionality)

```sql
CREATE TYPE discount_type AS ENUM ('PERCENTAGE', 'FIXED_AMOUNT');

CREATE TABLE discount_codes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    discount_type discount_type NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL CHECK (discount_value > 0),
    min_order_amount DECIMAL(10, 2) DEFAULT 0,
    max_discount_amount DECIMAL(10, 2),
    usage_limit INT,
    used_count INT NOT NULL DEFAULT 0,
    valid_from TIMESTAMPTZ NOT NULL,
    valid_until TIMESTAMPTZ NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_discount_codes_code ON discount_codes(code);
CREATE INDEX idx_discount_codes_valid ON discount_codes(valid_from, valid_until);
```

---

## 2. ENTITY-RELATIONSHIP DIAGRAM

```
+---------------+       +-------------------+       +------------------+
|   bookings    |       |     payments      |       | payment_methods  |
+---------------+       +-------------------+       +------------------+
| id (PK)       |<----->| booking_id (FK)   |       | id (PK)          |
| booking_code  |       | payment_method_id |------>| name             |
| user_id       |       | transaction_id    |       | description      |
| showtime_id   |       | ticket_price      |       | icon_type        |
| status        |       | combo_price       |       | is_active        |
| total_amount  |       | discount_amount   |       +------------------+
| ...           |       | total_amount      |
+---------------+       | status            |       +------------------+
                        | qr_code_url       |       |     combos       |
                        +-------------------+       +------------------+
                              |                     | id (PK)          |
                              |                     | name             |
                              v                     | description      |
                        +-------------------+       | price            |
                        | payment_combos    |       | image_url        |
                        +-------------------+       | is_active        |
                        | payment_id (FK)   |       +------------------+
                        | combo_id (FK)     |-------------->|
                        | quantity          |
                        | unit_price        |
                        | subtotal          |
                        +-------------------+
```

---

## 3. API ENDPOINTS SPECIFICATION

### 3.1. GET /api/v1/bookings/{bookingId}
Get booking information for payment screen.

**Controller:** `PaymentController`
**Service Method:** `getBookingInfoForPayment(bookingId: Long)`

### 3.2. GET /api/v1/combos
Get all available combos.

**Controller:** `ComboController` or `PaymentController`
**Service Method:** `getAllActiveCombos()`

### 3.3. GET /api/v1/cinemas/{cinemaId}/combos
Get combos available at a specific cinema.

**Controller:** `ComboController`
**Service Method:** `getCombosByCinema(cinemaId: Long)`

### 3.4. GET /api/v1/payment-methods
Get all available payment methods.

**Controller:** `PaymentController`
**Service Method:** `getAllActivePaymentMethods()`

### 3.5. POST /api/v1/payments/calculate
Calculate total price.

**Controller:** `PaymentController`
**Service Method:** `calculatePayment(request: CalculatePaymentRequest)`

### 3.6. POST /api/v1/payments
Create a payment transaction.

**Controller:** `PaymentController`
**Service Method:** `createPayment(request: CreatePaymentRequest)`

---

## 4. DTO SPECIFICATIONS

### 4.1. BookingInfoDto (Response)
```kotlin
data class BookingInfoDto(
    val movieTitle: String,
    val moviePosterUrl: String?,
    val genre: String,
    val format: String,
    val duration: String,
    val cinemaName: String,
    val showtime: String,
    val showdate: String,
    val seats: List<String>,
    val room: String,
    val ticketPrice: Long,
    val ticketCount: Int
)
```

### 4.2. ComboDto (Response)
```kotlin
data class ComboDto(
    val id: String,
    val name: String,
    val description: String?,
    val price: Long,
    val imageUrl: String?
)
```

### 4.3. PaymentMethodDto (Response)
```kotlin
data class PaymentMethodDto(
    val id: String,
    val name: String,
    val description: String?,
    val iconType: String
)
```

### 4.4. CreatePaymentRequest
```kotlin
data class CreatePaymentRequest(
    val bookingId: Long,
    val paymentMethodId: String,
    val combos: List<ComboSelectionDto>,
    val discountCode: String?
)

data class ComboSelectionDto(
    val comboId: Long,
    val quantity: Int
)
```

### 4.5. CalculatePaymentRequest
```kotlin
data class CalculatePaymentRequest(
    val bookingId: Long,
    val combos: List<ComboSelectionDto>,
    val discountCode: String?
)
```

### 4.6. PaymentSummaryDto (Response)
```kotlin
data class PaymentSummaryDto(
    val ticketPrice: Long,
    val ticketCount: Int,
    val comboPrice: Long,
    val discount: Long,
    val totalPrice: Long
)
```

### 4.7. PaymentResponseDto (Response)
```kotlin
data class PaymentResponseDto(
    val success: Boolean,
    val transactionId: String?,
    val qrCodeUrl: String?,
    val message: String
)
```

---

## 5. ERROR CODES

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `BOOKING_NOT_FOUND` | 404 | Booking not found |
| `BOOKING_EXPIRED` | 400 | Booking has expired |
| `BOOKING_ALREADY_PAID` | 400 | Booking already paid |
| `PAYMENT_METHOD_INVALID` | 400 | Invalid payment method |
| `PAYMENT_METHOD_NOT_FOUND` | 404 | Payment method not found |
| `COMBO_NOT_AVAILABLE` | 400 | Combo not available |
| `COMBO_NOT_FOUND` | 404 | Combo not found |
| `INVALID_DISCOUNT_CODE` | 400 | Invalid discount code |
| `DISCOUNT_EXPIRED` | 400 | Discount code expired |
| `DISCOUNT_USAGE_LIMIT` | 400 | Discount usage limit reached |
| `MIN_ORDER_NOT_MET` | 400 | Minimum order amount not met |
| `PAYMENT_FAILED` | 500 | Payment processing failed |

---

## 6. BUSINESS RULES

1. **Booking Validation:**
   - Booking must exist and be in `PENDING` or `CONFIRMED` status
   - Booking must not be expired (check `expires_at`)
   - Booking must not have been paid already

2. **Combo Validation:**
   - Combo must exist and be active
   - Quantity must be > 0

3. **Payment Method Validation:**
   - Payment method must exist and be active

4. **Discount Code Validation:**
   - Code must exist and be active
   - Must be within valid date range
   - Usage count must not exceed limit
   - Order amount must meet minimum requirement

5. **Price Calculation:**
   ```
   ticketTotal = ticketPrice * ticketCount
   comboTotal = SUM(combo.price * combo.quantity)
   discount = calculateDiscount(discountCode, ticketTotal + comboTotal)
   totalAmount = ticketTotal + comboTotal - discount
   ```

---

## 7. IMPLEMENTATION CHECKLIST

- [ ] Create Flyway migration V10__create_payment_tables.sql
- [ ] Create `Combo` entity
- [ ] Create `PaymentMethod` entity
- [ ] Create `Payment` entity
- [ ] Create `PaymentCombo` entity
- [ ] Create `DiscountCode` entity (optional)
- [ ] Create repositories for all entities
- [ ] Create DTOs and mappers
- [ ] Create `ComboService`
- [ ] Create `PaymentMethodService`
- [ ] Create `PaymentService`
- [ ] Create `PaymentController`
- [ ] Add seed data for combos and payment methods
- [ ] Write unit tests
- [ ] Write integration tests
