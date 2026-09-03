---
title: "Flashcards: DDD Strategic — Bounded Contexts and Context Mapping"
slug: ddd-strategic-bounded-contexts-and-context-mapping
document_type: flashcard-deck
domain: architecture
topic_id: T-902
canonical: ../handbook/architecture/ddd-strategic-bounded-contexts-and-context-mapping.md
last_updated: 2026-09-02
---

# Flashcards: DDD Strategic — Bounded Contexts and Context Mapping

**Canonical chapter:** [`syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md`](../syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md)

## Card: Why don't two teams need to agree on "Order"?

**Prompt:**
Two teams disagree on what "Order" means. What's the structural resolution?

**Answer:**
They don't have to agree. Each team's "Order" can be a legitimate, separate model in its own bounded context, connected by an explicit context-mapping relationship (typically an Anti-Corruption Layer) rather than forced into one shared class.

**Why it matters:**
This is the register's own named interview follow-up, designed to catch candidates who only know DDD's tactical patterns.

**Common trap:**
Proposing a single unified model as the "clean" fix — this is the exact misconception the register calls out.

**Related:**
[handbook/architecture/ddd-strategic-bounded-contexts-and-context-mapping.md](../syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md)

## Card: Conformist vs. Anti-Corruption Layer, proven

**Prompt:**
What did this chapter's real compile prove about Conformist vs. ACL?

**Answer:**
Two byte-identical consumer files — one Conformist, one ACL-protected — were compiled against an upstream type after a real field rename. The Conformist file failed with a real "cannot find symbol" error; the ACL-protected file compiled unchanged, because only its translator (a separate file, expected to change) absorbed the rename.

**Why it matters:**
It's real, measured proof rather than an assertion of the trade-off — the exact same lines of consumer code either survive or don't, based purely on which relationship pattern was chosen upstream.

**Common trap:**
Treating ACL as strictly "better" with no cost — it requires building and maintaining the translator that Conformist skips entirely.

**Related:**
[handbook/architecture/ddd-strategic-bounded-contexts-and-context-mapping.md](../syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md)

## Card: Bounded contexts as service boundaries

**Prompt:**
Why are bounded contexts the correct unit for microservice decomposition, rather than technical layers?

**Answer:**
A technical layer ("Data Access," "Validation") has no ubiquitous language of its own and doesn't correspond to a coherent domain concept a team can own end-to-end. A bounded context does — it's where a specific model and vocabulary already apply consistently, making it the natural seam for independent deployability and team ownership.

**Why it matters:**
Connects this chapter directly to microservice decomposition — a common Staff system-design follow-up.

**Common trap:**
Drawing service boundaries along layers or database tables instead of domain concepts.

**Related:**
[handbook/architecture/ddd-strategic-bounded-contexts-and-context-mapping.md](../syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md), [handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md](../syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md)
