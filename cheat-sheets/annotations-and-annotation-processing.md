---
title: "Cheat Sheet: Annotations and Annotation Processing"
slug: annotations-and-annotation-processing
document_type: cheat-sheet
domain: java-core
topic_id: T-112
canonical: ../handbook/java-core/annotations-and-annotation-processing.md
last_updated: 2026-09-02
---

# Annotations and Annotation Processing

**Canonical chapter:** [`syllabus/02-java/language-core/annotations-and-annotation-processing.md`](../syllabus/02-java/language-core/annotations-and-annotation-processing.md)

## Core Mental Model

`@Retention` decides how far an annotation survives past compilation — source, class file, or runtime — and each stage genuinely discards what the next doesn't need. Most framework "magic" (`@Column`, `@Autowired`, `@Test`) is just reflection reading `RUNTIME`-retained metadata and building behavior from it — there's no deeper mechanism than "the annotation survived long enough for reflection to find it."

## Essential Definitions

- **`RetentionPolicy.SOURCE`** — discarded entirely after compilation; never reaches the `.class` file (e.g., `@Override`).
- **`RetentionPolicy.CLASS`** (the default) — present in the `.class` file's `RuntimeInvisibleAnnotations` attribute, but invisible to `getAnnotations()`/reflection.
- **`RetentionPolicy.RUNTIME`** — present in `RuntimeVisibleAnnotations`, the only attribute reflection actually reads.
- **`@Inherited`** — propagates a class-level annotation from a superclass to subclasses via `extends` only; never through `implements`, even for an `@Inherited`-marked interface annotation.

## Decision Table

| Question | Answer |
|---|---|
| Does a framework need to read this annotation via reflection at runtime? | Must declare `@Retention(RetentionPolicy.RUNTIME)` explicitly — never rely on the default |
| Is the annotation purely a compiler hint (`@Override`, `@SuppressWarnings`)? | `SOURCE` retention is correct and cheapest |
| Does the annotation need to propagate to subtypes automatically? | Works via class `extends` only — never assume it reaches `implements`-ing classes |
| Is high-volume runtime reflection cost a concern? | Consider compile-time annotation processing (`javax.annotation.processing`) instead |

## Common Pitfalls

- Forgetting `@Retention(RetentionPolicy.RUNTIME)` — the default is `CLASS`, invisible to reflection, with zero compiler warning.
- Assuming `@Inherited` propagates through interface implementation — verified `false`, even for an `@Inherited`-marked interface annotation.
- Confusing runtime reflection-based processing with compile-time annotation processing — genuinely different mechanisms with different costs/capabilities.
- Assuming no explicit `@Retention` behaves like `RUNTIME` — the real default is `CLASS`.

## Interview Answer Skeleton

**30-sec:** `@Retention` controls how long an annotation survives: `SOURCE` never reaches bytecode; `CLASS` (default) is in the bytecode but invisible to reflection; `RUNTIME` is genuinely readable. Framework magic is reflection reading `RUNTIME`-retained annotations. `@Inherited` only works through class `extends`, never interfaces.

**2-min:** Add the real, verified bytecode disassembly proof (`javap` shows `RuntimeInvisibleAnnotations` vs `RuntimeVisibleAnnotations`) and the working reflection-based mini-ORM example: scan fields, read `@Column` values, build SQL dynamically — no code generation involved.

**Whiteboard:** Source → `javac` → three branches by retention policy → discarded / invisible bytecode / reflection-visible. Annotate the `RUNTIME` branch "the only path any framework's reflection can see."

**Staff-level framing:** Generalize to any multi-stage pipeline that progressively discards information (compilation stages, data pipelines, API versioning) — a downstream consumer expecting information an upstream stage silently dropped is a recurring, often-silent failure mode; design for loud failure at each boundary.

## Common Interview Traps

- A custom validation annotation with no explicit `@Retention` silently never fires at runtime — no exception, nothing in the logs — because the default `CLASS` retention is invisible to the exact reflection code meant to read it.
- Reflection-based scanning code that finds zero matches should assert/log/throw rather than silently do nothing.

## Related

- `syllabus/02-java/language-core/classloaders-and-class-initialization.md`
- `syllabus/02-java/language-core/enums-enummap-and-enumset.md`
- `syllabus/02-java/language-core/reflection-and-dynamic-proxies.md`
