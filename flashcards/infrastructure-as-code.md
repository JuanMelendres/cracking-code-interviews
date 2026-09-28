---
title: "Flashcards: Infrastructure as Code"
slug: infrastructure-as-code
document_type: flashcard-deck
domain: 15-cloud
topic_id: T-2438
canonical: ../syllabus/15-cloud/infrastructure-as-code.md
last_updated: 2026-09-28
---

# Flashcards: Infrastructure as Code

**Canonical chapter:** [`syllabus/15-cloud/infrastructure-as-code.md`](../syllabus/15-cloud/infrastructure-as-code.md)

## Card: What a state file is and why it is dangerous

**Prompt:**
What does a Terraform state file contain, and what follows operationally?

**Answer:**
It maps declared resources to the real cloud objects the tool created, which is what lets a plan compare configuration, state, and reality. Four consequences: it contains secrets such as generated passwords **in plaintext**, so it is a credential store; it must be shared between engineers and CI, so it belongs in a remote backend; it must be **locked**, because concurrent applies can corrupt it; and it should be versioned, so a bad apply is recoverable.

**Why it matters:**
The worst IaC outcomes are state outcomes — lost, leaked, or raced — and a plan catches none of them.

**Common trap:**
Believing state is a regenerable cache. Losing it does not destroy infrastructure; it orphans it.

**Related:**
[Infrastructure as Code](../syllabus/15-cloud/infrastructure-as-code.md)

## Card: What a plan compares, and what it cannot tell you

**Prompt:**
What does `terraform plan` actually compute, and what are its limits?

**Answer:**
A three-way comparison of configuration (what you declared), state (what the tool believes it created), and real infrastructure — producing create / update / replace / destroy actions. Its limits: it does not predict the *behaviour* of a change ("modify security group" does not say "this severs a production dependency"); it is point-in-time, so reality can change before apply; and it cannot see resources outside its own state.

**Why it matters:**
Read every plan for the word `destroy` first — recreation of a stateful resource is the most common way IaC causes an outage.

**Common trap:**
Attaching plan output to a pull request as a ritual nobody reads.

**Related:**
[CI/CD Pipeline Design and Deployment Strategies](../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md)

## Card: Why a one-line change destroys a database

**Prompt:**
How can renaming an attribute destroy a production database, and what prevents it?

**Answer:**
Some attributes cannot be updated in place, so the provider requires **replacement** — destroy then create. Whether a given change forces replacement is a *provider* property, so the same logical edit is safe on one resource type and destructive on another. Prevention is mechanical, not attentional: `lifecycle { prevent_destroy = true }` on stateful resources turns the mistake into a failed apply, plus a CI check that fails on destroy actions against protected types with a deliberate override.

**Why it matters:**
The plan says so plainly; the failure is that nobody read past the summary. Mechanisms beat "review more carefully."

**Common trap:**
Blaming the tool for correctly reporting what it was about to do.

**Related:**
[Infrastructure as Code](../syllabus/15-cloud/infrastructure-as-code.md)

## Card: Drift as a signal

**Prompt:**
Someone fixed a security group by hand during an incident. What happens next, and what should you do?

**Answer:**
The next plan sees a rule the configuration does not declare and proposes to remove it — correctly, by its own rules — so applying blind reverts the fix. The response depends on cause: an emergency fix should be codified; an unmanaged resource imported or documented; an ownership overlap with another tool resolved; a provider-side automatic change ignored deliberately with a comment.

**Why it matters:**
Chronic drift noise is a safety problem: reviewers who see meaningless changes every time stop reading the plan that matters.

**Common trap:**
Treating drift as a rules violation instead of asking why someone needed console access to fix something.

**Related:**
[Infrastructure as Code](../syllabus/15-cloud/infrastructure-as-code.md)

## Card: Splitting state by blast radius

**Prompt:**
How should state be split, and what does splitting cost?

**Answer:**
Production always separate from non-production, with different credentials and apply permissions; then split where **change frequency** differs sharply — long-lived foundations (networking, DNS, IAM) apart from frequently-changing application resources. The cost is cross-state references and more places to look. One state for everything means every change plans against the world, every apply locks everyone, and one mistake has unbounded reach.

**Why it matters:**
A weekly application change should not require planning against a network that changes twice a year.

**Common trap:**
Splitting so finely that the cross-state plumbing becomes its own maintenance burden.

**Related:**
[Cloud Cost and Scaling Economics](../syllabus/15-cloud/cloud-cost-and-scaling-economics.md)
