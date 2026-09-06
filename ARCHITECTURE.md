# 🍯 HoneyChain Architecture Guide

> **HoneyChain** — Production-Grade, Modular Full-Stack Architecture for Blockchain-Powered Honey Traceability.

---

## 1. System Overview

HoneyChain is engineered with a **Domain-Driven, Modular Layered Architecture** on the backend (Spring Boot 3.3.5 / Java 17) and a **Feature-Oriented Component Architecture** on the frontend (React 19 / Vite / Redux Toolkit / Tailwind CSS).

The architecture is designed to scale across 15+ future modules including IoT Telemetry, AI Yield Prediction, Hyperledger Blockchain Anchoring, Lab Purity Testing, QR Traceability, and Marketplace E-commerce without architectural degradation or cyclic dependencies.

---

## 2. Backend Architecture

### Root Package: `com.honeychain`

### Layered Flow

```
HTTP Request
     │
     ▼
[ Controller ]         ── Validates request payload (@Valid)
     │                    Maps DTOs, returns ResponseEntity<ApiResponse<T>>
     ▼
[ Service Interface ]  ── Defines business contract
     │
     ▼
[ Service Impl ]       ── Enforces business rules, transactions (@Transactional),
     │                    interacts with abstractions & security
     ▼
[ Repository ]         ── Spring Data JPA data access interfaces
     │
     ▼
[ Entity / Database ]  ── MySQL (Production/Dev) / H2 (In-Memory Testing)
```

### Architectural Principles

1. **Controllers are Thin**: Zero business logic in controllers. Controllers only validate input, call services, and return `ApiResponse<T>`.
2. **Entities are Encapsulated**: Never expose JPA entities directly from REST endpoints. Always map to/from specific Request and Response DTOs.
3. **Domain-Bound Entities & Enums**: Entities and enums live in their domain modules (e.g. `user/entity/User.java`, `user/entity/Role.java`), never in one giant global dumping ground.
4. **Service Abstractions**: Pluggable external integrations (Blockchain, IoT, AI, SMS, Payments) are defined via clear interfaces with mock/real implementations.
5. **No Bidirectional Spaghetti**: No circular JPA relationships. Use controlled unidirectionality, `@JsonIgnore`, or DTO mapping.
6. **Centralized Exception Handling**: `@RestControllerAdvice` translates domain exceptions into standard `ApiResponse` or `ErrorResponse` with HTTP status codes.

---

## 3. Backend Folder Structure

```
com.honeychain
│
├── HoneyChainApplication.java          # Spring Boot main entry point
│
├── config/                             # Cross-cutting infrastructure configs
│   ├── CorsConfig.java                 # CORS configuration (http://localhost:5173)
│   ├── OpenApiConfig.java              # Swagger / OpenAPI 3.0 specification
│   ├── JacksonConfig.java              # JSR-310 JavaTimeModule & date formatting
│   └── DataInitializer.java           # Startup seed data for all roles
│
├── security/                           # Core security framework & filters
│   ├── config/
│   │   └── SecurityConfig.java         # Filter chain, stateless session, RBAC rules
│   ├── jwt/
│   │   ├── JwtService.java             # JJWT 0.12 token generation, signing & parsing
│   │   └── JwtAuthenticationFilter.java# Per-request Bearer token extractor & validator
│   ├── service/
│   │   └── CustomUserDetailsService.java# UserDetails loader from UserRepository
│   └── authentication/
│       ├── JwtAuthenticationEntryPoint.java # 401 Unauthorized handler
│       └── CustomAccessDeniedHandler.java   # 403 Forbidden handler
│
├── common/                             # Shared utilities, base entities & DTOs
│   ├── controller/
│   │   └── HealthController.java       # GET /api/health
│   ├── dto/
│   │   ├── ApiResponse.java            # Standard { success, message, data, timestamp }
│   │   ├── ErrorResponse.java          # Error payload with validation errors
│   │   └── PageResponse.java           # Pagination wrapper
│   ├── entity/
│   │   └── BaseEntity.java             # @MappedSuperclass (id, createdAt, updatedAt)
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java # Centralized @RestControllerAdvice
│   │   ├── ResourceNotFoundException.java # 404
│   │   ├── BadRequestException.java       # 400
│   │   ├── UnauthorizedException.java     # 401
│   │   ├── ConflictException.java         # 409
│   │   └── ApiException.java              # 400
│   └── util/
│       └── AppConstants.java           # App constants & defaults
│
├── auth/                               # Authentication feature module
│   ├── controller/
│   │   └── AuthController.java         # /api/auth/login, send-otp, verify-otp
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── SendOtpRequest.java
│   │   └── VerifyOtpRequest.java
│   └── service/
│       ├── AuthService.java            # Auth orchestration contract
│       ├── OtpService.java             # OTP generation & validation contract
│       └── impl/
│           ├── AuthServiceImpl.java    # Password & OTP authentication rules
│           └── MockOtpService.java     # Dev/test OTP generator with expiry
│
├── user/                               # User management feature module
│   ├── controller/
│   │   └── UserController.java         # /api/users, /api/users/me
│   ├── dto/
│   │   └── UserResponse.java
│   ├── entity/
│   │   ├── User.java                   # User entity with phone, role, BCrypt pass
│   │   └── Role.java                   # BEEKEEPER, CUSTOMER, LAB, KVIC_OFFICER, ADMIN
│   ├── repository/
│   │   └── UserRepository.java         # JPA queries for phone lookup
│   └── service/
│       ├── UserService.java
│       └── impl/
│           └── UserServiceImpl.java
│
├── beekeeper/                          # Beekeeper module (/api/beekeepers/**)
│   ├── controller/BeekeeperController.java
│   └── service/
│
├── hive/                               # Hive management module
│   └── service/HiveService.java
│
├── batch/                              # Honey batch module
│   └── service/BatchService.java
│
├── lab/                                # Lab testing & certification module (/api/lab/**)
│   ├── controller/LabController.java
│   └── service/
│
├── blockchain/                         # Blockchain ledger abstraction
│   ├── service/BlockchainService.java
│   └── implementation/MockBlockchainService.java
│
├── qr/                                 # QR code generation & routing
│   └── service/QrCodeService.java
│
├── verification/                       # Consumer batch verification
│   └── service/VerificationService.java
│
├── iot/                                # IoT sensor telemetry module
│   ├── service/IotDataService.java
│   └── implementation/MockIotDataService.java
│
├── ai/                                 # AI harvest & yield prediction module
│   ├── service/YieldPredictionService.java
│   └── implementation/MockYieldPredictionService.java
│
├── marketplace/                        # Customer marketplace module (/api/customers/**)
│   ├── controller/CustomerController.java
│   └── service/
│
├── cart/                               # Shopping cart module
│   └── service/CartService.java
│
├── order/                              # Order processing module
│   └── service/OrderService.java
│
├── review/                             # Ratings and reviews module
│   └── service/ReviewService.java
│
├── notification/                       # In-app and SMS notifications
│   └── service/NotificationService.java
│
└── admin/                              # Administration & KVIC module (/api/admin/**)
    ├── controller/AdminController.java
    └── service/
```

---

## 4. Frontend Architecture

### Directory: `my-react-app/src`

```
src/
│
├── app/                                # Application core setup
│   ├── App.jsx                         # Root component (Provider + Router)
│   ├── store.js                        # Redux store config (DevTools enabled in dev)
│   └── router.jsx                      # Centralized route tree with ProtectedRoute
│
├── components/                         # Shared UI components
│   ├── common/
│   │   ├── Navbar.jsx                  # Auth-aware responsive navigation bar
│   │   └── Footer.jsx                  # Application footer
│   ├── ui/
│   │   ├── Button.jsx                  # Standardized button with loading state
│   │   ├── Card.jsx                    # Glassmorphism container
│   │   └── Input.jsx                   # Input field with label and error display
│   ├── layout/
│   │   └── MainLayout.jsx              # Default page wrapper (Navbar + Main + Footer)
│   └── feedback/
│       ├── LoadingSpinner.jsx          # Circular animated spinner
│       └── Alert.jsx                   # Info/Success/Warning/Error notification banner
│
├── features/                           # Feature-oriented domain modules
│   ├── landing/
│   │   └── pages/
│   │       └── LandingPage.jsx         # Marketing landing page with live health check
│   ├── auth/
│   │   ├── api/
│   │   │   └── authApi.js              # Axios calls: login, sendOtp, verifyOtp
│   │   ├── authSlice.js                # Redux slice: tokens, user, roles, OTP state
│   │   ├── hooks/
│   │   │   └── useAuth.js              # Hook exposing auth actions and selectors
│   │   └── pages/
│   │       ├── LoginPage.jsx           # Password login (Admin, Lab, Officer)
│   │       ├── OtpLoginPage.jsx        # SMS OTP login (Beekeeper, Customer)
│   │       └── RegisterPage.jsx        # First-time registration with OTP
│   ├── beekeeper/
│   │   └── pages/
│   │       └── BeekeeperDashboard.jsx  # Beekeeper portal dashboard
│   ├── customer/
│   │   └── pages/
│   │       └── CustomerDashboard.jsx   # Customer marketplace dashboard
│   ├── lab/
│   │   └── pages/
│   │       └── LabDashboard.jsx        # Lab testing portal dashboard
│   └── admin/
│       └── pages/
│           └── AdminDashboard.jsx      # Admin & KVIC oversight dashboard
│
├── layouts/                            # Role-specific portal layouts
│   ├── BeekeeperLayout.jsx             # Amber-themed portal layout
│   ├── CustomerLayout.jsx              # Emerald-themed portal layout
│   ├── LabLayout.jsx                   # Indigo-themed portal layout
│   └── AdminLayout.jsx                 # Rose-themed portal layout
│
├── services/                           # Centralized infrastructure services
│   ├── axios.js                        # Axios instance + JWT interceptor + 401 handler
│   └── storage.js                      # LocalStorage token and user manager
│
├── constants/
│   └── roles.js                        # Role enums, labels, and redirect routes
│
├── utils/
│   └── helpers.js                      # Date formatting, hash truncation, role helpers
│
└── index.css                           # Tailwind CSS v4 + HoneyChain design tokens
```

---

## 5. Authentication & Authorization Flow

### Password Login (Admin, Lab, KVIC Officer)
```
1. Client POST /api/auth/login { phoneNumber, password }
2. AuthController -> AuthService.login()
3. Look up user by phone, verify isActive
4. PasswordEncoder.matches(rawPassword, user.getPassword())
5. JwtService.generateToken(phone, role)
6. Returns { success: true, token: "...", role: "ADMIN" }
7. Client stores token in localStorage & Redux store
8. Client redirects to /admin/dashboard
```

### OTP Login (Beekeeper, Customer)
```
1. Client POST /api/auth/send-otp { phoneNumber, role }
2. OtpService.generateAndSendOtp(phone) -> Generates 6-digit OTP (5-min expiry)
3. In dev mode, OTP logged to console (Dev code: 123456)
4. Client enters OTP -> POST /api/auth/verify-otp { phoneNumber, otp, role }
5. OtpService.verifyOtp() -> checks expiry & code match
6. If new user, auto-registers with given role
7. JwtService.generateToken(phone, role)
8. Returns { success: true, token: "...", role: "BEEKEEPER" }
9. Client redirects to /beekeeper/dashboard
```

### Role-Based Authorization
| Role | Authority | Access Rules |
|---|---|---|
| `BEEKEEPER` | `ROLE_BEEKEEPER` | `/api/beekeepers/**` |
| `CUSTOMER` | `ROLE_CUSTOMER` | `/api/customers/**` |
| `LAB` | `ROLE_LAB` | `/api/lab/**` |
| `ADMIN` | `ROLE_ADMIN` | `/api/admin/**` |
| `KVIC_OFFICER` | `ROLE_KVIC_OFFICER` | `/api/admin/**` |
| Public | None | `/api/auth/**`, `/api/health`, `/swagger-ui/**` |

---

## 6. Request Lifecycle

```
[ HTTP Client ]
      │ (with Authorization: Bearer <jwt>)
      ▼
[ CorsFilter ]
      │
      ▼
[ JwtAuthenticationFilter ]
      │── Extracts token from Authorization header
      │── Validates signature & expiration via JwtService
      │── Loads user authorities via CustomUserDetailsService
      └── Populates SecurityContextHolder
      │
      ▼
[ AuthorizationFilter ]
      │── Matches request against HttpSecurity rules (e.g. hasRole('ADMIN'))
      │── If rejected: CustomAccessDeniedHandler returns 403 JSON
      └── If unauthenticated: JwtAuthenticationEntryPoint returns 401 JSON
      │
      ▼
[ Controller ]
      │── Validates request with @Valid
      │── Passes clean DTO to Service
      └── Wraps response in ApiResponse.success(data)
      │
      ▼
[ Service (@Transactional) ]
      │── Executes business rules
      │── Calls JPA Repository or external abstraction
      └── Throws ResourceNotFoundException / BadRequestException on error
      │
      ▼
[ GlobalExceptionHandler ] (catches exceptions)
      └── Returns consistent error JSON { success: false, message: "...", timestamp: "..." }
```

---

## 7. Rules for Adding Future Features

When creating a new module (e.g. `batch`):
1. **Directory**: Create `com.honeychain.batch` with subpackages: `controller`, `dto`, `service`, `repository`, `entity`, `mapper`.
2. **Entity**: Place entity in `batch/entity/HoneyBatch.java` extending `common.entity.BaseEntity`.
3. **DTOs**: Create `CreateBatchRequest.java` and `BatchResponse.java` in `batch/dto/`. Never return `HoneyBatch` from controllers.
4. **Service**: Define `BatchService` interface and `BatchServiceImpl` implementation.
5. **Security**: Add security matchers in `SecurityConfig.java` if the endpoint requires specific role permissions.
6. **Frontend**: Create `src/features/batch/` containing `api/batchApi.js`, `components/`, and `pages/`. Add routes in `src/app/router.jsx`.
