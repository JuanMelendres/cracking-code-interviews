---
title: "Flashcards: Supply Chain Security, SBOM, and Dependency Risk"
slug: supply-chain-security-sbom-and-dependency-risk
document_type: flashcard-deck
domain: security
topic_id: T-1306
canonical: ../handbook/security/supply-chain-security-sbom-and-dependency-risk.md
last_updated: 2026-08-06
---

# Flashcards: Supply Chain Security, SBOM, and Dependency Risk

**Canonical chapter:** [`syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md`](../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md)

## Card: Why direct-dependency-only review is insufficient

**Prompt:**
Why is reviewing only a project's directly-declared dependencies insufficient for supply-chain risk assessment?

**Answer:**
Most real vulnerabilities are found in transitive dependencies or base-image-level packages, both invisible to a direct-dependency-only review.

**Why it matters:**
The precise scope gap that justifies generating a full SBOM rather than reviewing `pom.xml`/`package.json` alone.

**Common trap:**
Reviewing only directly-declared dependencies and treating that as a complete supply-chain risk assessment.

**Related:**
[handbook/security/supply-chain-security-sbom-and-dependency-risk.md](../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md)

## Card: What the real docker scout scan found

**Prompt:**
What did the real docker scout scan against `eclipse-temurin:21-jre` find?

**Answer:**
213 packages in the SBOM; 13 vulnerabilities including one CRITICAL-severity CVE in a transitive Go package (`golang.org/x/net`) unrelated to any application code.

**Why it matters:**
Real, measured evidence that meaningful vulnerabilities live in transitive/base-image packages a team never directly chose.

**Common trap:**
Assuming a base image is safe by default because the application's own dependencies were reviewed.

**Related:**
[handbook/security/supply-chain-security-sbom-and-dependency-risk.md](../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md)

## Card: Why base-image remediation should be centrally owned

**Prompt:**
Why should base-image vulnerability remediation typically be owned centrally rather than per-service?

**Answer:**
Many services often share the same base image and therefore the identical vulnerability simultaneously — a single centrally-coordinated fix and rebuild is far more efficient than dozens of independent, duplicated remediation efforts.

**Why it matters:**
An organizational, not just technical, argument for centralizing a specific class of remediation.

**Common trap:**
Leaving each service team to independently patch a shared base-image vulnerability rather than fixing it once, centrally.

**Related:**
[handbook/security/supply-chain-security-sbom-and-dependency-risk.md](../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md)
