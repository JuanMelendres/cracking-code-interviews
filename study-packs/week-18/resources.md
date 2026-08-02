---
title: "Week 18 Resources"
week: 18
document_type: study-pack-resources
status: draft
last_reviewed: 2026-08-02
---

# Week 18 Resources

| Source | Type | Notes |
|---|---|---|
| [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) | PRIMARY | Platform/Jupiter/Vintage architecture, extension model, parameterized/dynamic tests |
| [Pact — Contract Testing documentation](https://docs.pact.io/) | PRIMARY | Consumer-driven contract testing reference |
| [PIT (PITest) — Mutation Testing for Java](https://pitest.org/) | PRIMARY | The standard JVM mutation-testing tool (not used directly this week — a hand-rolled mutant demonstrates the same principle without the dependency) |
| [jqwik — Property-Based Testing for Java](https://jqwik.net/) | PRIMARY | The standard JVM property-based-testing framework (not used directly this week — a hand-rolled property test demonstrates the same principle without the dependency) |
| [RFC 9110 — HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110) | PRIMARY | Background for the load-testing demo's HTTP request/response model |
| `handbook/performance/percentiles-tail-latency-and-coordinated-omission.md` (T-1204) | INTERNAL | Owns the percentile-mathematics and coordinated-omission depth this week's load-testing chapter deliberately doesn't duplicate |
| OpenJDK 21.0.12 | TOOL | Produced all Java demonstrations this week: `practice/java/week-18/` |
| JUnit Jupiter/Platform 5.12.2 / 1.12.2, Mockito 5.17.0 | TOOL | Resolved from the local Maven repository; ran via a small programmatic `Launcher`-API console runner, no build tool required |
