#!/usr/bin/env python3
"""Fixes a curated, high-confidence list of link-text/title-drift instances
found by scripts/audit_link_text_drift.py -- cases where link text was
almost certainly written to match a target chapter's title at the time,
and the target was later retitled without the link text being updated.

Scope, per explicit user decision (2026-09-15): only fix
  (a) links whose text is literally the raw .md filename (a clear
      formatting bug, not a style choice), and
  (b) link-text/title pairs recurring 3+ times across the repo (strong
      evidence the link text WAS the real title before a rename), plus
  (c) a few sub-3x cases where a domain name (Spring/Kafka) is entirely
      missing from the link text, which risks genuine reader confusion.
Excludes intentional navigational shorthand ("Learning Path: X" -> "X",
"X" -> "X -- Domain Index"), section-reference additions ("SS15"), and
single-occurrence subtitle/parenthetical omissions that read as
deliberate, concise style rather than drift.

Each replacement is an EXACT match on the full bracket content of a
Markdown link (`[OLD TEXT](`), so it cannot accidentally touch prose
text outside a link.

Run: python3 scripts/fix_link_text_drift.py
"""
from __future__ import annotations

import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SYLLABUS = os.path.join(ROOT, "syllabus")

# (old link text, new link text)
REPLACEMENTS: list[tuple[str, str]] = [
    ("Strangler Fig and Migration Patterns", "Strangler Fig, Anti-Corruption Layer, and Migration Patterns"),
    ("Spring Cache Abstraction", "Spring Cache Abstraction and Pitfalls"),
    ("Design Patterns Applied", "Design Patterns Applied (GoF in Production)"),
    ("JIT Tiered Compilation and Deoptimization", "JIT: Tiered Compilation, Inlining, and Deoptimization"),
    ("Delivery Semantics and Exactly-Once Processing", "Kafka Delivery Semantics and Exactly-Once Processing"),
    ("Delivery Semantics and Exactly-Once", "Kafka Delivery Semantics and Exactly-Once Processing"),
    ("Java Memory Model", "Java Memory Model and volatile"),
    ("Distributed Transactions — Saga and Outbox", "Distributed Transactions: Saga, Outbox, and 2PC"),
    ("Distributed Transactions, Saga, and the Outbox Pattern", "Distributed Transactions: Saga, Outbox, and 2PC"),
    ("Polymorphism and Dynamic Dispatch", "Polymorphism and Dynamic Dispatch Mechanics"),
    ("Test Strategy and Test Doubles", "Test Strategy, the Pyramid, and Test Doubles"),
    ("JPA Entity Lifecycle and the N+1 Problem", "JPA Entity Lifecycle, the Persistence Context, and the N+1 Problem"),
    ("Time-Boxing and Mid-Round Changes", "System Design Interview Delivery: Time-Boxing and Mid-Round Changes"),
    ("Locks, Deadlocks, and Lock Escalation", "Locks, Deadlocks, and Lock Escalation in RDBMS"),
    ("JSONB and Advanced Index Types", "JSONB and Advanced PostgreSQL Index Types"),
    ("Data Modelling and Join Tables", "Data Modelling and Explicit Join Tables"),
    ("Auto-Configuration and Bean Lifecycle", "Spring Auto-Configuration and Bean Lifecycle"),
    ("Consumer Lag, Backpressure, DLQ", "Consumer Lag, Backpressure, and DLQ Strategy"),
    ("reflection-and-dynamic-proxies.md", "Reflection and Dynamic Proxies"),
    ("equals/hashCode/Comparable", "equals(), hashCode(), and Comparable Contracts"),
    ("Generics, Erasure, PECS", "Generics: Erasure, Variance, and PECS"),
    ("Strings, Interning, Compact Strings", "Strings: Interning, Compact Strings, and Builders"),
    ("Consensus — Raft and Paxos", "Consensus Algorithms: Raft and Paxos"),
    ("MVCC, Vacuum, and Bloat", "MVCC in PostgreSQL, Vacuum, and Bloat"),
    ("Table Partitioning and Sharding", "Table Partitioning and Sharding Strategies"),
    ("Atomics, CAS, ABA", "Atomics, CAS, and the ABA Problem"),
    ("Executors and Pool Sizing", "Executors and Thread Pool Sizing"),
    ("Security Filter Chain", "Spring Security Filter Chain"),
    ("RAG and Vector Databases", "RAG and Vector Databases (pgvector)"),
    ("Event-Driven Architecture Integration Styles", "Event-Driven Architecture: Integration Styles, Choreography, and Orchestration"),
    ("Serialization Hazards", "Serialization Hazards and Alternatives"),
    ("Rendering Strategies: SSR, SSG, and ISR", "Rendering Strategies: SSR, SSG, and ISR — Mechanics and When to Choose Each"),
    ("CopyOnWriteArrayList Trade-offs", "CopyOnWriteArrayList and Copy-on-Write Trade-offs"),
    ("Technical Answer Framework", "The Technical Answer Framework — Nine Layers"),
    ("The Technical Answer Framework", "The Technical Answer Framework — Nine Layers"),
    ("Deadlock and Thread Diagnostics", "Deadlock, Race Conditions, and Thread Diagnostics"),
    ("OWASP Top 10", "OWASP Top 10 for Backend Services"),
    ("Design-Style Coding Problems", "Design-Style Coding Problems (LRU, LFU, Iterators)"),
    # sub-3x, raw filename (clear formatting bug regardless of frequency)
    ("immutability-and-defensive-copying.md", "Immutability and Defensive Copying"),
    ("react-fundamentals-jsx-components-props-and-state.md", "React Fundamentals: JSX, Components, Props, and State"),
    ("equals-hashcode-and-comparable-contracts.md", "equals(), hashCode(), and Comparable Contracts"),
    ("test-strategy-and-test-doubles.md", "Test Strategy, the Pyramid, and Test Doubles"),
    # sub-3x, domain name (Spring/Kafka) entirely missing from link text
    ("Transactional Proxy Mechanics and Propagation", "Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation"),
    ("`@Transactional`: Proxy Mechanics, Rollback Rules, and Propagation", "Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation"),
    ("Producer Semantics & Partition Key Design", "Kafka Producer Semantics: acks, Idempotence, and Partition Key Design"),
    ("Consumer Groups, Rebalancing, and Offset Management", "Kafka Consumer Groups, Rebalancing, and Offset Management"),
]


def main() -> None:
    total_fixed = 0
    files_touched = 0
    for dirpath, _, filenames in os.walk(SYLLABUS):
        for fn in filenames:
            if not fn.endswith(".md"):
                continue
            path = os.path.join(dirpath, fn)
            text = open(path, encoding="utf-8").read()
            new_text = text
            file_fixes = 0
            for old, new in REPLACEMENTS:
                pattern = "[" + old + "]("
                replacement = "[" + new + "]("
                count = new_text.count(pattern)
                if count:
                    new_text = new_text.replace(pattern, replacement)
                    file_fixes += count
            if file_fixes:
                open(path, "w", encoding="utf-8").write(new_text)
                rel = os.path.relpath(path, ROOT)
                print(f"{file_fixes:3d}  {rel}")
                total_fixed += file_fixes
                files_touched += 1

    print(f"\nFixed {total_fixed} link(s) across {files_touched} file(s).")


if __name__ == "__main__":
    main()
