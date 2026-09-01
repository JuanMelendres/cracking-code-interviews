---
title: "Cheat Sheet: The Twelve-Factor App — Config, Precedence, and Fail-Fast Validation"
slug: twelve-factor-config
document_type: cheat-sheet
domain: system-design
topic_id: T-1008
canonical: ../handbook/system-design/twelve-factor-config.md
last_updated: 2026-09-01
---

# The Twelve-Factor App: Config, Precedence, and Fail-Fast Validation

**Canonical chapter:** [`handbook/system-design/twelve-factor-config.md`](../handbook/system-design/twelve-factor-config.md)

## Core Mental Model

Treat configuration the way you'd treat a function's parameters, not its implementation: the *code* should be identical no matter which environment it runs in, and everything that legitimately differs between environments (a database URL, a feature flag, a timeout) should be passed in from outside, not branched on inside. If you could not take the exact same build artifact and run it unmodified in dev, staging, and production purely by changing what's injected into its environment, config and code have leaked into each other somewhere.

## Essential Definitions

- **Factor III (config)** — strict separation between config (anything that varies between deployments) and code, with config stored in the environment, never hardcoded or committed.
- **Config precedence** — a real, standard layering: defaults < config file < environment variables < command-line arguments, each layer overriding only the specific keys it sets.
- **Fail-fast validation** — checking every required config value at startup, before the health check can report ready, converting a silent, delayed failure into an immediate, clear one.
- **Health check ≠ config-complete** — a passing health check proves the process is running and responding, not that its configuration is complete.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Does a value differ between any two environments this code runs in? | It's config — inject it, never branch on an environment name in code |
| Is the value a credential, key, or anything sensitive? | Treat it as a secret specifically, not plain config |
| Does the application use a config value anywhere in its normal request path? | Validate its presence at startup, before the health check can report ready |
| Unsure why a config value "isn't taking effect"? | Check the real precedence order before assuming the value is wrong |

**Trade-offs:**

| Config source | Precedence | Typical real use |
|---|---|---|
| Hardcoded defaults | Lowest | Safe fallback, zero configuration |
| Config file | Low-mid | Per-environment baseline values |
| Environment variables | Mid-high | Container/orchestrator-injected values |
| Command-line arguments | Highest | Per-invocation override, debugging |

## Key Numbers (real, executed from-scratch Java config loader)

Real precedence layering, one layer at a time:

```
Defaults + file only:                          timeout.ms = 2000  (from file:config.properties)
+ real env var (APP_TIMEOUT_MS=5000):          timeout.ms = 5000  (from env:APP_TIMEOUT_MS)
+ real CLI arg (--timeout.ms=9000):            timeout.ms = 9000  (from cli:--timeout.ms=9000)
```

Real fail-fast contrast for the identical missing `database.url`:

```
BUGGY (no startup validation): NullPointerException, deep in business logic
FIXED (real startup validation): Missing required config key 'database.url' -- refusing to start.
```

## Common Pitfalls

- Branching on an environment name (`if (env.equals("prod"))`) inside application code instead of injecting the actual differing values.
- Committing environment-specific config files (especially with secrets) directly into version control.
- Assuming a passing health check proves the application is correctly configured, rather than merely running and responding.
- Not knowing the real precedence order, debugging "why isn't my config change taking effect" by guessing.

## Interview Answer Skeleton

**30-sec:** Factor III demands config that varies between environments be strictly separated from code, stored in the environment. Real precedence: defaults < file < environment variables < CLI arguments. Fail-fast validation at startup turns a missing-config bug into an immediate, named failure.

**2-min:** Add the measured precedence proof (one layer at a time overriding the last) and the fail-fast contrast: an identical missing value reproduced as both a confusing `NullPointerException` deep in business logic and a clear, named startup failure.

**Whiteboard:** Four stacked boxes bottom to top — defaults, config file, environment variables, CLI arguments — arrow up labeled "increasing precedence," one key's value overwritten crossing each box. Separately, a timeline: deploy → health check passes → (much later) request fails — label the gap "what fail-fast validation closes."

**Staff-level framing:** Connect a health check's limited guarantee (process is running) to the real production risk of undetected missing configuration, and propose a deployment-pipeline-level prevention (diffing required config across environment manifests) rather than relying solely on runtime validation.

## Production Warning Signs

- A service passes its readiness probe, shows healthy, then fails on its very first real request with a generic `NullPointerException` — a required environment variable present in staging but omitted from the production manifest during a refactor.
- The same build behaving differently in two environments with no code difference — expected and healthy if intentional (Factor III working); a real bug if from an environment-name branch in code.
- A config value "isn't taking effect" despite being set somewhere — check the real precedence order; a higher-precedence source may be silently overriding it.

## Related

- `handbook/spring/auto-configuration-and-bean-lifecycle.md`
- `handbook/security/secrets-management-and-key-rotation.md`
- `handbook/spring/spring-actuator-health-and-observability-hooks.md`
