---
title: "Flashcards: Schema Registry and Compatibility Evolution"
slug: schema-registry-and-compatibility-evolution
document_type: flashcard-deck
domain: kafka
topic_id: T-708
canonical: ../handbook/kafka/schema-registry-and-compatibility-evolution.md
last_updated: 2026-09-02
---

# Flashcards: Schema Registry and Compatibility Evolution

**Canonical chapter:** [`syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md`](../syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md)

## Card: The BACKWARD add-vs-remove asymmetry

**Prompt:**
Under BACKWARD compatibility, which is safe: removing a field, or adding one without a default?

**Answer:**
Removing a field is safe (unconditionally). Adding a field without a default is not — it will be rejected.

**Why it matters:**
This is the single most commonly inverted rule on this topic; real, executed evidence (a real HTTP 409 vs. 200) backs this exact result.

**Common trap:**
Assuming removal is the riskier change — it's the opposite under BACKWARD.

**Related:**
[Core Concepts](../syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md#core-concepts)

## Card: Compatibility mode = deploy-order promise

**Prompt:**
What question should decide BACKWARD vs. FORWARD for a topic?

**Answer:**
Which side deploys first: if consumers typically deploy after producers, BACKWARD; if producers need to move ahead of some lagging consumers, FORWARD.

**Why it matters:**
Turns an easily-memorized-wrong rule into something reasoned from the real deployment scenario.

**Common trap:**
Picking a mode by habit rather than by the actual deploy-order constraint.

**Related:**
[Mental Model](../syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md#mental-model)

## Card: What the registry actually prevents

**Prompt:**
What real failure does a Schema Registry's compatibility check prevent, mechanically?

**Answer:**
A consumer's Avro schema resolution failing at decode time — a required reader field with no default and no corresponding writer data throws a real, immediate exception (`AvroTypeException` in this chapter's own evidence).

**Why it matters:**
Ties the abstract "compatibility" concept to the concrete byte-level mechanism it's protecting.

**Common trap:**
Describing the registry as validating data content rather than validating a schema *change* against a resolution guarantee.

**Related:**
[Internal Implementation](../syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md#internal-implementation)
