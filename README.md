# 🍯 HoneyChain

> **Blockchain-powered honey traceability — from hive to customer, with verifiable purity.**

HoneyChain is a full-stack hackathon MVP that creates an end-to-end trust chain for honey:
every batch is hashed on a blockchain (simulated), lab-tested for purity, assigned a scannable QR code, and
publicly verifiable by consumers — with no login required.

> ⚠️ **Hackathon MVP Notice**: Blockchain, Payment, IoT, SMS, and AI Prediction are simulated/mock
> implementations. Clearly labelled as such. See [LIMITATIONS.md](LIMITATIONS.md).

---

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| 🔗 **Blockchain Traceability** | Every batch and lab result receives a SHA-256 mock hash |
| 🔬 **Lab Purity Testing** | Lab technicians submit purity scores; results update batch status |
| 📱 **QR Code Verification** | Scannable PNG QR code generated for every PURE batch |
| 🌐 **Public Verification** | No-login verification page: genuine/counterfeit + full timeline |
| 🛡️ **Anti-Counterfeit** | Fingerprint-based scan history; risk scoring; dispute management |
| 📡 **IoT Hive Monitoring** | Simulated temperature/humidity/activity sensors with health alerts |
| 🤖 **AI Yield Prediction** | Rule-based harvest prediction using hive health (not a real ML model) |
| 🛒 **Marketplace** | Product listings, cart, checkout, mock payment, order tracking |
| ⭐ **Reviews** | Customer reviews linked to verified orders |
| 📴 **Offline-First Batches** | IndexedDB-backed offline batch creation with background sync |
| 🌍 **Vernacular Support** | English, Tamil, and Hindi UI with persistent language preference |
| 🔊 **Voice Read-Aloud** | Browser SpeechSynthesis for key screens |
| 🔔 **Notifications** | In-app notification bell with real-time events |
| 👤 **Admin/KVIC Dashboard** | Beekeeper verification, batch oversight, lab management, analytics |

---

## 🏛️ Architecture

```
Browser
  │
  ├── React SPA (Vite)
  │     ├── Redux Toolkit (state)
  │     ├── Axios (API calls)
  │     ├── IndexedDB (offline batches)
  │     └── SpeechSynthesis (voice)
  │
  ↓ REST API
  │
  ├── Spring Boot 3.3.5 (Java 17)
  │     ├── Spring Security + JWT
  │     ├── Spring Data JPA / Hibernate
  │     ├── Mock Blockchain Service
  │     ├── ZXing QR Code Generator
  │     ├── Mock IoT Simulator
  │     ├── Mock SMS Service
  │     └── Rule-based AI Yield Predictor
  │
  ↓ JPA
  │
  └── MySQL 8.0
```

Full architecture: [ARCHITECTURE.md](ARCHITECTURE.md)

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 19, Vite 8, Redux Toolkit, React Router v7 |
| Styling | Vanilla CSS (glassmorphism, dark mode) |
| Backend | Spring Boot 3.3.5, Java 17 |
| Security | Spring Security, JWT (JJWT 0.12.6) |
| Database | MySQL 8.0 (JPA/Hibernate) |
| QR Codes | ZXing 3.5.3 |
| API Docs | SpringDoc OpenAPI 2.6.0 (Swagger UI) |
| Offline | IndexedDB (browser-native) |
| I18n | Custom translation files (en/ta/hi) |
| Voice | Web Speech API (browser SpeechSynthesis) |
| Containers | Docker, Docker Compose |
| Tests | JUnit 5, Spring MockMvc — 188 tests |

---

## 🚀 Local Setup

### Prerequisites
- Java 17+, Maven 3.9+, Node 20+, npm 10+
- MySQL 8.0 (or Docker)

### Backend

```bash
cd backend
cp .env.example .env
# Edit .env — fill in DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET

./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# → http://localhost:8080
```

### Frontend

```bash
cd my-react-app
npm install

# No .env.local needed for dev — Vite proxy handles /api/* → localhost:8080
npm run dev
# → http://localhost:5173
```

### Database

```bash
# Start MySQL via Docker for local dev
docker run -d --name honeychain-db \
  -e MYSQL_ROOT_PASSWORD=yourpassword \
  -e MYSQL_DATABASE=honeychain_db \
  -p 3306:3306 mysql:8.0
```

---

## 🐳 Docker Setup

```bash
# 1. Fill in environment variables
cp .env.example .env
# Edit .env — required: MYSQL_ROOT_PASSWORD, DB_USERNAME, DB_PASSWORD, JWT_SECRET

# 2. Build and start
docker compose build
docker compose up -d

# 3. Verify
docker compose ps
curl http://localhost:8080/api/health
# → http://localhost (frontend)
```

See [DEPLOYMENT.md](DEPLOYMENT.md) for full deployment guide.

---

## 👤 Demo Accounts

> ⚠️ **DEMO ONLY** — do not use in real production environments.

| Role | Phone | Password |
|------|-------|----------|
| 🐝 Beekeeper | 9876543210 | Demo@123 |
| 🛒 Customer | 9876543211 | Demo@123 |
| 🔬 Lab | 9876543212 | Demo@123 |
| 👑 Admin/KVIC | 9876543214 | Demo@123 |

Enable demo seed: set `DEMO_DATA_ENABLED=true` in `.env` on first startup.

---

## 📖 API Documentation (Swagger)

```
http://localhost:8080/swagger-ui.html
```

All endpoints documented with request/response schemas, security requirements, and examples.

---

## ⚙️ Environment Variables

| Variable | Description |
|----------|-------------|
| `DB_URL` | JDBC connection string |
| `DB_USERNAME` | Database user |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | HS256 signing key (min 64 hex chars) |
| `FRONTEND_PUBLIC_URL` | Frontend URL (CORS + QR generation) |
| `UPLOAD_DIRECTORY` | Directory for QR PNGs and photos |
| `BLOCKCHAIN_MODE` | `mock` for demo |
| `IOT_MOCK_ENABLED` | `true` for simulated sensors |
| `NOTIFICATION_SMS_ENABLED` | `false` for in-app mock |
| `DEMO_DATA_ENABLED` | `true` to seed demo accounts |
| `VITE_API_BASE_URL` | (Frontend) Production backend URL |

Full reference: [DEPLOYMENT.md](DEPLOYMENT.md#environment-variables-reference)

---

## 🧪 Testing

```bash
# Run all 188 tests
cd backend
mvn clean test

# Frontend build verification
cd my-react-app
npm run build
```

Tests cover: Authentication, Beekeeper, Hive, Batch, Blockchain, Lab, QR Generation,
Public Verification, Anti-Counterfeit, IoT, Yield Prediction, Marketplace, Orders,
Reviews, Admin, Security/Ownership isolation.

---

## ⚠️ Known Limitations

- **Blockchain**: SHA-256 mock hash stored in MySQL. No real distributed ledger.
- **AI Prediction**: Rule-based heuristics based on IoT health status. Not a trained ML model.
- **Payment**: Mock flow — no real payment gateway.
- **SMS**: In-app notifications only. Mock SMS logs to console.
- **IoT**: Simulated sensor data. No real hardware integration.
- **File Storage**: Local disk. Use object storage (S3/GCS) for production scale.
- **Schema Migrations**: Hibernate `ddl-auto=update`. Migrate to Flyway for production.

Full details: [LIMITATIONS.md](LIMITATIONS.md)

---

## 📋 Demo Flow

```
ACT 1 — BEEKEEPER: Login → Create Hive → Create Batch → Blockchain Hash
ACT 2 — LAB: Login → Submit Purity (98% PURE) → Blockchain Lab Result
ACT 3 — QR: Beekeeper → Generate QR for PURE batch
ACT 4 — CUSTOMER TRUST: Scan QR → ✅ GENUINE / VERIFIED HONEY
ACT 5 — SMART HIVE: Hive Health Dashboard → IoT Alerts → Yield Prediction
ACT 6 — MARKETPLACE: Customer → Cart → Checkout → Mock Payment → Order Confirmed
ACT 7 — TRACKING: Beekeeper → Packed → Shipped | Customer → Delivered → Review
ACT 8 — ADMIN: Dashboard → Beekeepers → Batches → Analytics → Disputes
```

---

## 📁 Project Structure

```
honeychain/
├── backend/              Spring Boot application
│   ├── src/              Java source
│   ├── Dockerfile        Multi-stage Java 17 Docker image
│   └── .env.example      Backend environment template
├── my-react-app/         React frontend
│   ├── src/              React source
│   ├── Dockerfile        Multi-stage Node+Nginx Docker image
│   ├── nginx.conf        SPA routing + security headers
│   └── .env.example      Frontend environment template
├── uploads/              QR codes and uploaded files (volume-mounted)
├── docker-compose.yml    Orchestrates mysql + backend + frontend
├── .env.example          Docker Compose environment template
├── README.md             This file
├── ARCHITECTURE.md       Detailed system architecture
├── DEPLOYMENT.md         Deployment and operations guide
├── LIMITATIONS.md        Known limitations and mock integrations
└── SYSTEM_STATUS.md      Current feature implementation status
```
#   H o n e y - T r a c k a b i l i t y -  
 