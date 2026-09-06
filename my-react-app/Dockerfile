# ============================================================
# HoneyChain Frontend — Dockerfile
# Multi-stage build: Node build → Nginx static serve
# ============================================================

# ── Stage 1: Build ──────────────────────────────────────────
FROM node:20-alpine AS build

WORKDIR /app

# Copy dependency manifests first for layer caching
COPY package.json package-lock.json ./
RUN npm ci --prefer-offline

# Build arguments — passed at build time via docker compose build args
# IMPORTANT: This URL must be reachable from the user's browser (not a Docker service name).
# Example: --build-arg VITE_API_BASE_URL=https://api.honeychain.example.com
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

# Copy source and build
COPY . .
RUN npm run build

# ── Stage 2: Serve ──────────────────────────────────────────
FROM nginx:1.27-alpine AS serve

# Copy compiled static assets from build stage
COPY --from=build /app/dist /usr/share/nginx/html

# Copy custom Nginx config (SPA routing + security headers)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose HTTP port
EXPOSE 80

# Health check — verify Nginx is serving
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- http://localhost:80/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
