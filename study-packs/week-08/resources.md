---
title: "Week 8 Resources"
week: 8
last_reviewed: 2026-07-29
---

# Week 8 Resources

Classified by authority: **PRIMARY**, **BOOK**, **TOOL**, **SECONDARY**.

---

## T-701 — Kafka Architecture Fundamentals

| Source | Type | Note |
|---|---|---|
| [Kafka documentation — Design](https://kafka.apache.org/documentation/#design) | PRIMARY | |
| [KIP-500 — Replace ZooKeeper with a Self-Managed Metadata Quorum (KRaft)](https://cwiki.apache.org/confluence/display/KAFKA/KIP-500) | PRIMARY | The mode this week's practice cluster runs in |

## T-702/T-705 — Producer Semantics & Partition Keys

| Source | Type | Note |
|---|---|---|
| [Kafka documentation — Producer configs](https://kafka.apache.org/documentation/#producerconfigs) | PRIMARY | |
| [KIP-98 — Exactly Once Delivery and Transactional Messaging](https://cwiki.apache.org/confluence/display/KAFKA/KIP-98+-+Exactly+Once+Delivery+and+Transactional+Messaging) | PRIMARY | Introduced idempotent + transactional producers |
| [KIP-480 — Sticky Partitioner](https://cwiki.apache.org/confluence/display/KAFKA/KIP-480%3A+Sticky+Partitioner) | PRIMARY | |
| `apache/kafka-clients` 3.7.0 via Maven Central | TOOL | Produced the real partition-routing demonstration against a live broker; see `practice/java/week-08/kafka/` |

## T-703 — Consumer Groups & Rebalancing

| Source | Type | Note |
|---|---|---|
| [Kafka documentation — Consumer configs](https://kafka.apache.org/documentation/#consumerconfigs) | PRIMARY | |
| [KIP-429 — Kafka Consumer Incremental Rebalance Protocol](https://cwiki.apache.org/confluence/display/KAFKA/KIP-429%3A+Kafka+Consumer+Incremental+Rebalance+Protocol) | PRIMARY | |

## T-704 — Delivery Semantics & Exactly-Once

| Source | Type | Note |
|---|---|---|
| [Kafka documentation — Semantics of exactly-once](https://kafka.apache.org/documentation/#semantics) | PRIMARY | |
| [KIP-98 — Exactly Once Delivery and Transactional Messaging](https://cwiki.apache.org/confluence/display/KAFKA/KIP-98+-+Exactly+Once+Delivery+and+Transactional+Messaging) | PRIMARY | |

## Runtime

| Source | Type | Note |
|---|---|---|
| `apache/kafka:3.7.0` Docker image | TOOL | Single-broker KRaft-mode cluster used for every real demo this week; see `practice/java/week-08/kafka/README.md` |

## General

| Source | Type | Note |
|---|---|---|
| `00-project/knowledge-architecture-blueprint.md` §5.8 | PRIMARY | The Kafka Semantics Cluster spec (T-702/703/704/705) this pack implements |
| `00-project/learning-roadmap.md` §4 (Week 8) | PRIMARY | Full Week 8 (Plan B) spec this pack implements |
