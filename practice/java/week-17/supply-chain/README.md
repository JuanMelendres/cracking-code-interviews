# Supply Chain / SBOM Demo — Reproduce Commands

No Java source for this topic — the evidence comes directly from Docker Scout
CLI output against a real image, `eclipse-temurin:21-jre` (the same base
image used in `syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md`).

Tooling used: Docker 29.6.2, Docker Scout v1.24.0.

```bash
# Generate the SBOM (213 packages found at time of writing)
docker scout sbom eclipse-temurin:21-jre --format list

# Scan the SBOM against Docker Scout's CVE database
docker scout cves eclipse-temurin:21-jre
```

Real captured result at time of writing: 213 packages inventoried; 13
vulnerabilities found across 3 packages (1 CRITICAL, 1 HIGH, 7 MEDIUM, 1 LOW,
3 UNSPECIFIED) — including CVE-2026-39821 (CRITICAL) in the transitive Go
package `golang.org/x/net@0.40.0`, bundled into the base image itself, not a
dependency any application code chose directly. CVE numbers and counts will
drift over time as the image and vulnerability database are updated — that
drift is itself the point: this is why SBOM scanning must run continuously,
not as a one-time snapshot.
