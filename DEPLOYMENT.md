# HoneyChain — Deployment Guide

> **Status**: Hackathon MVP Release Candidate  
> **Version**: Phase 20 Final  
> Mock integrations clearly labelled — see [LIMITATIONS.md](LIMITATIONS.md).

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Environment Variables Reference](#environment-variables-reference)
4. [Docker Deployment](#docker-deployment)
5. [Production URL Configuration](#production-url-configuration)
6. [Database Management](#database-management)
7. [File Storage (Uploads)](#file-storage-uploads)
8. [QR Code URL Configuration](#qr-code-url-configuration)
9. [CORS Configuration](#cors-configuration)
10. [Health Checks](#health-checks)
11. [Logging](#logging)
12. [HTTPS & TLS](#https--tls)
13. [Rollback Procedure](#rollback-procedure)
14. [Database Backup](#database-backup)
15. [Smoke Test Checklist](#smoke-test-checklist)
16. [Final Build Commands](#final-build-commands)

---

## Prerequisites

| Tool | Required Version | Purpose |
|------|-----------------|---------|
| Java JDK | 17+ | Backend build (local) |
| Maven | 3.9+ | Backend build (local) |
| Node.js | 20+ | Frontend build (local) |
| npm | 10+ | Frontend dependencies |
| Docker | 24+ | Container runtime |
| Docker Compose | v2+ | Multi-service orchestration |
| MySQL | 8.0 (via Docker) | Primary database |

---

## Local Development Setup

### Backend (Spring Boot)

```bash
cd backend

# Copy and populate environment variables
cp .env.example .env
# Edit .env: fill in DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET

# Run with dev profile (uses MySQL by default, H2 fallback for tests)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Backend runs at: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui.html`  
Health: `http://localhost:8080/api/health`

### Frontend (React + Vite)

```bash
cd my-react-app

# Install dependencies
npm install

# Copy env (leave VITE_API_BASE_URL empty — Vite proxy handles /api → localhost:8080)
cp .env.example .env.local

# Start dev server
npm run dev
```

Frontend runs at: `http://localhost:5173`

> **How the dev proxy works**: Vite forwards all `/api/*` requests to `http://localhost:8080`. You do NOT set `VITE_API_BASE_URL` in local dev — the proxy handles it.

### Database (MySQL local)

```bash
# Start MySQL via Docker for local dev
docker run -d \
  --name honeychain-mysql-dev \
  -e MYSQL_ROOT_PASSWORD=yourpassword \
  -e MYSQL_DATABASE=honeychain_db \
  -p 3306:3306 \
  mysql:8.0
```

Schema is created automatically via `spring.jpa.hibernate.ddl-auto=update` on first startup.

---

## Environment Variables Reference

### Backend

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DB_URL` | ✅ | — | Full JDBC connection string |
| `DB_USERNAME` | ✅ | — | Database username |
| `DB_PASSWORD` | ✅ | — | Database password |
| `JWT_SECRET` | ✅ | — | HS256 signing key (min 64 hex chars) |
| `JWT_EXPIRATION_MS` | — | `86400000` | Token TTL in milliseconds (24h) |
| `FRONTEND_PUBLIC_URL` | ✅ | `http://localhost:5173` | Frontend URL (CORS + QR generation) |
| `UPLOAD_DIRECTORY` | — | `uploads` | Directory for QR PNGs and photos |
| `BLOCKCHAIN_MODE` | — | `mock` | `mock` = simulated; replace for real chain |
| `IOT_MOCK_ENABLED` | — | `true` | `false` requires real sensor hardware |
| `NOTIFICATION_SMS_ENABLED` | — | `false` | `true` requires real SMS provider |
| `DEMO_DATA_ENABLED` | — | `false` | Seed demo accounts on startup |
| `AI_PREDICTION_MAX_AGE` | — | `24` | Hours before yield prediction expires |
| `VERIFICATION_FINGERPRINT_SALT` | — | `honeychain-secure-salt-2026` | Anti-counterfeit salt |
| `SPRING_PROFILES_ACTIVE` | — | `dev` | `dev` or `prod` |

### Frontend (Vite build-time)

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `VITE_API_BASE_URL` | Production only | (empty = use proxy) | Public backend URL visible to the browser |

> ⚠️ **Critical**: `VITE_API_BASE_URL` is baked into the JS bundle at build time. Set it to the URL users' browsers can reach — **not** a Docker service name.

---

## Docker Deployment

### Quick Start

```bash
# 1. Copy and fill in environment variables
cp .env.example .env
# Edit .env — fill in all required values (passwords, JWT_SECRET, URLs)

# 2. Build all Docker images
docker compose build

# 3. Start all services in background
docker compose up -d

# 4. Check service status
docker compose ps

# 5. Follow startup logs
docker compose logs -f backend
```

### Expected Service Status

```
NAME                    STATUS                   PORTS
honeychain-mysql        running (healthy)        0.0.0.0:3306->3306/tcp
honeychain-backend      running (healthy)        0.0.0.0:8080->8080/tcp
honeychain-frontend     running (healthy)        0.0.0.0:80->80/tcp
```

### Service Startup Order

```
mysql (health: mysqladmin ping)
  ↓ (waits until healthy)
backend (health: GET /api/health)
  ↓ (waits until healthy)
frontend (Nginx serving static files)
```

MySQL may take 30–60 seconds to initialize on first boot. The backend is configured with `start_period: 60s` and 5 retries to tolerate this.

### Access Points (Local Docker)

| Service | URL |
|---------|-----|
| Frontend | http://localhost |
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Health Check | http://localhost:8080/api/health |
| MySQL | localhost:3306 (dev only) |

### Stopping Services

```bash
# Stop and remove containers (DATA IS PRESERVED in mysql_data volume)
docker compose down

# View logs
docker compose logs backend
docker compose logs frontend
docker compose logs mysql

# Restart a single service
docker compose restart backend
```

> ⚠️ **NEVER run `docker compose down -v`** during normal operations. The `-v` flag deletes the `mysql_data` volume and all database data permanently.

---

## Production URL Configuration

For a real deployed environment (beyond hackathon demo):

| Resource | Example URL |
|----------|-------------|
| Frontend | `https://honeychain.example.com` |
| Backend API | `https://api.honeychain.example.com` |
| QR Verification | `https://honeychain.example.com/verify/HC-2026-XXXX` |
| Swagger | `https://api.honeychain.example.com/swagger-ui.html` |

Update `.env`:
```bash
FRONTEND_PUBLIC_URL=https://honeychain.example.com
VITE_API_BASE_URL=https://api.honeychain.example.com
```

Then rebuild: `docker compose build frontend && docker compose up -d frontend`

---

## Database Management

### Schema Initialization

HoneyChain uses `spring.jpa.hibernate.ddl-auto=update`. On first startup:
- Spring Boot auto-creates all tables from JPA entity definitions
- Subsequent startups apply only schema deltas

### Seed Data

Set `DEMO_DATA_ENABLED=true` to seed demo accounts on startup (idempotent — safe to restart).

Demo accounts (⚠️ **DEMO ONLY — do not use in real production**):

| Role | Phone | Password | Purpose |
|------|-------|----------|---------|
| Beekeeper | 9876543210 | Demo@123 | Hive & batch management |
| Customer | 9876543211 | Demo@123 | Marketplace & orders |
| Lab | 9876543212 | Demo@123 | Purity testing |
| Admin/KVIC | 9876543214 | Demo@123 | Admin dashboard |

> ⚠️ **Change or disable these accounts before deploying publicly.**

---

## File Storage (Uploads)

QR codes and batch photos are stored in the `uploads/` directory, mounted as:

```yaml
# docker-compose.yml
volumes:
  - ./uploads:/app/uploads
```

On the host machine, files are at `./uploads/qr/<batchId>.png`.

**Ensure this directory exists before starting Docker:**
```bash
mkdir -p uploads/qr
```

If this volume mount is removed, uploaded files will be lost when the container is recreated.

---

## QR Code URL Configuration

The QR code embedded in each PNG contains:

```
{FRONTEND_PUBLIC_URL}/verify/{batchId}
```

Example:
- Local: `http://localhost/verify/HC-2026-AB12CD34`
- Deployed: `https://honeychain.example.com/verify/HC-2026-AB12CD34`

**After changing `FRONTEND_PUBLIC_URL`**, existing QR codes are not updated. Re-generate QR codes for existing batches if the URL changes.

**Critical release test**: Scan the QR with a real phone. It must open the public verification page without login.

---

## CORS Configuration

CORS is configured to allow only the `FRONTEND_PUBLIC_URL` origin:

```properties
cors.allowed-origins=${FRONTEND_PUBLIC_URL}
```

- Do **not** use `allowedOrigins("*")` with credentialed requests
- If the frontend URL changes, update `FRONTEND_PUBLIC_URL` and restart the backend

---

## Health Checks

### Backend Health Endpoint

```
GET /api/health
```

Response:
```json
{
  "success": true,
  "message": "HoneyChain backend is running"
}
```

Does **not** expose: database credentials, JWT secret, internal paths, or environment variables.

### Docker Healthchecks

All three services have Docker `HEALTHCHECK` configured:

| Service | Check Command | Start Period |
|---------|--------------|--------------|
| mysql | `mysqladmin ping` | 30s |
| backend | `GET /api/health` | 60s |
| frontend | `GET http://localhost/` | 10s |

---

## Logging

Containers write to stdout/stderr. View with:

```bash
docker compose logs backend    # Backend logs
docker compose logs frontend   # Nginx access logs
docker compose logs mysql      # MySQL logs
```

**Never logged** (enforced by application):
- Passwords or JWT secrets
- OTP values
- Payment credentials
- Full sensitive user data
- Database connection strings

SMS mock logs mask phone numbers (shows `+91****7890` pattern).

---

## HTTPS & TLS

For production deployments:
1. Place a reverse proxy (Nginx, Traefik, AWS ALB) in front
2. Terminate TLS at the reverse proxy
3. Set `FRONTEND_PUBLIC_URL=https://honeychain.example.com`
4. Set `VITE_API_BASE_URL=https://api.honeychain.example.com`

**Do not commit** TLS private keys or certificates to the repository.

---

## Rollback Procedure

### Application Rollback

1. Note the current image tag: `docker compose images`
2. Update `docker-compose.yml` to reference the previous image version or rebuild from a previous git tag
3. `docker compose up -d --no-build` (if using pre-built images) or `docker compose build && docker compose up -d`
4. Verify health: `docker compose ps`

### Database Rollback

Since HoneyChain uses Hibernate `ddl-auto=update` (not Flyway/Liquibase):
- Schema additions are non-destructive (new columns, new tables)
- Rollback to a previous schema version is **manual** — back up first
- For future production: migrate to Flyway for versioned, reversible migrations

---

## Database Backup

> ⚠️ HoneyChain does not implement automated backups. Configure backups at the infrastructure level.

**Recommended backup strategy:**

```bash
# Manual backup
docker exec honeychain-mysql \
  mysqldump -u root --password=$MYSQL_ROOT_PASSWORD honeychain_db \
  > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore from backup
docker exec -i honeychain-mysql \
  mysql -u root --password=$MYSQL_ROOT_PASSWORD honeychain_db \
  < backup_20260904_000000.sql
```

**Production recommendations:**
- Schedule daily backups with retention (e.g., 30 days)
- Test restore process regularly
- Store backups off-host (e.g., S3, GCS)
- Encrypt backup files at rest

---

## Smoke Test Checklist

After deployment, verify:

- [ ] `GET /api/health` → `{"success":true, ...}`
- [ ] Frontend loads at `/`
- [ ] Login with demo beekeeper account
- [ ] Create a honey batch → batch ID `HC-2026-XXXX` generated
- [ ] Blockchain hash assigned
- [ ] Send for lab testing
- [ ] Lab login → submit purity result (PURE, 98%)
- [ ] Beekeeper → Generate QR
- [ ] **Scan QR with a real phone** → opens `/verify/HC-2026-XXXX` without login
- [ ] Public verification shows: ✅ Genuine, 98% Pure, Blockchain Verified
- [ ] Customer marketplace → add to cart → checkout → mock payment → order confirmed
- [ ] Beekeeper → mark order Packed → Shipped
- [ ] Customer → mark Delivered → leave review
- [ ] Admin dashboard loads with correct data
- [ ] Offline batch creation (mobile with airplane mode)
- [ ] Language switch (English / Tamil / Hindi) persists on reload

---

## Final Build Commands

```bash
# ── Backend ───────────────────────────────────────────────
cd backend
mvn clean test              # Run all 188 tests
mvn package                 # Build JAR (target/honeychain-backend-*.jar)

# ── Frontend ──────────────────────────────────────────────
cd my-react-app
npm install
npm run build               # Build dist/ (Vite production bundle)

# ── Docker ────────────────────────────────────────────────
cd ..                       # Project root
docker compose build        # Build all images
docker compose up -d        # Start all services
docker compose ps           # Verify status

# ── Verification ──────────────────────────────────────────
docker compose logs backend         # Check startup
docker compose logs mysql           # Check DB initialization
curl http://localhost:8080/api/health  # Backend health
curl http://localhost/               # Frontend loads

# ── Shutdown (preserves data) ─────────────────────────────
docker compose down                  # Stop containers, keep volumes

# ── Shutdown (DESTROYS database) ──────────────────────────
# docker compose down -v            # ⚠️ Only use to wipe everything and start fresh
```
