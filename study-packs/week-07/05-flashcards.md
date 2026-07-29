---
title: "Flashcards — Week 7"
week: 7
last_reviewed: 2026-07-29
---

# Flashcards — Week 7

14 cards, spaced repetition per `00-project/learning-roadmap.md` §0.4.

---

**1. Q: What's the correct bean lifecycle order?**
A: Constructor → `BeanPostProcessor.before` → `@PostConstruct` → `InitializingBean.afterPropertiesSet()` → custom init-method → `BeanPostProcessor.after`.

**2. Q: What mechanism creates a `@Transactional` proxy?**
A: A `BeanPostProcessor`, running before `@PostConstruct`.

**3. Q: What does `@ConditionalOnMissingBean` rely on?**
A: Auto-configuration running after application configuration.

**4. Q: Is `@Transactional` broken on an `@Async` method?**
A: No — the transaction works correctly; the caller just can't see a void method's failure without a `Future`.

**5. Q: Difference between a 401 and a 403?**
A: 401 = authentication failed; 403 = authentication succeeded but authorization failed.

**6. Q: Can a filter chain short-circuit before reaching the controller?**
A: Yes — any filter can return a response directly instead of calling the next filter.

**7. Q: Why do CORS/CSRF checks typically run before authentication?**
A: They're cheaper, more decisive rejections — fail fast before the more expensive authentication check.

**8. Q: OAuth2 vs. OIDC, in one line each?**
A: OAuth2 = authorization; OIDC = identity, built on OAuth2 via an ID token.

**9. Q: Why PKCE if you already have a client secret?**
A: They protect different attack surfaces — PKCE protects the authorization code in transit; the secret protects the token-exchange call.

**10. Q: Can a valid, non-expired JWT be revoked?**
A: Not without a stateful deny-list check — verification alone is a pure computation over the token's own bytes.

**11. Q: Two honest JWT-revocation mitigations?**
A: Short expiry + refresh tokens, or a deny-list (solves it but reintroduces a stateful lookup).

**12. Q: Why does the buggy LC 46 `permute` find ZERO results on duplicate input?**
A: A value-based "used" check means once any occurrence of a value is placed, no other occurrence of that same value can ever be placed — no permutation of full length is ever completed.

**13. Q: LC 78 vs LC 39 — the one-token difference?**
A: The recursive call passes `i + 1` (each element once) vs. `i` (element reusable).

**14. Q: Why does LC 22's open/close counter approach beat generate-then-filter?**
A: It prunes invalid branches immediately instead of generating all 2^(2n) strings and validating afterward — a real complexity difference (Catalan bound vs. exponential-then-filter).
