# Authentication Attack Defense: Brute Force, Credential Stuffing, MFA — Real Demo

Backs [`syllabus/12-security/authentication-attack-defense-brute-force-and-mfa.md`](../../../../syllabus/12-security/authentication-attack-defense-brute-force-and-mfa.md) (T-1310).

Pure JDK, no dependencies. No mocked clock -- `LockingLoginService` and the
lockout-expiry proof use real `System.currentTimeMillis()` and a real
`Thread.sleep`.

## Run it

```bash
mkdir -p out
javac -d out src/*.java
java -cp out BruteForceDemo
java -cp out TotpDemo
```

Real combined output captured in [`output-transcript.txt`](output-transcript.txt),
re-run twice to confirm it's reliably reproducible (only the attacker's random
guessed TOTP code differs between runs, as expected).

## What it proves

1. **Brute force, unprotected** (`UnprotectedLoginService`) — an attacker
   working through a 10-entry wordlist reaches the real password on attempt 8
   and logs in. No rate limiting, no lockout, nothing slows the attacker down.
2. **Brute force, account lockout** (`LockingLoginService`, 5 attempts / 10s
   sliding window / 3s lockout) — the identical attacker, identical wordlist,
   is locked out on attempt 5, three attempts before ever reaching the real
   password.
3. **The real trade-off, shown directly, not just asserted** — while alice's
   account is locked out from the attacker's failed attempts, alice's own
   login with her correct password is rejected too. Account lockout is a
   deliberate availability lever an attacker can pull against a legitimate
   user (`login(attemptUsername, ...)` state is keyed by the *attempted*
   username, not per-source-IP) — the reason production systems pair lockout
   with per-IP tracking, CAPTCHA, or a second factor instead of relying on it
   alone. After the real 3-second window elapses, alice logs in successfully.
4. **TOTP correctness against the official standard, not a self-check**
   (`Totp`, RFC 6238/RFC 4226, HMAC-SHA1) — verified against all 5 of RFC 6238
   Appendix B's own published test vectors. All 5 pass.
5. **Credential stuffing defeated by a second factor** (`MfaProtectedLoginService`)
   — a simulated credential-stuffing attacker has alice's *real* password
   (exactly as if leaked from an unrelated breach and reused here). Password
   alone is rejected: a guessed static code and a random 6-digit guess both
   fail. Alice, with her correct password and a real, freshly generated TOTP
   code, succeeds.

## Honest limitations

- The wordlist attack and the lockout window sizes (5 attempts / 10s / 3s)
  are shrunk for a demo that runs in a couple of seconds; production lockout
  policy typically uses longer windows. The mechanism proven is identical at
  any scale.
- `MfaProtectedLoginService` checks the *current* 30-second TOTP window only
  (no ±1-step clock-skew tolerance, which most production implementations
  add) -- omitted here because it would weaken, not strengthen, the point
  being demonstrated (a stolen password alone is insufficient).
- SHA-256 password comparison here is for demo clarity only, not a claim
  about production password storage — password hashing (bcrypt/Argon2)
  is the real applied-cryptography question, covered in
  [Applied Cryptography](../../../../syllabus/12-security/applied-cryptography-hashing-signing-tls.md).
