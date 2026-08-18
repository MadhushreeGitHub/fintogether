LAST UPDATED: 2026-07-24

Spring Boot 3 concepts practised:
- Profile-scoped @Configuration classes (dev vs non-dev)
- Spring Data JPA repositories (JpaRepository, existsBy naming)
- Spring Data Auditing (@EnableJpaAuditing, AuditingEntityListener,
  @CreatedDate / @LastModifiedDate on @MappedSuperclass)
- Bean Validation via @Valid + @RequestBody on controller parameters
- @RestControllerAdvice + @ExceptionHandler with RFC 7807 ProblemDetail
- @Transactional at method level with default REQUIRED propagation
- Constructor injection via Lombok @RequiredArgsConstructor + final fields
- Spring Security 6 filter chain via SecurityFilterChain @Bean
- spring-security-oauth2-jose (JwtEncoder, JwtDecoder, JwtClaimsSet,
  NimbusJwtEncoder, NimbusJwtDecoder)
- ResponseEntity.created(URI).body(...) idiom for 201 responses
- ServletUriComponentsBuilder.fromCurrentContextPath() for robust
  Location headers

Microservices patterns implemented:
- RS256 asymmetric JWT signing chosen for future multi-service verification
- Public/private key split, JWKS endpoint plan documented for Phase 3
- Persisted refresh token strategy with rotation-on-use (design; code
  lands in FIN-10)
- Uniform 401 + timing-attack mitigation via always-run BCrypt against dummy hash. Prevents email enumeration via side-channel

Java 21 features used (and why there):
- Records for DTOs (SignupRequest, UserResponse, FieldErrorDetail) —
  immutability, generated equals/hashCode, no boilerplate
- Pattern matching for instanceof discussed for exception logging
  (deferred, would land in GlobalExceptionHandler enhancement)
- .toList() over Collectors.toList() for immutable list return

Database design decisions made + trade-offs considered:
- UUID over BIGINT for user id — no ID collision across environments,
  doesn't leak row count; trade-off: 16 bytes + worse index locality
  than BIGINT
- App-side UUID generation via @GeneratedValue(strategy=UUID) —
  ID available before insert, DB-agnostic; trade-off: coordination
  burden if multiple writers (not our case)
- CITEXT for email — case-insensitive uniqueness at DB layer;
  trade-off: Postgres-coupled type
- VARCHAR(60) for password_hash — BCrypt exact size, room for Argon2
  migration later
- CHECK constraint on role — DB-level defense against corrupt writes;
  trade-off: schema migration required when enum grows
- TIMESTAMPTZ everywhere + Instant on Java side — UTC-normalized,
  unambiguous; LocalDateTime banned in domain layer
- Soft delete via deleted_at + @SQLDelete + @SQLRestriction — no
  manual filtering, defense against forgetting the WHERE clause
- No public setters on entity, Lombok @Builder + protected no-args
  constructor — prevents arbitrary mutation of persisted state
- equals/hashCode per Vlad Mihalcea (constant hash, id-based equals
  with null-safe check) — safe when entity is used in Set before persist
- user_name in DB + username in Java, bridged via @Column(name=...) —
  snake_case DB, camelCase Java; standard industry pattern
- Flyway migrations are immutable once applied — during dev-pre-launch,
  wipe volume; post-launch, forward migrations only
- existsBy over findBy...isPresent() for uniqueness checks — index-only
  scan, no heap fetch, no entity construction, clearer intent

System design decisions made + trade-offs considered:
- Started with user-service directly (not monolith-extraction path);
  trade-off documented — less refactoring experience, mitigated by
  Phase 3 contract-first work
- Signup creates account only, no auto-login — separation of concerns,
  easier to layer email verification later
- Four-bucket failure taxonomy (client bad data / valid data + state
  conflict / server infra failure / race conditions) drives all
  @ExceptionHandler methods
- Application-level existsBy + DB-level UNIQUE constraint = defense in
  depth; only the constraint prevents the TOCTOU race, existsBy is fast
  rejection + clean error for the common case
- RS256 over HS256 — asymmetric enables downstream services to verify
  without holding the signing secret; matches microservices vision
- Thick JwtService pattern — encapsulates claim schema, expiration
  policy, algorithm choice; callers pass User, service builds everything
- JWT signing key: dev generates at startup (acceptable for local),
  prod will load from env vars / secrets manager (profile-scoped
  @Configuration keeps downstream code profile-agnostic)
- Access token 15 min, refresh token 7 days, refresh persisted with
  rotation — financial-app-appropriate risk profile
- Static utility class for normalization (email lowercase+trim,
  phone digit-strip) — reusable, testable, DTOs stay dumb data carriers
- Password confirmation is client-side UX only, never in API contract
  — reduces password-handling surface area
- Entity → DTO mapping inlined in service for MVP; will extract to
  MapStruct when duplication across services appears
- Sensitive field redaction in validation error responses — password
  rejected value never leaked to JSON, logs, or network traces
- ProblemDetail (RFC 7807) as error response standard — structured
  errors array attached via setProperty("errors", ...)
- Domain exceptions extend RuntimeException — Spring auto-rollback,
  no throws pollution on service signatures
- No public setters on User + @Builder access control — prevents
  partial state, but builder-open trade-off accepted for MVP
- JwtEncoder holds full RSAKey; JwtDecoder needs only public key.
  Phase 3 expense-service will consume public key via JWKS endpoint.

Gaps identified — topics to revisit:
- Postgres index types (B-tree, hash, GIN, partial indexes) — did not
  add indexes beyond auto-generated UNIQUE + PK; interview-relevant
- Transaction isolation levels — used default READ_COMMITTED, chose
  correctly but need to verify SERIALIZABLE and how Postgres implements it
- EXPLAIN ANALYZE — no query performance analysis yet
- JWT key rotation strategy — deferred but interview-relevant
- When to reach for @Version optimistic locking (not needed yet, will
  hit it when updates arrive)
- Testcontainers integration tests
- Static factory methods on entities as an alternative to @Builder
- MapStruct vs. hand-written mappers