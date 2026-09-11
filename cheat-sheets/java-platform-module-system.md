---
title: "Cheat Sheet: Java Platform Module System (JPMS)"
slug: java-platform-module-system
document_type: cheat-sheet
domain: 02-java/language-core
topic_id: T-116
canonical: ../syllabus/02-java/language-core/java-platform-module-system.md
last_updated: 2026-09-11
---

# Java Platform Module System (JPMS)

**Canonical chapter:** [`syllabus/02-java/language-core/java-platform-module-system.md`](../syllabus/02-java/language-core/java-platform-module-system.md)

## Core Mental Model

A module is a package's compile-time visibility contract (`exports`) and its runtime reflective-access contract (`opens`), made explicit and independently grantable — replacing a classpath-era honor system where both were effectively all-or-nothing and unenforced.

## Essential Definitions

- **`requires`** — declares a dependency on another module.
- **`exports`** — grants compile-time/runtime visibility to a package.
- **`opens`** — grants reflective access to a package, independent of `exports`.
- **Automatic module** — a plain JAR placed on the module path without a `module-info.java`, letting legacy dependencies participate incrementally.

## Decision Table

| Need | Directive/Tool |
|---|---|
| Depend on another module | `requires <module>` |
| Grant compile-time/runtime visibility | `exports <package>` |
| Grant reflective access | `opens <package>` (optionally `to <module>`) |
| Provide a pluggable implementation without exporting it | `provides <interface> with <impl>` + `uses <interface>` |
| Find internal JDK API usage in a dependency | `jdeps --jdk-internals <jar>` |
| Temporarily restore reflective access after a break | `--add-opens <module>/<package>=<target>` |

## Common Pitfalls

- Assuming `exports` implies `opens` (or vice versa) — they are independent grants; reflection needs `opens` specifically.
- Treating `ServiceLoader`'s `uses`/`provides` as a general encapsulation bypass — it's a narrow, SPI-scoped exception, not a general hole (a direct import of the provider's internal package still fails to compile).
- Big-bang modularizing a whole legacy codebase at once instead of using automatic modules/the unnamed module to migrate incrementally.

## Interview Answer Skeleton

**30-sec:** JPMS (Java 9+) adds `module-info.java`, declaring `requires` plus two independent, real per-package grants: `exports` (visibility) and `opens` (reflection) — replacing the classpath's unenforced, all-or-nothing model.

**2-min:** Add: `ServiceLoader`'s `uses`/`provides` lets a module supply a pluggable implementation without exporting it — a narrow SPI exception, proven narrow by a direct import of the same internal package still failing to compile.

**Staff-level framing:** `jdeps --jdk-internals` and `--add-opens` are the real tools for a JDK-upgrade encapsulation break — know both the diagnostic and the mitigation, not just that "reflection might break."

## Related

- syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md
- syllabus/02-java/language-core/reflection-and-dynamic-proxies.md
