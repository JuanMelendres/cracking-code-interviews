---
title: "Frontend Mid → Senior, Week 3 — Production Concerns: Discoverability and Real Performance Metrics"
document_type: study-pack
week: 3
track: frontend-mid-to-senior
status: draft
estimated_hours: 7
last_reviewed: 2026-09-08
---

# Week 3 — Production Concerns: Discoverability and Real Performance Metrics

## Weekly Outcome

By the end of this week you can implement Next.js's Metadata API correctly for a page's SEO needs, and explain what Core Web Vitals actually measure and how Next.js's image/font optimization improves them.

## Why This Week Matters

Two frequently-underweighted production concerns for a shipped app — a Senior frontend engineer is expected to own both, not treat them as an afterthought once the "real" features are done.

## Prerequisites

Weeks 1–2.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [The Metadata API and SEO Fundamentals](../../../syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md) (F-209) |
| Wed–Sat | [Image and Font Optimization, and Core Web Vitals](../../../syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md) (F-210) |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Metadata API and SEO (F-209) | [`syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md`](../../../syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md) |
| 2 | Image/Font Optimization and Core Web Vitals (F-210) | [`syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md`](../../../syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md) |

## Hands-On Exercises

Both chapters cite their own real, verified demos — follow the link from each chapter's own "Real Verified Demos" section directly (not re-verified independently here).

## Production Cookbook Cross-Reference

No frontend-specific `production-cookbook/` entry exists yet for either topic — stated honestly rather than fabricated.

## Interview Answer Drills

Answer, out loud: "what's the difference between static and dynamic metadata in the App Router, and when do you need `generateMetadata`?" and "name the three Core Web Vitals and what each one actually measures."

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described page with a slow Largest Contentful Paint, propose 2 concrete fixes using Next.js's `<Image>` and font optimization features, in under 3 minutes.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists.
- [ ] Can name all three Core Web Vitals and their target thresholds.
- [ ] Can implement dynamic metadata for a page from a blank file.

## Completion Criteria

- [ ] Implemented correct static and dynamic metadata for at least one real page.
- [ ] Can explain how Next.js's `<Image>` component improves LCP, unprompted.

## Retrospective

Note whether SEO or Web Vitals felt like the bigger knowledge gap coming in — both are common Staff-adjacent "what would you check first" follow-up questions.

## Next Week

[Week 4 — Component Design Patterns, Then What's Actually Happening Underneath](../week-04/README.md).
