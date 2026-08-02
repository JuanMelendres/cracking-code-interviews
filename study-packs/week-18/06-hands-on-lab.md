---
title: "Hands-On Lab — Week 18 (Testing Domain Closure)"
week: 18
document_type: study-pack-lab
status: draft
last_reviewed: 2026-08-02
---

# Hands-On Lab — Week 18 (Testing Domain Closure)

Five labs, one per topic, all pure Java (plus JUnit 5 jars already resolvable from the local Maven repository — no network download required if already cached).

**Verification note:** all commands below are real and were executed on OpenJDK 21.0.12 with JUnit Jupiter/Platform 5.12.2 / 1.12.2 and Mockito 5.17.0.

## Setup — resolve the JUnit classpath once

```bash
JUNIT_CP=$(find ~/.m2/repository/org/junit ~/.m2/repository/org/opentest4j ~/.m2/repository/org/apiguardian -name "*.jar" | grep -v sources | grep -v javadoc | tr '\n' ':')
```

## Lab 1 — Load test: mean vs. percentiles (T-1106)

```bash
cd practice/java/week-18/load-testing/src
javac -d ../out LoadTestDemo.java
java -cp ../out LoadTestDemo
```

Expected: mean and p50 both look healthy; p95/p99 reveal a real, injected 1-in-20 slow path invisible below that threshold.

## Lab 2 — Live-coding TDD: run-length encoding (T-1108)

```bash
cd practice/java/week-18/live-coding-tdd/src
CP="$JUNIT_CP"
RUNNER=../../junit5-features/out
javac -d ../out -cp "$CP" Rle.java RleTest.java
java -cp "../out:$RUNNER:$CP" ConsoleTestRunner RleTest
```

Expected: 3 tests, all passing (built up incrementally — try reverting `Rle.java` to earlier versions from this chapter's Internal Implementation section to reproduce each RED step yourself).

## Lab 3 — Contract verification: compliant vs. breaking provider (T-1105)

```bash
cd practice/java/week-18/contract-testing/src
CP="$JUNIT_CP"
RUNNER=../../junit5-features/out
javac -d ../out -cp "$CP" ContractVerificationTest.java OrderProvider.java
java -Dcontract.mode=compliant -cp "../out:$RUNNER:$CP" ConsoleTestRunner ContractVerificationTest
java -Dcontract.mode=breaking -cp "../out:$RUNNER:$CP" ConsoleTestRunner ContractVerificationTest
```

Expected: compliant mode passes; breaking mode fails with a message naming the specific missing field (`status`) and why the consumer needs it.

## Lab 4 — JUnit 5 advanced features and tag filtering (T-1102)

```bash
cd practice/java/week-18/junit5-features/src
CP="$JUNIT_CP"
javac -d ../out -cp "$CP" ConsoleTestRunner.java AdvancedFeaturesDemoTest.java
java -cp "../out:$CP" ConsoleTestRunner AdvancedFeaturesDemoTest
java -cp "../out:$CP" ConsoleTestRunner AdvancedFeaturesDemoTest slow
java -cp "../out:$CP" ConsoleTestRunner AdvancedFeaturesDemoTest fast
```

Expected: 10/10 tests pass unfiltered; exactly 1/10 with the `slow` tag filter; exactly 9/10 with the `fast` tag filter.

## Lab 5 — Property-based bug-finding and mutation testing (T-1107)

```bash
cd practice/java/week-18/mutation-property
CP="$JUNIT_CP"
RUNNER=../junit5-features/out

# Property-based half
javac -d out -cp "$CP" src/MergeSorted.java src/MergeSortedExampleTest.java src/MergeSortedPropertyTest.java
java -cp "out:$RUNNER:$CP" ConsoleTestRunner MergeSortedExampleTest
java -cp "out:$RUNNER:$CP" ConsoleTestRunner MergeSortedPropertyTest

# Mutation-testing half
javac -d out-original original/DiscountPolicy.java
javac -d out-mutant mutant/DiscountPolicy.java
javac -d out-tests -cp "$CP:out-original" src/DiscountPolicyWeakTest.java src/DiscountPolicyStrongTest.java
java -cp "out-tests:out-original:$RUNNER:$CP" ConsoleTestRunner DiscountPolicyWeakTest
java -cp "out-tests:out-mutant:$RUNNER:$CP" ConsoleTestRunner DiscountPolicyWeakTest
java -cp "out-tests:out-original:$RUNNER:$CP" ConsoleTestRunner DiscountPolicyStrongTest
java -cp "out-tests:out-mutant:$RUNNER:$CP" ConsoleTestRunner DiscountPolicyStrongTest
```

Expected: the two hand-picked example tests pass despite a real bug; the property test fails on trial 2 with a concrete counterexample. The weak suite passes against both the original and the mutant (mutant survives); the strong suite passes against the original but fails against the mutant (mutant killed).

## Self-Check

- [ ] All five labs reproduced with your own matching (not necessarily identical) real output
- [ ] Can explain, for Lab 1, why mean and p50 both missed what p95 caught
- [ ] Can explain, for Lab 2, why the RED step at each stage failed for the *expected* reason
- [ ] Can explain, for Lab 3, why the breaking-provider failure message names a specific field rather than just "contract violated"
- [ ] Can explain, for Lab 4, why tag filtering counts (1/10, 9/10) prove genuine execution partitioning, not just labeling
- [ ] Can explain, for Lab 5, why both hand-picked examples missed the merge bug, and why the weak suite's mutant survived specifically at the boundary value
