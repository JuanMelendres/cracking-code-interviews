---
title: "Flashcards: The Twelve-Factor App: Config, Precedence, and Fail-Fast Validation"
slug: twelve-factor-config
document_type: flashcard-deck
domain: system-design
topic_id: T-1008
canonical: ../handbook/system-design/twelve-factor-config.md
last_updated: 2026-09-01
---

# Flashcards: The Twelve-Factor App: Config, Precedence, and Fail-Fast Validation

**Canonical chapter:** [`syllabus/15-cloud/twelve-factor-config.md`](../syllabus/15-cloud/twelve-factor-config.md)

## Card: What does Factor III actually require?

**Prompt:**
What does the twelve-factor app's Factor III (config) require, precisely?

**Answer:**
Strict separation of config (anything that varies between deployments) from
code, with config stored in the environment (typically environment
variables) rather than hardcoded or committed to version control — so the
identical build artifact runs correctly in any environment purely by
changing what's injected into it.

**Why it matters:**
It's the principle behind why container images and Kubernetes
ConfigMaps/Secrets work the way they do.

**Common trap:**
Branching on an environment name inside code instead of injecting the
actual differing values.

**Related:**
[handbook/system-design/twelve-factor-config.md](../syllabus/15-cloud/twelve-factor-config.md)

## Card: What's the real config precedence order?

**Prompt:**
The same config key is set in a file, an environment variable, and a
command-line argument. Which value wins?

**Answer:**
The command-line argument — measured directly: defaults < config file <
environment variables < command-line arguments, each layer overriding only
the specific keys it sets.

**Why it matters:**
It's the same real order most frameworks (including Spring Boot) use in
production — knowing it precisely resolves "why isn't my config change
taking effect" debugging instantly.

**Common trap:**
Assuming the config file is authoritative over environment variables.

**Related:**
[handbook/system-design/twelve-factor-config.md](../syllabus/15-cloud/twelve-factor-config.md)

## Card: Why does fail-fast startup validation matter?

**Prompt:**
A service is missing a required config value, but nothing validates it at
startup. What actually happens?

**Answer:**
The service "starts" successfully and passes its health check; the failure
only appears later, wherever the missing value is first actually used —
measured directly as a generic `NullPointerException` deep in business
logic, giving no indication the real cause is missing configuration.

**Why it matters:**
It's the concrete mechanism behind a real class of production incident: a
health check proves the process runs, not that its configuration is
complete.

**Common trap:**
Assuming a passing health check is sufficient proof of correct
configuration.

**Related:**
[handbook/system-design/twelve-factor-config.md](../syllabus/15-cloud/twelve-factor-config.md)
