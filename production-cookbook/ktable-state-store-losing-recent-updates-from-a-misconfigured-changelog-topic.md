---
title: "KTable State Store Losing Recent Updates From a Misconfigured Changelog Topic"
document_type: production-cookbook-entry
domain: kafka
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md
source: syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md#production-scenarios
---

# KTable State Store Losing Recent Updates From a Misconfigured Changelog Topic

## Context

A Kafka Streams application instance restarts and rebuilds its local state store from its changelog topic.

## Symptoms

Some recently-updated keys come back with stale values — not the last value the application itself wrote.

## Impact

Application state silently diverges from what was actually written most recently, for an unpredictable subset of keys.

## Initial Hypotheses

- A bug in the application's own aggregation logic — checked, the aggregation logic itself is correct and was verified independently.
- A Kafka Streams library bug — checked, extremely unlikely for something this fundamental, and no matching known issue.
- The changelog topic's `cleanup.policy` was accidentally overridden to `delete` (e.g., by an external topic-config-management tool applying an organization-wide default) rather than the `compact` Kafka Streams itself requests when creating the topic — correct.

## Evidence

`kafka-topics.sh --describe` on the specific changelog topic directly reveals its actual `cleanup.policy`, diverging from the expected `compact` value.

## Investigation Timeline

1. State-store restore after a broker restart returns stale values for a subset of keys.
2. Application-bug and library-bug hypotheses ruled out via independent logic verification and known-issue search.
3. `kafka-topics.sh --describe` run against the changelog topic, revealing `cleanup.policy=delete` instead of the expected `compact`.

## Root Cause

An external, organization-wide topic-config-management tool applied a `delete` retention default to a Kafka Streams-internal changelog topic, overriding the `compact` policy the library itself requests and depends on for correct state recovery.

## Immediate Mitigation

Correct the topic's `cleanup.policy` back to `compact` for the specific changelog topic.

## Permanent Fix

Exempt internal Kafka Streams topics (identifiable by naming convention, typically `<application.id>-*-changelog`) from any organization-wide topic-config-management automation that applies retention defaults.

## Alternatives Considered

Manually re-seeding the state store from application-level source-of-truth data — a real, working fallback for this specific incident, but not a fix for the root cause, which would recur on the next broker restart.

## Trade-offs

Exempting internal topics from centralized config management is a real, if narrow, exception to an organization's "everything managed consistently" policy — accepted, since these topics' correct configuration is a library-level invariant, not a team-level operational choice.

## Prevention

Alert on any drift between a Kafka Streams internal changelog topic's actual `cleanup.policy` and the `compact` value the library itself always requests.

## Monitoring and Alerts

- A scheduled `kafka-topics.sh --describe` audit (or equivalent API check) across all `*-changelog` topics, alerting on any `cleanup.policy` other than `compact`.
- Config-management tooling explicitly excluding topics matching the Kafka Streams internal-topic naming convention from any blanket retention-policy rule.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a Kafka Streams application's state store returned stale values for some keys after a broker restart.
- **Task:** find why a restore from the changelog topic didn't reflect the most recent writes.
- **Action:** described the changelog topic directly and found its `cleanup.policy` had drifted from `compact` to `delete`, applied by an external config-management tool.
- **Result:** restored the correct policy and exempted internal Kafka Streams topics from the organization-wide automation going forward.

## Staff-Level Discussion

A compacted topic's guarantee only holds if `cleanup.policy=compact` is actually, currently true — a real, externally-caused configuration drift can silently break an application's state-recovery guarantee without any application-code change at all. The organizational lesson is that library-managed infrastructure (topics Kafka Streams itself creates and depends on) needs to be explicitly protected from generic, organization-wide automation that has no awareness of that dependency.

## Related Handbook Chapters

- [Retention, Log Compaction, and Tiered Storage](../syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md) — the canonical `cleanup.policy=compact` mechanics behind this incident.
