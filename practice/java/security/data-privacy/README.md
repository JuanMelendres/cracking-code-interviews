# Data Privacy Techniques — Real Demo

Backs [`syllabus/12-security/data-privacy-pii-handling-and-retention.md`](../../../../syllabus/12-security/data-privacy-pii-handling-and-retention.md) (T-2437).

Pure JDK (`javax.crypto`), no dependencies. Nothing here is legal advice; it demonstrates the *engineering* techniques a privacy design rests on.

## Run it

```bash
mkdir -p out
javac -d out src/DataPrivacyDemo.java
java -cp out DataPrivacyDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **Pseudonymisation** — records carry a stable `user_ref` while the mapping to the real identifier lives in one controlled system. Stable across calls, so joins still work. The chapter's point, restated in the demo output: pseudonymised data is *still personal data*, because re-identification is possible by design.
- **An unsalted hash is not anonymisation** — the demo recovers the original address by guessing against the stored SHA-256, printing `MATCH -- identity recovered`. The value space of email addresses is small enough to enumerate or to test against a known customer list.
- **Crypto-shredding**, the headline. Two ciphertexts are written to systems with different deletion capabilities (a backup and an archive), both readable. The erasure request destroys **the subject's key**, not the copies — after which both decryptions fail and the ciphertext, still physically present, is permanently unreadable. This is the one design that reconciles "delete my data" with "we keep an immutable audit log," and its real cost is that key management becomes availability-critical: losing a key is indistinguishable from erasing a subject.
- **A summary table** of what each technique gives you and whether the result is still personal data — only aggregation/k-anonymity leaves the regulation's scope, and it costs row-level detail.
