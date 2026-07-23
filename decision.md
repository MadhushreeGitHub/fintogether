1. Signup API accepts one password field. Confirmation is a client-side UX concern, not an API concern. confirmPassword is never in the API contract.
2. Signup DTO has five fields: email, phone, username, password, role.
3. Password: max 128 (BCrypt truncates at 72 bytes anyway, but capping the input at 128 prevents someone POSTing a 10MB password to slow down your hasher).
Passwords should be hashed with a strong algorithm (e.g., BCrypt, Argon2) before storing in the database.
4. Phone: max 15, must be a valid phone number format (e.g., E.164). Storage format for the phone number should be only digits i.e without any + signs.
As it may add phone numbers outside of India as well, we should not restrict it to Indian numbers only.
5. Role: enum earner and dependent. The role is assigned by the system, not the user. The user cannot choose their role during signup.
6. DB column is user_name (snake_case for multi-word); entity field is username (Java camelCase). Bridged via @Column(name=...)
7. In Auditable.java class createdBy and updatedBy typed as UUID to reference users.id. Stable identifier even if user changes email/username.
8. Auditable Sprint 1 = createdAt + updatedAt only. Add createdBy/updatedBy after login endpoint exists and SecurityContext can supply the current user's UUID via AuditorAware bean.
9. Uniqueness checks use existsByX (not findByX(...).isPresent()) — index-only scan, no heap fetch, no entity construction, clearer intent.
10. findByEmail(String) deferred to FIN-6 (login). Signup only requires existsBy checks.
11. Password encoder lives in its own PasswordEncoderConfig (no @Profile). SecurityConfig stays @Profile('dev'); prod filter chain lands in SecurityConfigProd in FIN-9.
12. Conflict exceptions carry the conflicting value for server-side logging. Client-facing error messages remain generic to prevent enumeration attacks
13. Domain exceptions extend RuntimeException. Spring's default transaction rollback triggers on unchecked exceptions; checked would require explicit @Transactional(rollbackFor=...) config on every service method.
14. Normalization lives in com.fintogether.user.util.Normalizer — static utility class. email(String) and phone(String) methods. Called at the top of every service method that reads these fields (signup, login, future password reset). DTOs remain dumb data carriers; per-service inlining rejected due to duplication."
15. "Phone normalization: strip all non-digit characters. +91 98765 43210 → 919876543210. Country code preserved."
16. "Email normalization: lowercase + trim. MADHU@Gmail.com   → madhu@gmail.com.
17. "@Transactional propagation left at default REQUIRED. Sufficient for MVP; write methods called from non-transactional controllers get their own transaction, and reuse the caller's transaction if invoked from within another transactional method.
18. Service methods do not wrap save() in try/catch. DataIntegrityViolationException bubbles to GlobalExceptionHandler for 409 translation. Wrapping in RuntimeException would break the failure taxonomy.
19. Dev-profile SecurityConfig permits /api/v1/auth/** unauthenticated. Production SecurityConfig will require authentication only on /api/v1/users/** and other protected resources; auth endpoints remain public by definition (signup, login, refresh).


## 🏁 Milestone: FIN-5 signup endpoint working end-to-end — 2026-07-22

**Tag:** v0.x.0-signup · **By:** Madhushree

- Signup endpoint working end-to-end
- UUID generated app-side; email + phone normalized
- BCrypt hash persisted; Spring Data Auditing populates `createdAt`
- Response returns sanitized `UserResponse` (no password/hash leaked)
- Why it matters: first real write path with auth-grade hashing + audit trail in place — foundation for the rest of user-service

**UserRepository extends JpaRepository (not CrudRepository) for JPA-native features (saveAndFlush, List returns) needed beyond MVP. 
Interface segregation trade-off accepted — service is Postgres+JPA committed.**
**equals/hashCode following Vlad Mihalcea's UUID pattern — constant hashCode, id-based equals with null-safe check. 
Not yet implemented in User entity, TBD before we add multi-user relationships.**

**User entity has no public setters. Creation happens via User.create(...) static factory. Domain methods
(changePassword, markDeleted) handle mutations**
 