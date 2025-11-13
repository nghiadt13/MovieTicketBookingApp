# Database Schema Documentation

## Tổng Quan

Hệ thống Movie Booking sử dụng PostgreSQL với schema được thiết kế để quản lý:

- Người dùng và xác thực (Users & Authentication)
- Danh mục phim (Movie Catalog)
- Rạp chiếu và phòng chiếu (Cinemas & Screens)
- Lịch chiếu và đặt vé (Showtimes & Bookings)
- Hệ thống thành viên (Membership System)
- Quản lý nội dung (Content Management)

---

## Cấu Trúc Database

### 1. Extensions & Utilities

#### CITEXT Extension

```sql
CREATE EXTENSION IF NOT EXISTS citext;
```

- Cho phép so sánh text không phân biệt hoa thường
- Sử dụng cho email, phone_number, slug

#### set_updated_at() Function

```sql
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER
```

- Tự động cập nhật trường `updated_at` khi record được update
- Được sử dụng bởi nhiều trigger trong hệ thống

---

## 2. ENUM Types

### Movie Status

```sql
CREATE TYPE movie_status_enum AS ENUM ('COMING_SOON','NOW_SHOWING','ENDED');
```

### User Roles

```sql
CREATE TYPE user_role_enum AS ENUM ('ROLE_USER', 'ROLE_ADMIN', 'ROLE_STAFF');
```

### Social Providers

```sql
CREATE TYPE social_provider_enum AS ENUM ('GOOGLE', 'FACEBOOK', 'X', 'INSTAGRAM');
```

### OTP Related

```sql
CREATE TYPE otp_channel_enum AS ENUM ('EMAIL', 'SMS');
CREATE TYPE otp_purpose_enum AS ENUM ('PASSWORD_RESET', 'TWO_FACTOR_AUTH', 'ACCOUNT_VERIFICATION');
```

### Seat & Booking

```sql
CREATE TYPE seat_type_enum AS ENUM ('STANDARD', 'VIP', 'COUPLE', 'DELUXE');
CREATE TYPE showtime_status_enum AS ENUM ('SCHEDULED', 'SELLING', 'FULL', 'COMPLETED', 'CANCELLED');
CREATE TYPE booking_status_enum AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED');
```

---

## 3. User Management Module

### 3.1. users Table

**Mục đích:** Lưu trữ thông tin người dùng cơ bản

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, IDENTITY | ID tự động tăng |
| email | CITEXT | UNIQUE | Email đăng nhập (nullable) |
| phone_number | CITEXT | UNIQUE | Số điện thoại đăng nhập (nullable) |
| password_hash | TEXT | | Mật khẩu đã hash (nullable cho social login) |
| display_name | TEXT | NOT NULL | Tên hiển thị |
| avatar_url | TEXT | | URL ảnh đại diện |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Trạng thái tài khoản |
| email_verified_at | TIMESTAMPTZ | | Thời điểm verify email |
| phone_number_verified_at | TIMESTAMPTZ | | Thời điểm verify phone |
| last_login_at | TIMESTAMPTZ | | Lần đăng nhập cuối |
| created_at | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Thời điểm tạo |
| updated_at | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Thời điểm cập nhật |

**Constraints:**
- CHECK: `email IS NOT NULL OR phone_number IS NOT NULL` (phải có ít nhất email hoặc phone)

**Triggers:**
- `trg_users_updated_at`: Tự động cập nhật `updated_at`

### 3.2. roles Table

**Mục đích:** Định nghĩa các vai trò trong hệ thống

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, IDENTITY | ID vai trò |
| name | user_role_enum | NOT NULL, UNIQUE | Tên vai trò |

**Seed Data:**
- ROLE_USER
- ROLE_ADMIN
- ROLE_STAFF

### 3.3. user_roles Table

**Mục đích:** Junction table liên kết users và roles (Many-to-Many)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | BIGINT | FK → users(id), CASCADE | ID người dùng |
| role_id | BIGINT | FK → roles(id), RESTRICT | ID vai trò |

**Primary Key:** (user_id, role_id)

**Indexes:**
- `idx_user_roles_role_id` on role_id

### 3.4. social_accounts Table

**Mục đích:** Liên kết tài khoản với social providers

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, IDENTITY | ID |
| user_id | BIGINT | FK → users(id), CASCADE | ID người dùng |
| provider | social_provider_enum | NOT NULL | Nhà cung cấp |
| provider_user_id | TEXT | NOT NULL | ID từ provider |
| created_at | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Thời điểm liên kết |

**Constraints:**
- UNIQUE: (user_id, provider) - Mỗi user chỉ link 1 account/provider
- UNIQUE: (provider, provider_user_id) - Mỗi social account chỉ link 1 user

**Indexes:**
- `idx_social_accounts_user_id` on user_id

### 3.5. user_otps Table

**Mục đích:** Quản lý OTP cho các mục đích khác nhau

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, IDENTITY | ID |
| user_id | BIGINT | FK → users(id), CASCADE | ID người dùng |
| purpose | otp_purpose_enum | NOT NULL | Mục đích OTP |
| channel | otp_channel_enum | NOT NULL | Kênh gửi (EMAIL/SMS) |
| contact_value | TEXT | NOT NULL | Email/Phone nhận OTP |
| code_hash | TEXT | NOT NULL | Hash của OTP code |
| expires_at | TIMESTAMPTZ | NOT NULL | Thời điểm hết hạn |
| consumed_at | TIMESTAMPTZ | | Thời điểm sử dụng |
| attempt_count | SMALLINT | NOT NULL, DEFAULT 0 | Số lần thử |
| created_at | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Thời điểm tạo |

**Constraints:**
- CHECK: `exp
ata
5. Seed Ddexes)
4. Inenciesự dependhứ tles (theo t Tabpes
3.. ENUM Tyunctions
2ns & Fionste:**
1. Ex tự thực thi.)

**Thứ2, V3..ons (V1, Vmigrati version ành cácthhỏ a nions
3. Chiatquibase migrLiy/Flywaerence cho  refụng làm Sử d mới
2.se trên databarực tiếp Chạy t
1.có thể:ema và schộ  chứa toàn bct.sql`ojePre `
Fily
ion Strategrat
## Mig
---
re)
awaezone-TZ (timTIMESTAMP dụng igger
- Sửvới trd_at` có `updatee  updatables có thểd_at`
- T`createng có uan trọes qất cả tabls
- TmpTimestay

### historaudit và u cho ại dữ liệật
- Giữ la th vì xó thaytive` flag dụng `is_ac- SửDelete
# Soft ##rùng lặp

iệu không t cho dữ ltraints UNIQUE cons
- rulesho business craintsK constCHEC hợp
- ESTRICT phùASCADE/R keys với C
- Foreign Integrity Datas

###uerieolumn qti-cs cho mulexeposite indries
- Comred quefiltelause) cho HERE cl indexes (Wartiayên
- Pry thường xuquecác ạo cho xes được tance
- Indeform
### Perive
case-sensitduplicate tránh /phone để emailXT cho Sử dụng CITE
-  lưush trước khiợc haodes đư OTP cCrypt)
-c hash (Brds luôn đượty
- Passwo
### Securites
actices & No
## Best Pr`

---
s
``owtime  └── sh                   locks
── seat_         └      │             s
    ing_item─ bookseats ──┬─eens ──┬── ── scrnemas ciks

locseat_    └──                 s
     king─ boo   ├─                      s
rice┬── ticket_pimes ──howt └── s         formats
_formats ──   ├── movienres
      ── geovie_genres ──┬── m

movies ersrship_tiembeips ── mer_membershus └── 
       ksloc├── seat_
        ─ seatsms ─oking_itekings ── bo    ├── boos
    user_otp─      ├─ccounts
   ├── social_a     es
   es ── roler_rol─┬── usrs ─`
use
``Diagram
ips lationsh## Re



---t |ểm cập nhậi đihờW() | TFAULT NOLL, DE| NOT NUAMPTZ ST TIMEdated_at |
| upm tạo |i điểhờOW() | TAULT NEFNULL, DOT Z | NTAMPT| TIMESreated_at ái |
| cTrạng thUE |  TRL, DEFAULTOT NULOOLEAN | Ne | Biv |
| is_actkhi click | URL đích RCHAR(255) |get_url | VAung |
| tar dNội| | nt | TEXT ntenh |
| coL hình ả NULL | UR| NOT5)  VARCHAR(25rl |e_u|
| imagiêu đề  | TNOT NULL5) | ARCHAR(25tle | V ID |
| tiARY KEY |IMIAL | PRIGSER
| id | B---------|-----------|------|-----------|-- |
|-onptiscrints | DeConstrain | Type | 
| Colum
n homepagecarousel trêh:** Banner c đícble

**Mụitems Taarousel_ 9.2. c
###
n |iểm xuất bả | Thời đNT_TIMESTAMPAULT CURRE DEFTAMP |d_at | TIMESsheh |
| publiình ảnXT | | URL h| TEge_url ma dung |
| iộiT NULL | NEXT | NO content | Tề |
|LL | Tiêu đNUEXT | NOT le | T| tit|
 tin tức KEY | ID| PRIMARY IAL id | SER----|
| -|-----------------------|------|--ion |
|----s | Descripttraint Type | Consumn |
| Coln tức
ch:** Ti đíle

**Mục.1. news Tabule

### 9gement Modent Mana. Cont
---

## 9_id
serUE: us:**
- UNIQstraint |

**Conhậtm cập n| Thời điểTIMESTAMP RRENT_| DEFAULT CUMP | TIMESTAated_at 
| updgia |ham hời điểm tP | TT_TIMESTAMAULT CURRENEF| DMESTAMP t | TI
| joined_ah lũy |0 | Điểm tíc| DEFAULT  | INTEGER oints
| pi tiêu | | Tổng ch 0| DEFAULT(10,2)  DECIMAL |ngotal_spendi |
| tID hạng | d)ers(i_tihiprsFK → membe INTEGER | | tier_id |ời dùng |
D ngưIQUE | Iid), UNrs(T | FK → useid | BIGINer_ us ID |
|MARY KEY |PRIAL | 
| id | SERI----------|------|------------|-----------|n |
|-tios | Descriponstraint | C | TypeColumnuser

| n của hành viên t:** Thông ti đíchble

**Mụcrships Taser_membe
### 8.2. uô tả |
 TEXT | | Mescription |h |
| dhình ản0) | | URL AR(50ARCHage_url | V|
| im Hệ số điểm .0 || DEFAULT 13,2) MAL(plier | DECInts_multioigiá |
| p giảm EFAULT 0 | % D) |,2| DECIMAL(5_percent 
| discountầu | yêu cL | Chi tiêuUL NOT N) |(10,2d | DECIMALireequ| spending_r|
ng hứ tự hạUNIQUE | TNULL, T R | NO| INTEGErank_order n hạng |
| QUE | Tê, UNI NULLNOTRCHAR(50) | me | VA| na |
D hạng KEY | IAL | PRIMARY
| id | SERI------|---|----------------|------|-------tion |
|--escriptraints | De | Cons | Typmnn

| Colug thành viê Các hạnc đích:****MụTable

_tiers shipmber 8.1. meule

###od System Mhiprs. Membe# 8

---

#ntiln locked_until` o_ulockedt_locks_- `idx_seadexes:**
id)

**Int_ime_id, sea (showtUE:s:**
- UNIQ**Constraintm tạo |

Thời điể now() | ULL, DEFAULTZ | NOT NTAMPTat | TIMES created_n khi |
|đếLL | Khóa  NUMPTZ | NOT TIMESTAcked_until ||
| lo người dùng CADE | ID, CASd)users(iINT | FK →  BIG| user_id |ghế |
 | ID SCADEts(id), CA→ seaFK IGINT | eat_id | Bu |
| shiếất c | ID suid), CASCADEhowtimes(T | FK → sBIGINime_id | howt| ID |
| s, IDENTITY  KEYARYPRIMGINT | | BId | i-----|
-|----------------------|----------|--ription |
|ts | Descain | Constrn | TypeColum

| ng đặt vé khi đaờiế tạm th ghích:** Khóae

**Mục đcks Tableat_lo
### 7.3. sseat_id
on eat_id` tems_s_ingx_booki:**
- `idxes
**Inde seat_id)
id,E: (booking_- UNIQUtraints:**

**Cons |
 điểm tạohời | TFAULT now()L, DE NOT NULTZ |TIMESTAMP| ted_at 
| creaé |á v Gi 0 |CK >= NULL, CHE0,2) | NOTECIMAL(1| price | D |
T | ID ghếICESTR R),ats(id FK → seT |d | BIGIN|
| seat_iID đơn  CASCADE | ookings(id),INT | FK → bd | BIG booking_iD |
|Y | I, IDENTITY KEYRIMAR | PNT
| id | BIGI-------|-----|--------|-------------------|on |
|-escriptiaints | D Constrpe |umn | Tyặt

| Colng đơn điết vé tro** Chi t**Mục đích:ms Table

ite.2. booking_
### 7d_at DESC
reateat` on cgs_created_x_bookintus
- `idus` on sta_statingsx_bookd
- `id_iern uss_user_id` oidx_booking- `Indexes:**


**ed_at``updatt  nhậcậpự động _at`: Tgs_updatedookin**
- `trg_bs:Trigger
**`
d_atteeacrpires_at > - CHECK: `exnts:**
Constrai**hật |

điểm cập n Thời  now() |ULL, DEFAULTT N NOMPTZ |STAIME Tated_at |upd|
| m tạo i điể Thờnow() |DEFAULT | NOT NULL, MESTAMPTZ t | TIted_a| creaết hạn |
Thời điểm h |  NULLZ | NOTPTTIMESTAMat | 
| expires_nh toán |hời điểm tha| | TMESTAMPTZ paid_at | TIch |
| ID giao dịT | |  | TEXidction_transament_
| pay |nh toánhức tha| | Phương thod | TEXT payment_met|
|  tiền ng | Tổ >= 0CHECKL, UL2) | NOT NIMAL(10,ount | DEC_am
| total | Trạng thái |NG'PENDI, DEFAULT 'm | NOT NULLtatus_enubooking_ss | atuhiếu |
| stt cID suấTRICT | es(id), RES showtimT | FK →GINBIime_id | | showt|
g ười dùnD ngTRICT | Irs(id), RESuse | FK → d | BIGINT_ier| us đặt vé |
| MãQUE L, UNI NUL NOTode | TEXT | booking_c |
|D đơn | I IDENTITYY KEY, PRIMAR BIGINT || id |-|
-----------|--------------|--------|-on |
|-----iptiscrtraints | DeConse | umn | Typ

| Col đặt vé* Đơnục đích:*able

**M bookings T

### 7.1.nt Moduleng & Payme# 7. Booki

---

#type)d, seat_owtime_iNIQUE: (shints:**
- U**Constraạo |

 t | Thời điểmT now() DEFAUL NULL,TZ | NOT TIMESTAMPted_at |crea |
| = 0 | Giá véLL, CHECK >NOT NU) | ,2(10 DECIMALprice |i ghế |
| NULL | Loạum | NOT eneat_type__type | satu |
| seD suất chiếDE | Is(id), CASCAhowtime → s BIGINT | FKowtime_id |
| sh| ID |ENTITY MARY KEY, IDRI | P | BIGINT id
|-----|-----|------------|---------------|---
|ion |Descriptaints | onstrn | Type | C

| Columuhiếo mỗi suất cế ch loại gheo vé thiáích:** G

**Mục đ Tablecket_prices. ti

### 6.2RUEctive = TWHERE is_as n statuus` oimes_statowt_sh`idx_time)
- , startscreen_id_time` on (startcreen_owtimes_ssh `idx_time)
-d, start_ovie_iime` on (m_start_t_moviedx_showtimes*
- `is:*ndexe*I
*dated_at`
ật `upp nh cậự độngd_at`: T_updateg_showtimes
- `tr*ggers:*Tri 0`

**s >=le_seat: `availabECK_time`
- CH startnd_time >K: `eHEC**
- Caints:Constr

**t |điểm cập nhậi now() | Thờ, DEFAULT OT NULLPTZ | N TIMESTAM | updated_at
| tạo || Thời điểmLT now() LL, DEFAU | NOT NUTAMPTZ TIMESed_at |
| creatthái |RUE | Trạng LT T DEFAUOT NULL,| NAN tive | BOOLE |
| is_acng ghế còn trốK >= 0 | SốHECNOT NULL, CALLINT |  | SM_seatsble |
| availaáiTrạng th' | 'SCHEDULEDEFAULT  DNOT NULL, | numime_status_ewt sho
| status |ết thúc |n k Thời giaULL |T NTAMPTZ | NOTIMESe | |
| end_timắt đầu  b Thời gianLL | NOT NUESTAMPTZ |TIM |  start_time |
|| ID phòngTRICT  REScreens(id),FK → sBIGINT | en_id | 
| screID phim | | RESTRICTid), vies( FK → mod | BIGINT |
| movie_it chiếu | suấ | ID IDENTITYMARY KEY,T | PRI| BIGIN----|
| id -----------|--------|------------|---|
|--n  Descriptionts |traipe | Conslumn | Ty

| Cou phim chiếh:** Lịch**Mục đíce

times Tabl. show.1

### 6ModulePricing ime & owt

## 6. Sh--number)

-t_ sea_name,d, row_i: (screen
- UNIQUEnts:***Constrai
* tạo |
 | Thời điểmT now()UL, DEFAZ | NOT NULLIMESTAMPTt | T_a| createdi |
 tháng| TrạEFAULT TRUE L, DT NUL| NOOOLEAN s_active | Bhế |
| i gại | LoT 'STANDARD'UL, DEFALLNOT NUm | _enuypepe | seat_t_ty|
| seat, 2, 3...)  | Số ghế (1 NOT NULLALLINT |r | SMeat_numbe...) |
| s(A, B, C| Hàng ghế NOT NULL XT |  | TE
| row_nameID phòng |E | ADASC), C(idK → screens| BIGINT | Fscreen_id  |
| hếD g | IITYNTDEARY KEY, I PRIM| BIGINT |
| id -------|----------|--|-------------|-------iption |
|-ts | Descrtrainns Type | Co Column |
| chiếu
g phòngtrongồi * Ghế nch:***Mục đíts Table

. sea 5.3
###t`
dated_anhật `up động cập ted_at`: Tựreens_updarg_sc
- `tiggers:**
**Trid, name)
a_UE: (cinemNIQ- U
**onstraints:*C
*nhật |
điểm cập ) | Thời ULT now( DEFA| NOT NULL,TZ IMESTAMP_at | T
| updated |i điểm tạoThờ | FAULT now()NULL, DEAMPTZ | NOT STTIMEd_at | 
| createi | | Trạng tháAULT TRUENULL, DEF | NOT LEANve | BOOcti
| is_aAX, 4DX) |3D, IM,  (STANDARDLoại phòngXT | | ype | TE_t
| screenhế |ố gg sK > 0 | TổnEC, CHNULLNOT SMALLINT | s | atal_se |
| tot phòng | Tên| NOT NULL | TEXT | name |
 rạpIDASCADE | s(id), CemaFK → cinBIGINT | nema_id | |
| ciòng | ID phENTITY , IDRY KEYRIMAINT | P
| id | BIG---||--------------|----------------|-----
|--ription |scDe| ts  Constrainpe |lumn | Ty rạp

| Coiếu trongg chích:** Phòn**Mục đable

s T. screen 5.2t`

###_aatedt `updng cập nhậat`: Tự độd_mas_updatetrg_cine
- `ggers:**|

**Tri cập nhật ời điểm | ThLT now()DEFAU NULL, MPTZ | NOT| TIMESTAted_at da
| upm tạo |điểw() | Thời EFAULT no NULL, DNOTMESTAMPTZ | d_at | TIreate
| ci |tháE | Trạng  DEFAULT TRU | NOT NULL,e | BOOLEAN| is_activh độ |
in | | KIMAL(11,8) DEClongitude |
| | | Vĩ độ |CIMAL(10,8) itude | DE lat
||| Email  | l | TEXT emaiại |
| tho Số điện | TEXT | |bere_num
| phonuận/Huyện |TEXT | | Qrict | ist |
| d Thành phố NULL |XT | NOT
| city | TEĐịa chỉ | NOT NULL | XT |ddress | TE|
| aên rạp  TL |T | NOT NULTEXe | am
| nạp |ID rTY | IDENTIY KEY, IMAR PR| BIGINT || id --|
----------|-------|-------------|---- |
|----oniptints | Descr Constraiumn | Type | Col
|
ếu phimp chiin rạh:** Thông tđícMục **le

 Tabnemas ci 5.1.dule

###reening Moma & Sc 5. Cine---

##at_id

t` on formats_formae_form`idx_movi**
- *Indexes:mat_id)

*vie_id, for* (mo Key:*
**Primary
nh dạng | địT | IDTRICd), RES → formats(iINT | FKid | BIGformat_
| D phim | CASCADE | Iies(id), FK → movBIGINT || d  movie_i-----|
|-----|--------|-------------|-|-------- |
scription | Detraintse | Consn | Typ
| Columo-Many)
ts (Many-trmas và fokết movieên n table liunctioc đích:** J*Mụable

*ats T. movie_form### 4.5_id

e` on genre_genr_genresidx_movie:**
- `*Indexes

*, genre_id)ovie_idry Key:** (mrimaại |

**P loICT | ID thểTR(id), RES → genresT | FK_id | BIGIN genre
| ID phim |E |), CASCADmovies(idT | FK → e_id | BIGIN
| movi------|----|--------------------|-----|--
|---on |scripti | DeonstraintsType | C| Column any)

| to-Mres (Many- và gen moviesên kếtle lion tab** Juncti
**Mục đích:es Table
_genr movie4.4.## 

#iển thị |n hNULL | Nhã | NOT  | TEXT label |
|h dạngE | Mã địnUNIQUNULL, T | NOT | code | TEX dạng |
địnhY | ID DENTIT, I PRIMARY KEYGINT || id | BI--|
------------|----------|------|---|
|-------ription ts | Descstrainype | Con| T
| Column .)
MAX, etc(2D, 3D, Ichiếu dạng nh mục định  đích:** Da

**Mụcablemats T for## 4.3.

#ly) |L-friend| Slug (URIQUE NULL, UNTEXT | NOT 
| slug | CIthể loại |QUE | Tên T NULL, UNI| TEXT | NOname  loại |
| Y | ID thểNTITY, IDEIMARY KE| PRGINT 
| id | BI-|------------|-----------|------|----------on |
| Descriptiints | Constra | Type |olumn
| Cloại phim
thể ục anh m DMục đích:**** Table

genres4.2. ## RUE

#tive = T_acistive WHERE n is_acve` o_movies_actitus
- `idx statatus` onidx_movies_ses:**
- `
**Index_at`
hật `updatedự động cập nt`: T_aupdatedrg_movies_- `t**
**Triggers:|

p nhật  điểm cậw() | ThờiULT no NULL, DEFAPTZ | NOTTIMESTAMated_at | ạo |
| updểm tThời điT now() | EFAULT NULL, DPTZ | NO | TIMESTAM_at|
| createdactive hái | Trạng tFAULT TRUE NOT NULL, DE|  BOOLEAN  is_active | |
|h giáượt đán 0 | Số lK >= CHECULT 0, NULL, DEFA | NOTt | INTEGERrating_coun bình |
| m trung | Điể, CHECK 0-10 0ULL, DEFAULT1) | NOT N(3,RIC| NUMEavg rating_railer |
| RL t| | UEXT _url | Tailer |
| tr| URL poster TEXT | poster_url |
| |i | Trạng tháING_SOON' COMULT 'LL, DEFAT NU_enum | NOtatus| movie_stus | stahành |
át Ngày phDATE | | | ase_date  rele
| (phút) |ượngời lCK > 0 | ThLINT | CHESMALin | n_m|
| duratioi dung Tóm tắt nộ TEXT | | nopsis | syim |
| | Tên phULLNOT N| e | TEXT 
| titlphim || ID NTITY , IDEKEYY IMAR | PR| BIGINT
| id -------|-------|---------|------|-------|----iption |
 | Descrnstraintspe | CoColumn | Ty| in phim

ng tLưu trữ thô* h:*
**Mục đíce
blTaovies . m

### 4.1alog Module4. Movie Cat

## L

--- NULt ISmed_ansu WHERE copose)id, pur (user_okup` onloser_otps_`idx_u:**
- exes
**Indeated_at`
> cr_at ires