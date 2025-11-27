# PAYMENT SCREEN UI DESCRIPTION

## 1. UI OVERVIEW

The payment screen (`PaymentFragment`) consists of 5 main sections:

| Section | Description |
|---------|-------------|
| **Header** | Back button + Title "Payment Confirmation" |
| **Movie Ticket Card** | Ticket card with poster, movie info and showtime |
| **Combo Section** | List of popcorn & drink combos with quantity selector |
| **Payment Methods** | List of payment methods (VietQR, MoMo, ZaloPay) |
| **Price Summary + Bottom Bar** | Price breakdown and payment button |

---

## 2. FILE STRUCTURE

```
app/src/main/java/com/example/mobileapp/feature/payment/
├── domain/model/
│   ├── BookingInfo.kt          # Booking information model
│   ├── ComboItem.kt            # Combo item model
│   ├── PaymentMethod.kt        # Payment method model
│   └── PaymentSummary.kt       # Price summary model
├── ui/
│   ├── adapter/
│   │   ├── ComboAdapter.kt     # Adapter for combo list
│   │   └── PaymentMethodAdapter.kt  # Adapter for payment methods
│   ├── fragment/
│   │   └── PaymentFragment.kt  # Main fragment
│   └── viewmodel/
│       └── PaymentViewModel.kt # ViewModel handling logic
```

---

## 3. REQUIRED APIs

### 3.1. GET Booking Info API

**Endpoint:** `GET /api/v1/bookings/{bookingId}`

**Response:**
```json
{
  "bookingInfo": {
    "movieTitle": "String",
    "moviePosterUrl": "String",
    "genre": "String",
    "format": "String",
    "duration": "String",
    "cinemaName": "String",
    "showtime": "String",
    "showdate": "String",
    "seats": ["String"],
    "room": "String",
    "ticketPrice": "Long",
    "ticketCount": "Int"
  }
}
```

**Field Details:**

| Field | Type | Description | Example | UI Element |
|-------|------|-------------|---------|------------|
| `movieTitle` | String | Movie name | "Avengers: Endgame" | `tv_movie_title` |
| `moviePosterUrl` | String | Movie poster image URL | "https://..." | `img_movie_poster` |
| `genre` | String | Movie genre | "Action" | `tv_genre_tag` |
| `format` | String | Screening format | "2D Subtitled" | `tv_movie_meta` |
| `duration` | String | Movie duration | "181 min" | `tv_movie_meta` |
| `cinemaName` | String | Cinema name | "CGV Vincom Center" | `tv_cinema_name` |
| `showtime` | String | Show time | "19:30" | `tv_showtime` |
| `showdate` | String | Show date | "Today, 26/11" | `tv_showdate` |
| `seats` | List\<String\> | List of selected seat codes | ["F12", "F13"] | `tv_seats` |
| `room` | String | Screening room name/number | "Room 05" | `tv_room` |
| `ticketPrice` | Long | Price per ticket (VND) | 90000 | Used for calculation |
| `ticketCount` | Int | Number of tickets | 2 | Used for calculation |

**UI Display Format:**
- `tv_movie_meta`: Combine format + duration = `"{format} - {duration}"` (e.g., "2D Subtitled - 181 min")
- `tv_seats`: Join seats list with comma = `seats.joinToString(", ")` (e.g., "F12, F13")

---

### 3.2. GET Combos API

**Endpoint:** `GET /api/v1/combos` or `GET /api/v1/cinemas/{cinemaId}/combos`

**Response:**
```json
{
  "combos": [
    {
      "id": "String",
      "name": "String",
      "description": "String",
      "price": "Long",
      "imageUrl": "String"
    }
  ]
}
```

**Field Details:**

| Field | Type | Description | Example | UI Element (item_combo.xml) |
|-------|------|-------------|---------|----------------------------|
| `id` | String | Combo identifier | "combo_1" | Used when submitting payment |
| `name` | String | Combo name | "Couple Combo Deluxe" | `tv_combo_name` |
| `description` | String | Combo description | "1 Large Popcorn + 2 Large Coke" | `tv_combo_description` |
| `price` | Long | Combo price (VND) | 109000 | `tv_combo_price` |
| `imageUrl` | String | Combo image URL | "https://..." | `img_combo` |

**Note:**
- `quantity` is managed on client-side (default = 0)
- User can increase/decrease quantity via `btn_increase` / `btn_decrease`

---

### 3.3. GET Payment Methods API

**Endpoint:** `GET /api/v1/payment-methods`

**Response:**
```json
{
  "paymentMethods": [
    {
      "id": "String",
      "name": "String",
      "description": "String",
      "iconType": "String"
    }
  ]
}
```

**Field Details:**

| Field | Type | Description | Example | UI Element |
|-------|------|-------------|---------|------------|
| `id` | String | Payment method identifier | "vietqr" | Used when submitting payment |
| `name` | String | Payment method name | "VietQR" | `tv_payment_name` |
| `description` | String | Short description | "Scan bank QR code" | `tv_payment_description` |
| `iconType` | Enum | Icon type for display | "VIETQR" | `icon_container`, `img_payment_icon` |

**Valid iconType Values:**

| iconType | Description | Background Resource |
|----------|-------------|---------------------|
| `VIETQR` | Bank QR payment | `bg_icon_vietqr` |
| `MOMO` | MoMo e-wallet | `bg_icon_momo` |
| `ZALOPAY` | ZaloPay wallet | `bg_icon_zalopay` |

---

### 3.4. POST Payment API

**Endpoint:** `POST /api/v1/payments`

**Request Body:**
```json
{
  "bookingId": "String",
  "paymentMethodId": "String",
  "combos": [
    {
      "comboId": "String",
      "quantity": "Int"
    }
  ],
  "discountCode": "String | null"
}
```

**Request Field Details:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `bookingId` | String | Yes | Booking ID from seat selection step |
| `paymentMethodId` | String | Yes | Selected payment method ID |
| `combos` | Array | Yes | List of selected combos (can be empty) |
| `combos[].comboId` | String | Yes | Combo ID |
| `combos[].quantity` | Int | Yes | Quantity (> 0) |
| `discountCode` | String | No | Discount code (if any) |

**Success Response:**
```json
{
  "success": true,
  "transactionId": "String",
  "qrCodeUrl": "String | null",
  "message": "String"
}
```

**Error Response:**
```json
{
  "success": false,
  "errorCode": "String",
  "message": "String"
}
```

---

### 3.5. POST Calculate Price API (Optional)

**Endpoint:** `POST /api/v1/payments/calculate`

**Purpose:** Calculate total price when user changes combo selection or applies discount code

**Request:**
```json
{
  "bookingId": "String",
  "combos": [
    {
      "comboId": "String",
      "quantity": "Int"
    }
  ],
  "discountCode": "String | null"
}
```

**Response:**
```json
{
  "ticketPrice": "Long",
  "ticketCount": "Int",
  "comboPrice": "Long",
  "discount": "Long",
  "totalPrice": "Long"
}
```

**Response Field Details:**

| Field | Type | Description | Example | UI Element |
|-------|------|-------------|---------|------------|
| `ticketPrice` | Long | Total ticket price (ticketPrice x ticketCount) | 180000 | `tv_ticket_price` |
| `ticketCount` | Int | Number of tickets | 2 | Label "Ticket (x2)" |
| `comboPrice` | Long | Total combo price | 79000 | `tv_combo_price` |
| `discount` | Long | Discount amount | 15000 | `tv_discount` |
| `totalPrice` | Long | Final total amount | 244000 | `tv_total_price`, `btn_pay` |

**Calculation Formula (if calculated on client):**
```kotlin
totalPrice = ticketPrice + comboPrice - discount
```

---

## 4. PRICE DISPLAY FORMAT

**VND Currency Format Rule:**
```kotlin
fun formatPrice(price: Long): String {
    return String.format("%,d", price).replace(",", ".") + "d"
}
```

**Examples:**
| Value | Display |
|-------|---------|
| 90000 | "90.000d" |
| 109000 | "109.000d" |
| 180000 | "180.000d" |
| 244000 | "244.000d" |

**Discount Display:** Add "-" prefix
```kotlin
tvDiscount.text = "-${formatPrice(discount)}"  // "-15.000d"
```

---

## 5. DATA FLOW DIAGRAM

```
+---------------------------------------------------------------------+
|                       PAYMENT SCREEN FLOW                           |
+---------------------------------------------------------------------+
|                                                                     |
|  +------------------+                                               |
|  | Screen Load      |                                               |
|  +--------+---------+                                               |
|           |                                                         |
|           v                                                         |
|  +--------------------------------------------------------------+   |
|  | [Parallel API Calls]                                         |   |
|  |  |-- GET /bookings/{id}     --> BookingInfo                  |   |
|  |  |-- GET /combos            --> List<ComboItem>              |   |
|  |  +-- GET /payment-methods   --> List<PaymentMethod>          |   |
|  +--------------------------------------------------------------+   |
|           |                                                         |
|           v                                                         |
|  +------------------+                                               |
|  | Render UI        |                                               |
|  |  - Movie Card    |                                               |
|  |  - Combo List    |                                               |
|  |  - Payment List  |                                               |
|  |  - Price Summary |                                               |
|  +--------+---------+                                               |
|           |                                                         |
|           v                                                         |
|  +--------------------------------------------------------------+   |
|  | [User Interactions]                                          |   |
|  |  |-- Tap +/- combo      --> updateComboQuantity()            |   |
|  |  |-- Select payment     --> selectPaymentMethod()            |   |
|  |  +-- Apply discount     --> POST /payments/calculate         |   |
|  +--------------------------------------------------------------+   |
|           |                                                         |
|           v                                                         |
|  +------------------+                                               |
|  | Recalculate      |                                               |
|  | PaymentSummary   |--> Update tv_total_price, btn_pay            |
|  +--------+---------+                                               |
|           |                                                         |
|           v                                                         |
|  +------------------+                                               |
|  | User taps        |                                               |
|  | "Pay Now"        |                                               |
|  +--------+---------+                                               |
|           |                                                         |
|           v                                                         |
|  +------------------+      +-----------------+                      |
|  | POST /payments   |----->| Success?        |                      |
|  +------------------+      +--------+--------+                      |
|                                     |                               |
|                    +----------------+----------------+               |
|                    v                                v               |
|           +--------------+                  +--------------+        |
|           | Show Success |                  | Show Error   |        |
|           | Toast + QR   |                  | Message      |        |
|           +--------------+                  +--------------+        |
|                                                                     |
+---------------------------------------------------------------------+
```

---

## 6. UI ELEMENT MAPPING

### 6.1. Movie Ticket Card (fragment_payment.xml)

| UI Element ID | Data Field | Source |
|---------------|------------|--------|
| `img_movie_poster` | moviePosterUrl | BookingInfo |
| `tv_genre_tag` | genre | BookingInfo |
| `tv_movie_title` | movieTitle | BookingInfo |
| `tv_movie_meta` | format + " - " + duration | BookingInfo |
| `tv_cinema_name` | cinemaName | BookingInfo |
| `tv_showtime` | showtime | BookingInfo |
| `tv_showdate` | showdate | BookingInfo |
| `tv_seats` | seats.joinToString(", ") | BookingInfo |
| `tv_room` | room | BookingInfo |

### 6.2. Combo Item (item_combo.xml)

| UI Element ID | Data Field | Source |
|---------------|------------|--------|
| `img_combo` | imageUrl | ComboItem |
| `tv_combo_name` | name | ComboItem |
| `tv_combo_description` | description | ComboItem |
| `tv_combo_price` | formatPrice(price) | ComboItem |
| `tv_quantity` | quantity.toString() | ComboItem (client-side) |

### 6.3. Payment Method (item_payment_method.xml)

| UI Element ID | Data Field | Source |
|---------------|------------|--------|
| `tv_payment_name` | name | PaymentMethod |
| `tv_payment_description` | description | PaymentMethod |
| `icon_container` | iconType -> background | PaymentMethod |
| `img_payment_icon` | iconType -> tint | PaymentMethod |
| `check_indicator` | isSelected | PaymentMethod (client-side) |

### 6.4. Price Summary (fragment_payment.xml)

| UI Element ID | Data Field | Format |
|---------------|------------|--------|
| `tv_ticket_price` | ticketPrice | formatPrice() |
| `tv_combo_price` | comboPrice | formatPrice() |
| `tv_discount` | discount | "-" + formatPrice() |
| `tv_total_price` | totalPrice | formatPrice() |
| `btn_pay` | totalPrice | "Pay " + formatPrice() |

---

## 7. IMPORTANT NOTES FOR BACKEND

### 7.1. General Rules
1. **Currency Unit:** All prices use `Long` type, unit is VND (do not use floating point)
2. **Seats Format:** Return as string array, frontend will join them
3. **IconType:** Fixed enum values: `VIETQR`, `MOMO`, `ZALOPAY`
4. **Null Handling:** Optional fields can return `null`

### 7.2. Required Validations
- `ticketPrice` > 0
- `ticketCount` >= 1
- `seats` must not be empty
- `paymentMethodId` must exist in system
- `comboId` must exist and be available

### 7.3. Suggested Error Codes
| Code | Description |
|------|-------------|
| `BOOKING_NOT_FOUND` | Booking not found |
| `BOOKING_EXPIRED` | Booking has expired |
| `PAYMENT_METHOD_INVALID` | Invalid payment method |
| `COMBO_NOT_AVAILABLE` | Combo not available |
| `INSUFFICIENT_BALANCE` | Insufficient balance |
| `PAYMENT_FAILED` | Payment failed |

### 7.4. Pagination (if needed)
If combo list is large, API should support pagination:
```json
{
  "combos": [...],
  "pagination": {
    "page": 1,
    "pageSize": 10,
    "totalItems": 25,
    "totalPages": 3
  }
}
```

---

## 8. SAMPLE DATA

### BookingInfo Sample
```json
{
  "movieTitle": "Avengers: Endgame",
  "moviePosterUrl": "https://images.unsplash.com/photo-1536440136628-849c177e76a1",
  "genre": "Action",
  "format": "2D Subtitled",
  "duration": "181 min",
  "cinemaName": "CGV Vincom Center",
  "showtime": "19:30",
  "showdate": "Today, 26/11",
  "seats": ["F12", "F13"],
  "room": "Room 05",
  "ticketPrice": 90000,
  "ticketCount": 2
}
```

### ComboItem Sample
```json
[
  {
    "id": "combo_1",
    "name": "Couple Combo Deluxe",
    "description": "1 Large Popcorn 2 flavors + 2 Large Coke",
    "price": 109000,
    "imageUrl": "https://images.unsplash.com/photo-1585647347483-22b66260dfff"
  },
  {
    "id": "combo_2",
    "name": "Solo Energy Combo",
    "description": "1 Medium Popcorn + 1 Medium Pepsi",
    "price": 79000,
    "imageUrl": "https://images.unsplash.com/photo-1576158187530-98706e75b285"
  }
]
```

### PaymentMethod Sample
```json
[
  {
    "id": "vietqr",
    "name": "VietQR",
    "description": "Scan bank QR code",
    "iconType": "VIETQR"
  },
  {
    "id": "momo",
    "name": "MoMo",
    "description": "MoMo e-wallet",
    "iconType": "MOMO"
  },
  {
    "id": "zalopay",
    "name": "ZaloPay",
    "description": "ZaloPay wallet",
    "iconType": "ZALOPAY"
  }
]
```

### PaymentSummary Sample
```json
{
  "ticketPrice": 180000,
  "ticketCount": 2,
  "comboPrice": 79000,
  "discount": 15000,
  "totalPrice": 244000
}
```
