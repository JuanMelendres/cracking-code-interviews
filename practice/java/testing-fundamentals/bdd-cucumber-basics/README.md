# Behavior-Driven Development with Cucumber — Real, Executed Demo

Backs [Behavior-Driven Development with Cucumber](../../../../syllabus/08-testing/behavior-driven-development-with-cucumber.md) (T-2412). Real Cucumber-JVM 7.18.0, run against real JUnit 5 via the JUnit Platform — no Maven/Gradle install required, every jar fetched directly from Maven Central.

## Setup

```bash
mkdir -p lib
BASE=https://repo1.maven.org/maven2
curl -sfL "$BASE/io/cucumber/cucumber-java/7.18.0/cucumber-java-7.18.0.jar" -o lib/cucumber-java-7.18.0.jar
curl -sfL "$BASE/io/cucumber/cucumber-core/7.18.0/cucumber-core-7.18.0.jar" -o lib/cucumber-core-7.18.0.jar
curl -sfL "$BASE/io/cucumber/cucumber-junit-platform-engine/7.18.0/cucumber-junit-platform-engine-7.18.0.jar" -o lib/cucumber-junit-platform-engine-7.18.0.jar
curl -sfL "$BASE/io/cucumber/cucumber-plugin/7.18.0/cucumber-plugin-7.18.0.jar" -o lib/cucumber-plugin-7.18.0.jar
curl -sfL "$BASE/io/cucumber/cucumber-expressions/17.1.0/cucumber-expressions-17.1.0.jar" -o lib/cucumber-expressions-17.1.0.jar
curl -sfL "$BASE/io/cucumber/datatable/7.18.0/datatable-7.18.0.jar" -o lib/datatable-7.18.0.jar
curl -sfL "$BASE/io/cucumber/tag-expressions/6.1.0/tag-expressions-6.1.0.jar" -o lib/tag-expressions-6.1.0.jar
curl -sfL "$BASE/io/cucumber/cucumber-gherkin/7.18.0/cucumber-gherkin-7.18.0.jar" -o lib/cucumber-gherkin-7.18.0.jar
curl -sfL "$BASE/io/cucumber/cucumber-gherkin-messages/7.18.0/cucumber-gherkin-messages-7.18.0.jar" -o lib/cucumber-gherkin-messages-7.18.0.jar
curl -sfL "$BASE/io/cucumber/gherkin/28.0.0/gherkin-28.0.0.jar" -o lib/gherkin-28.0.0.jar
curl -sfL "$BASE/io/cucumber/messages/24.1.0/messages-24.1.0.jar" -o lib/messages-24.1.0.jar
curl -sfL "$BASE/io/cucumber/docstring/7.18.0/docstring-7.18.0.jar" -o lib/docstring-7.18.0.jar
curl -sfL "$BASE/io/cucumber/ci-environment/10.0.1/ci-environment-10.0.1.jar" -o lib/ci-environment-10.0.1.jar
curl -sfL "$BASE/com/google/protobuf/protobuf-java/3.25.1/protobuf-java-3.25.1.jar" -o lib/protobuf-java-3.25.1.jar
curl -sfL "$BASE/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar" -o lib/apiguardian-api-1.1.2.jar
curl -sfL "$BASE/org/junit/platform/junit-platform-suite-api/1.10.3/junit-platform-suite-api-1.10.3.jar" -o lib/junit-platform-suite-api-1.10.3.jar
curl -sfL "$BASE/org/junit/platform/junit-platform-suite-engine/1.10.3/junit-platform-suite-engine-1.10.3.jar" -o lib/junit-platform-suite-engine-1.10.3.jar
curl -sfL "$BASE/org/junit/platform/junit-platform-console-standalone/1.10.3/junit-platform-console-standalone-1.10.3.jar" -o lib/junit-platform-console-standalone.jar

mkdir -p out
cp -r src/resources/features out/
javac -cp "lib/*" -d out src/java/steps/*.java src/java/*.java
java -cp "lib/*:out" org.junit.platform.console.ConsoleLauncher execute -cp out --scan-classpath --details=tree
```

This exact jar set (18 jars, all real Cucumber-JVM 7.18.0 runtime dependencies plus their transitives, resolved by hand from `cucumber-core`'s own published POM since no Maven/Gradle is used) is what it genuinely takes to run Cucumber without a build tool — worth knowing, since "just add the Cucumber dependency" undersells how many transitive pieces are actually involved.

## What it proves

1. **A real `.feature` file, written in Gherkin, drives real JUnit 5 test execution** — `src/resources/features/shopping_cart.feature`'s `Given`/`When`/`Then` steps map to real Java methods in `ShoppingCartSteps.java` via Cucumber expressions (`{double}`, `{int}`), and the whole suite runs through the standard JUnit Platform Console Launcher, the same tool used elsewhere in this repo's plain-JUnit demos.
2. **A `Scenario Outline` with an `Examples` table is real, genuine data-driven testing** — one scenario definition, three real executed test instances (`Example #1.1`–`#1.3`), each a distinct JUnit Platform test node with its own pass/fail result.
3. **A real, captured assertion failure** (`real-failure-output.txt`) — the discount calculation was deliberately changed to divide by `1000.0` instead of `100.0` (a realistic off-by-one-order-of-magnitude bug), the suite re-run, and the real Cucumber/JUnit failure output captured: `expected: <80.0> but was: <98.0>`, full stack trace, before the code was reverted to its correct, committed state.
4. **A genuinely interesting, unplanned finding from that same failure run**: `Example #1.3` (`subtotal=50.00, percent=0, expected=50.00`) still *passed* even with the bug present — a 0%-discount test case can't catch a percentage-scaling bug, since the bug's effect is multiplied by the broken percentage itself. A real, concrete illustration of why edge cases like "0%" don't substitute for a case that actually exercises the logic being tested.
5. **A real, captured undefined-step snippet** (`undefined-step-output.txt`) — a scenario with a step that has no matching step definition (`Given a gift card with a balance of 25.00 is applied to the cart`) produces Cucumber's own real, generated Java method snippet as a starting point, exact and unedited, not a hand-written illustration of what it "would" look like.
