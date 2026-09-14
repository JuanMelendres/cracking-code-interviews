---
title: "Flashcards: OWASP Top 10 for Backend Services"
slug: owasp-top-10-for-backend-services
document_type: flashcard-deck
domain: security
topic_id: T-1301
canonical: ../syllabus/12-security/owasp-top-10-for-backend-services.md
last_updated: 2026-09-14
---

# Flashcards: OWASP Top 10 for Backend Services

**Canonical chapter:** [`syllabus/12-security/owasp-top-10-for-backend-services.md`](../syllabus/12-security/owasp-top-10-for-backend-services.md)

## Card: Is the OWASP Top 10 exhaustive

**Prompt:**
Is the OWASP Top 10 an exhaustive vulnerability checklist?

**Answer:**
No — it's a prioritization tool covering the most common/impactful categories; real risk can include business-logic flaws the list doesn't name.

**Why it matters:**
Prevents treating "we covered the Top 10" as a complete security review.

**Common trap:**
Presenting Top 10 compliance as equivalent to a comprehensive security audit.

**Related:**
[syllabus/12-security/owasp-top-10-for-backend-services.md](../syllabus/12-security/owasp-top-10-for-backend-services.md)

## Card: Why IDOR routinely passes functional testing

**Prompt:**
Why does IDOR routinely pass functional testing?

**Answer:**
Because functional tests almost always test with the correct owner's credentials; the vulnerability only appears when a *different* user's object ID is supplied, which standard happy-path tests don't do.

**Why it matters:**
Explains why IDOR is one of the most commonly shipped-to-production vulnerability classes despite passing test suites.

**Common trap:**
Assuming a passing functional test suite means access-control checks are correct.

**Related:**
[syllabus/12-security/owasp-top-10-for-backend-services.md](../syllabus/12-security/owasp-top-10-for-backend-services.md)

## Card: Why a denylist is insufficient against SSRF

**Prompt:**
Why is a denylist insufficient as an SSRF defense?

**Answer:**
It's bypassable via alternate address representations, DNS rebinding, and redirects — an allowlist validated against the resolved destination is required instead.

**Why it matters:**
The precise, structural reason a denylist-based SSRF defense fails under real attacker techniques.

**Common trap:**
Believing a blocked-hostname denylist is sufficient SSRF protection.

**Related:**
[syllabus/12-security/owasp-top-10-for-backend-services.md](../syllabus/12-security/owasp-top-10-for-backend-services.md)

## Card: SSRF's current OWASP category

**Prompt:**
Is SSRF still its own standalone OWASP Top 10 category?

**Answer:**
No — as of the 2025 edition, SSRF was folded into Broken Access Control (A01). It was a standalone category (A10) only in the 2021 edition.

**Why it matters:**
Citing "A10: SSRF" as current is a real, checkable accuracy miss once an interviewer knows the list moved on in 2025.

**Common trap:**
Assuming the OWASP Top 10 is a rarely-changing reference and citing 2021 numbers from memory.

**Related:**
[syllabus/12-security/owasp-top-10-for-backend-services.md](../syllabus/12-security/owasp-top-10-for-backend-services.md)

## Card: What's genuinely new in the 2025 edition

**Prompt:**
Of the two categories with no direct 2021 equivalent-sounding name, which one is genuinely new content, and which is just a rename?

**Answer:**
Mishandling of Exceptional Conditions (A10:2025) is genuinely new — no 2021 category covered it. Software Supply Chain Failures (A03:2025) is not new; it's a renamed, widened version of 2021's "Vulnerable and Outdated Components."

**Why it matters:**
A precise answer distinguishes real new content from a rename — interviewers probing "what's new in 2025" are listening for this distinction.

**Common trap:**
Treating both new-sounding category names as equally "brand new."

**Related:**
[syllabus/12-security/owasp-top-10-for-backend-services.md](../syllabus/12-security/owasp-top-10-for-backend-services.md)
