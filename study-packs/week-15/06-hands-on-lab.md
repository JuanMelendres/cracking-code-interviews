---
title: "Hands-On Lab — Week 15 (Cloud & Infrastructure)"
week: 15
document_type: study-pack-lab
status: draft
last_reviewed: 2026-07-31
---

# Hands-On Lab — Week 15 (Cloud & Infrastructure)

This week's deliverable replaces the usual coding-practice problem set — the material is infrastructure-shaped, not algorithm-shaped, so the hands-on work is reproducing real container/YAML artifacts rather than solving LeetCode-style problems.

**Verification note:** all commands below are real and were executed on Docker 29.6.2 / `eclipse-temurin:21-jre`, and the YAML manifests were validated with `ruby -ryaml`.

## Lab 1 — Container-aware JVM heap sizing (T-1003)

```bash
cd practice/java/week-15/container-ergonomics
mkdir -p out && javac -d out src/HeapErgonomicsDemo.java
docker run --rm --memory=256m -v "$(pwd)/out:/app" eclipse-temurin:21-jre java -cp /app HeapErgonomicsDemo
docker run --rm --memory=512m -v "$(pwd)/out:/app" eclipse-temurin:21-jre java -cp /app HeapErgonomicsDemo
docker run --rm --memory=1g   -v "$(pwd)/out:/app" eclipse-temurin:21-jre java -cp /app HeapErgonomicsDemo
```

Expected: three different `maxMemory()` values, confirming the JVM is reading the container's cgroup limit, not host memory.

## Lab 2 — OutOfMemoryError vs. OOMKilled (T-1003)

```bash
javac -d out src/AllocationDemo.java
# Scenario A: OutOfMemoryError
docker run --rm --memory=512m -v "$(pwd)/out:/app" eclipse-temurin:21-jre java -Xmx64m -cp /app AllocationDemo; echo "exit: $?"
# Scenario B: OOMKilled
docker run --name oomkill-test --memory=100m -v "$(pwd)/out:/app" eclipse-temurin:21-jre java -Xmx256m -cp /app AllocationDemo; echo "exit: $?"
docker inspect oomkill-test --format '{{.State.OOMKilled}} exitcode={{.State.ExitCode}}'
docker rm oomkill-test
```

Expected: Scenario A ends with a `java.lang.OutOfMemoryError` stack trace and exit code 1. Scenario B ends with no output at all after the last successful log line, exit code 137, and `docker inspect` confirming `OOMKilled=true`.

## Lab 3 — Validate the Kubernetes manifest (T-1002)

```bash
cd practice/k8s/week-15
ruby -ryaml -e "
docs = YAML.load_stream(File.read('deployment-with-probes-and-limits.yaml'))
puts \"#{docs.length} YAML documents parsed successfully\"
docs.each { |d| puts \"  - #{d['kind']}: #{d['metadata']['name']}\" }
"
```

Expected: `3 YAML documents parsed successfully`, listing `Deployment`, `Service`, and `HorizontalPodAutoscaler`.

## Lab 4 — Validate the CI/CD pipeline (T-1009)

```bash
ruby -ryaml -e "
d = YAML.load_file('ci-cd-pipeline.yaml')
puts 'Parsed OK. Jobs: ' + d['jobs'].keys.join(', ')
"
```

Expected: `Parsed OK. Jobs: build-and-test, deploy-canary, promote-to-stable`.

## Lab 5 — Worked cost calculation (T-1007)

Redo Calculation 2 from `03-cloud-cost-and-scaling-economics.md` with your own numbers: pick a peak instance count, a trough instance count, peak hours/day, and a unit price, then compute both the static-peak-provisioning annual cost and the correctly-autoscaled annual cost by hand. Confirm your arithmetic matches the general formula given in that chapter's Solutions section.

## Self-Check

- [ ] All three heap-sizing measurements reproduced with your own container runs
- [ ] Both OutOfMemoryError and OOMKilled scenarios reproduced, including the `docker inspect` confirmation
- [ ] Both YAML manifests validated successfully in your own environment
- [ ] Your own cost calculation matches the general formula
