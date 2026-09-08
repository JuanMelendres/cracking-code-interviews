# Java Version Features Demo (T-2211)

Real, compiled, executed evidence backing [Java Version Features Timeline: Java 8 Through 25](../../../../syllabus/02-java/language-core/java-version-features-timeline.md).

## Files

- `src/VersionFeaturesDemo.java` — real, compiled, executed on OpenJDK 21.0.12, exercising features spanning six real Java releases in one program: `var` (Java 10), text blocks (Java 15), records including a real compact-constructor validation (Java 16), pattern matching for `instanceof` (Java 16), sealed interfaces (Java 17), exhaustive pattern matching for `switch` with no `default` branch (Java 21), and 1,000 real virtual threads completing concurrently (Java 21). 9/9 assertions pass.
- `version-features-output.txt` — the real captured output of running that demo.
- `jdk-version.txt` — the real `java -version` output confirming which JDK actually ran this.

## What this demo deliberately does NOT cover

Java 8 features (lambdas, streams, `Optional`, default/static interface methods) are NOT re-demonstrated here — they already have dedicated, real-evidence-backed canonical chapters in this repository (`lambdas-and-functional-interfaces.md`, `streams-and-collectors.md`, `optional-and-null-strategy.md`), and this project's own no-duplication rule means the version-features chapter links to those rather than re-proving the same claims. Structured concurrency and scoped values (both still preview or newly-final depending on the exact JDK release) are also not demonstrated here, since they require `--enable-preview` on some releases and this demo deliberately sticks to features that are unconditionally final and stable on a plain OpenJDK 21 install with no special flags — see the chapter itself for their real, current status.

## Reproduce it yourself

```bash
cd practice/java/oop-fundamentals/java-version-features
javac -d out src/VersionFeaturesDemo.java
java -cp out VersionFeaturesDemo
```
