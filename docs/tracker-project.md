# FinTogether — Project Progress Tracker

**Last updated:** 2026-08-15
**Current phase:** Phase 1 — Week 3 of 8
**Current sprint focus:** Refresh token infrastructure (FIN-10 next)

---

## SERVICE 1 — user-service (Phases 1–2)

### ✅ Completed

- **FIN-4:** user-service scaffolding
  - GitHub repo, Spring Boot 3 scaffold
  - Docker Compose for Postgres
  - Flyway baseline, YAML dev/prod profiles
  - Dev-profile SecurityConfig (HTTP Basic)

- **FIN-5:** POST /api/v1/auth/signup
  - SignupRequest + UserResponse DTOs (records, Bean Validation)
  - Role enum (EARNER, SAVER)
  - V1__init.sql Flyway migration (CITEXT email, TIMESTAMPTZ, CHECK on role, soft delete)
  - User entity + Auditable base class + JpaConfig
  - UserRepository (existsBy + findByEmail)
  - PasswordEncoderConfig (BCrypt cost 12, profile-agnostic)
  - 3 custom conflict exceptions + Normalizer utility
  - UserService (four-bucket failure taxonomy)
  - AuthController (201 with Location header via ServletUriComponentsBuilder)
  - GlobalExceptionHandler (RFC 7807 ProblemDetail, errors array, password redaction)
  - **Verified end-to-end:** 201 happy path, 409 duplicate, 400 validation, 400 malformed JSON, 400 invalid enum

- **FIN-9:** JWT infrastructure
  - JwtKeyConfig (dev-only, RSA 2048 keypair at startup)
  - JwtEncoderConfig (JwtEncoder + JwtDecoder beans via Nimbus)
  - JwtService (thick pattern, 7 claims: sub, iss, aud, iat, exp, jti, role)
  - JwtSecurityConfig (@Profile("!dev"), oauth2ResourceServer().jwt())

- **FIN-6:** POST /api/v1/auth/login
  - LoginRequest + LoginResponse DTOs (5 fields: accessToken, tokenType, expiresIn, userId, email)
  - AuthService separated from UserService
  - Timing attack mitigation via dummy BCrypt hash for missing users
  - Uniform "Invalid credentials" on wrong password + unknown email
  - handleBadCredentials in GlobalExceptionHandler (401)
  - **Verified end-to-end:** 200 happy path, 401 wrong password, 401 unknown email (timing-uniform), 400 missing field, 400 malformed email
  - JWT claims verified via jwt.io

- **FIN-21:** LAN + cross-machine testing setup
  - Postgres bound to 127.0.0.1 only (invisible on LAN)
  - Spring Boot bound to 0.0.0.0:8080 (LAN-facing)
  - Windows Firewall rule for port 8080 (Private profile only)
  - dev spring.security.user password moderated (fin-dev-2026-local)
  - **Verified:** Omkar hits API from his laptop via 192.168.1.7:8080

### 🚧 In progress

- None

### ⏳ Pending this phase

- **FIN-10:** refresh_tokens table (V2 Flyway migration) + persistence with rotation
- **FIN-7:** POST /api/v1/auth/refresh (rotation on use, family invalidation on reuse)
- **FIN-8:** extend GlobalExceptionHandler for refresh flow failures

---

## SERVICE 2 — expense-service (Phase 3)

### ✅ Completed
- None (Phase 3)

### 🚧 In progress
- None

### ⏳ Pending
- Full service scope defined in Phase 3

---

## CROSS-SERVICE / INFRA

- **API contracts:** N/A (single service in Phase 1)
- **Service communication:** N/A (single service in Phase 1)
- **Docker:** postgres via docker-compose, port 5432 bound to 127.0.0.1 only
- **CI/CD:** Not yet set up (Phase 4)

---

## QA — Omkar's parallel work

### ✅ Completed
- Manual verification of signup (4 scenarios) via Postman from his laptop
- Cross-machine Postman env `FinTogether Local` (base_url = LAN IPv4)

### 🚧 In progress
- **FIN-11:** Postman collection for signup with pm.test() assertions
- **FIN-12:** Jira test-case bank for signup (schema-constraint traceability)

### ⏳ Pending
- **FIN-13:** Postman collection for login (blocked by FIN-6 — now unblocked)
- **FIN-18:** Couple-linking security tests (Phase 2)
- Newman CLI + GitHub Actions integration (Phase 4)

---

## TECH DEBT / KNOWN ISSUES

- JwtSecurityConfig (@Profile("!dev")) untested — activates only when running without dev profile; no prod profile exists yet
- JWT signing key regenerates on every restart (dev only, acceptable for MVP)
- No integration tests yet (Testcontainers deferred to future sprint)
- No unit tests yet (JUnit 5 + Mockito deferred to future sprint)
- `decisions.md` file created but not yet formally committed as V1 baseline (FIN-19 open)
- SRS v1.3 has 6 minor typos identified but not yet fixed (non-blocking)

---

## DECISIONS LOG

See `docs/decisions.md`. Last major batch: FIN-6 completion (2026-08-14).

---

## FUTURE SCOPE BACKLOG (not in current 8 weeks)

### Sprint 2 candidates
- Password complexity validation (custom @ValidPassword annotation)
- Rate limiting on signup + login endpoints (Bucket4j)
- Email verification flow (SMTP integration)
- Audit log module (AUDIT_LOG table + interceptor pattern)
- Forgot password + reset password flows
- Change password flow (authenticated)

### Phase 2 (Weeks 4–5)
- Couple-linking feature — invite generation, invite acceptance, partnership entity
- Authorization rules for PRIVATE vs SHARED data (partner A cannot see partner B's PRIVATE)

### Phase 3 (Weeks 6–7)
- expense-service (personal + shared expense CRUD)
- Service-to-service JWT propagation
- JWKS endpoint (/.well-known/jwks.json) for expense-service to fetch public key
- Contract-first API design
- Newman CI integration in GitHub Actions

### Phase 4 (Week 8)
- Full Docker Compose (both services + Postgres)
- GitHub Actions pipeline (build, test, Newman, deploy-ready)
- Architecture diagram + README polish
- LinkedIn + portfolio push

### Deferred beyond 8 weeks
- UUIDv7 migration (when on Postgres 17)
- Prod JWT key loading from env vars or secrets manager
- JWT key rotation with kid header + grace period
- Refresh token abuse detection (family invalidation on reused-token attack)
- Sliding-window refresh token expiration
- Per-device refresh token tracking (user_agent column)
- Correlation ID / traceId in ProblemDetail responses
- Error catalog with stable problem URIs (replace about:blank)
- Structured audit logging with @CreatedBy / @LastModifiedBy
- Testcontainers integration tests
- MapStruct for entity↔DTO mapping
- Dashboard, budgets, month closure, reports (SRS §4.2, §4.8, §4.13, §4.14 — full Phase 1 vision)
- SIP/EMI dedicated tracking modules
- Notification module
- PDF/Excel export

---

## PHASE MILESTONES

- **Phase 1 (Weeks 1–3):** user-service auth complete — **on track**
  - Signup, login, refresh, JWT infrastructure
  - Target: end of Week 3 with FIN-10 + FIN-7 done
- **Phase 2 (Weeks 4–5):** couple-linking + refresh security hardening
- **Phase 3 (Weeks 6–7):** expense-service + service-to-service communication
- **Phase 4 (Week 8):** Dockerize, CI/CD, portfolio push