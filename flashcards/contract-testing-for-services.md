---
title: "Flashcards: Contract Testing for Services"
slug: contract-testing-for-services
document_type: flashcard-deck
domain: testing
topic_id: T-1105
canonical: ../handbook/testing/contract-testing-for-services.md
last_updated: 2026-08-06
---

# Flashcards: Contract Testing for Services

**Canonical chapter:** [`syllabus/08-testing/contract-testing-for-services.md`](../syllabus/08-testing/contract-testing-for-services.md)

## Card: Who authors a consumer-driven contract

**Prompt:**
Who should author a consumer-driven contract — the provider or the consumer?

**Answer:**
The consumer — only the consumer knows what it actually, specifically depends on; provider-authored contracts drift toward the provider's full spec rather than real usage.

**Why it matters:**
The defining distinction of "consumer-driven" versus a provider-authored API spec.

**Common trap:**
Letting the provider team author the contract, reintroducing the over-broad-spec problem the pattern exists to avoid.

**Related:**
[handbook/testing/contract-testing-for-services.md](../syllabus/08-testing/contract-testing-for-services.md)

## Card: What contract verification runs against

**Prompt:**
Does contract verification run against a mock of the provider, or the provider's real implementation?

**Answer:**
The provider's real, live implementation — this is what gives contract testing integration-test-level confidence.

**Why it matters:**
The mechanism that lets contract testing catch a real breaking change before it ships, not just a documentation mismatch.

**Common trap:**
Assuming contract verification is satisfied by checking a JSON schema alone, without running the real provider.

**Related:**
[handbook/testing/contract-testing-for-services.md](../syllabus/08-testing/contract-testing-for-services.md)

## Card: The main ongoing risk to contract testing's value

**Prompt:**
What's the main ongoing risk to a contract-testing practice's long-term value?

**Answer:**
Contract staleness — a contract no longer reflecting the consumer's real current usage, producing false positives or false negatives.

**Why it matters:**
Explains why contract testing needs ongoing maintenance discipline, not a one-time setup.

**Common trap:**
Writing the contract once at integration time and never revisiting it as the consumer's actual usage evolves.

**Related:**
[handbook/testing/contract-testing-for-services.md](../syllabus/08-testing/contract-testing-for-services.md)
