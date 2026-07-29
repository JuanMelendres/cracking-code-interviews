# Contributing

Single-author repository, but the workflow is deliberate. Approved study packs must stay stable so week-over-week scores remain comparable — a silently edited Week 1 makes the Week 6 delta meaningless.

---

## Privacy and sanitisation

> **Read this before committing anything derived from real work.**

This repository is **public**. STAR stories, architecture analyses, and production examples will by design be detailed accounts of real systems once written — they are the highest-risk content this repo will hold, more so because visibility is public rather than private. Sanitize before committing, not after.

### Never commit

| Category | Examples |
|---|---|
| **Employer secrets** | Internal architecture not publicly disclosed, unreleased roadmap, financials |
| **Confidential client names** | Any client under NDA or whose relationship is not public |
| **Production credentials** | Passwords, API keys, connection strings, certificates, private keys |
| **Internal URLs** | `*.internal`, `*.corp`, VPN hosts, private registries, Jira/Confluence links |
| **Real customer data** | Names, emails, order IDs, account numbers — even in sample data |
| **Proprietary source code** | Copy nothing verbatim from an employer's codebase |
| **Personal information about colleagues** | Names, performance details, anything they'd not want written down |
| **Interviewer-identifying detail** | Company + date + role can identify an individual |
| **Tokens embedded in URLs** | `?id_token=`, `?access_token=`, signed S3 URLs, share links |

> **A real example from this project's own audit.** The source knowledge base contained a "further reading" link whose query string held an expired Google OAuth `id_token` — a JWT carrying a personal email address, full name, and profile URL. It was long expired and posed no live risk, but it sat in a document for months. **Tokens hide inside URLs you paste without reading.** Strip query strings from any link you copy.

### Anonymise instead

| Instead of | Write |
|---|---|
| "Acme Bank" | `financial-services client` |
| "ShipFast Inc." | `logistics platform` |
| "Project Odyssey" | `internal migration project` |
| "the checkout-api service" | `production service` / `a payment service` |
| "Sarah, the staff engineer" | `a senior colleague` |
| "our Grafana at grafana.corp.acme.com" | `our metrics dashboard` |
| "we processed 4.2M orders for BigRetailer" | `a high-volume retail workload` |

**Keep the numbers, drop the identifiers.** *"A service handling roughly a thousand requests a minute"* is both safe and more useful in an interview than a company name.

### Sanitisation checklist for STAR stories

Before committing anything in `study-packs/*/`, `interview-playbook/behavioral/`, or a `story-bank.md`:

- [ ] No employer name, or only a public, non-sensitive one
- [ ] No client name unless demonstrably public
- [ ] No colleague identifiable by name or role-plus-team
- [ ] No internal system names that would mean something to an insider
- [ ] No URLs pointing at internal infrastructure
- [ ] Metrics preserved; identifying context removed
- [ ] Would you be comfortable if this leaked? If hesitant, sanitise further.

### Local-only escape hatch

Genuinely un-sanitisable material — an unredacted story draft, raw interview notes — goes in a `local/` directory or a `*.private.md` file. Both are gitignored:

```
study-packs/week-01/local/story-1-raw.md      ← ignored
study-packs/week-01/story-1.private.md        ← ignored
```

**Verify before relying on it:**

```bash
git check-ignore -v study-packs/week-01/local/story-1-raw.md
```

Interview recordings are also gitignored (`recordings/`, `*.m4a`, `*.mp3`, …) — they are large and frequently contain unsanitised speech.

---

## Branching

| Purpose | Branch name | Example |
|---|---|---|
| New study pack | `study/week-XX` | `study/week-03` |
| Correcting an approved week | `fix/week-XX-description` | `fix/week-01-lru-tests` |
| Handbook chapter | `feat/handbook-<domain>-<topic>` | `feat/handbook-spring-transactional` |
| Practice exercises | `feat/practice-<area>` | `feat/practice-sql-index-lab` |
| Repo maintenance | `chore/description` | `chore/add-validation-script` |

**Rules**

1. **One branch per week.** Never mix weeks.
2. **Never amend an approved week on a study branch.** Corrections get a dedicated `fix/` branch so the change is reviewable in isolation.
3. **No auto-merge to `main`.** Every branch is reviewed first.
4. **Report any change to approved content explicitly** in the PR description and in `CHANGELOG.md`.

---

## Commit messages

[Conventional Commits](https://www.conventionalcommits.org/).

```
<type>: <imperative summary>

[optional body — the WHY, not the what]
```

| Type | Use for |
|---|---|
| `docs:` | Study packs, project documents, README |
| `feat:` | New exercises, scripts, templates, handbook chapters |
| `fix:` | Corrections to previously committed content |
| `chore:` | Tooling, config, structure |
| `refactor:` | Reorganisation without content change |

```
docs: initialize interview preparation repository
docs: add week 1 study pack
docs: add week 2 study pack
feat: add runnable PostgreSQL index lab
fix: correct LRU cache eviction on key update
fix: clarify PostgreSQL index-only scan requires vacuum
chore: add markdown fence validation script
```

**`fix:` commits must state what was wrong**, not just what changed. The body carries the reasoning:

```
fix: correct LRU cache eviction on key update

put() unlinked an existing node from the list but never removed its key
from the map, so map.size() read as full and evicted an unrelated valid
entry on what should have been a pure update.

Failing sequence: put(1,1); put(2,2); put(1,10); get(2) -> -1, expected 2
```

---

## Validation before committing

```bash
./scripts/validate.sh
```

Checks:

| # | Check | Failure means |
|---|---|---|
| 1 | Every Markdown file is readable UTF-8 | Encoding corruption |
| 2 | Code fences are balanced | Rendering breaks from that point down |
| 3 | Diagrams use ` ```mermaid ` | Diagram renders as plain text |
| 4 | Java uses ` ```java `, SQL uses ` ```sql ` | No syntax highlighting |
| 5 | Headings start at `#` and don't skip levels | Broken document outline |
| 6 | No duplicate filenames across the repo | Ambiguous cross-references |
| 7 | Relative links resolve | Broken navigation |
| 8 | No secret patterns | **Stop and remediate** |

Check 8 is a coarse regex scan, not a guarantee. Run [Gitleaks](https://github.com/gitleaks/gitleaks) if available:

```bash
gitleaks detect --source . --no-git --verbose
```

---

## Study pack conventions

Each `study-packs/week-XX/` contains:

| File | Purpose |
|---|---|
| `README.md` | Objective, schedule, workload variants, exit criteria |
| `MANIFEST.md` | Every file, its purpose, and verification status |
| `NN-<topic>.md` | Numbered chapters in dependency order |
| `NN-week-X-mock-interview.md` | Candidate section and interviewer section, hard-separated |
| `NN-week-X-checklist.md` | Day-by-day operational list |
| `resources.md` | Primary sources, classified by authority |

### Content rules

**Source classification is mandatory.** Every non-obvious claim carries a marker:

| Marker | Meaning |
|---|---|
| `[V]` | Verified against a primary source or executed |
| `[J]` | Engineering judgment — defensible, contestable |
| `[H]` | Heuristic — interview advice based on pattern, not measurement |
| `[E-PG]` / `[E-InnoDB]` | Engine-specific. **Never generalise across engines.** |

**Never invent:**
- Interview frequency statistics or percentages
- Benchmark numbers
- Company-specific claims about what an employer asks
- Production incidents
- The author's personal experience

Where a production example is needed, supply a **template with extraction prompts**. A fabricated example collapses on the first follow-up.

**Executable content must be executed.** Java is compiled and run before committing; assertion counts go in the manifest. Where something could not be executed — SQL without a database — say so explicitly and mark expected output as illustrative.

**Defective code, where included deliberately for errata drills, must be fenced with `⛔` markers** and must never appear as the final solution.
