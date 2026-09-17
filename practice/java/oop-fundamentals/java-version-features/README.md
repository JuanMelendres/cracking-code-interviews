# Java Version Features Demo (T-2211)

Real, compiled, executed evidence backing [Java Version Features Timeline: Java 8 Through 25](../../../../syllabus/02-java/language-core/java-version-features-timeline.md).

## Files

- `src/VersionFeaturesDemo.java` — real, compiled, executed on OpenJDK 21.0.12, exercising at least one signature feature from every LTS release Java 8 through 21 in one program: a lambda expression, the Stream API, and `Optional` (Java 8); `var` in a lambda parameter and two new `String` convenience methods, `isBlank()`/`lines()` (Java 11); `var` for local-variable type inference (Java 10, the release that introduced it, ahead of its own next LTS); text blocks (Java 15); records including a real compact-constructor validation and pattern matching for `instanceof` (Java 16); sealed interfaces (Java 17); exhaustive pattern matching for `switch` with no `default` branch, and 1,000 real virtual threads completing concurrently (Java 21). 15/15 assertions pass.
- `version-features-output.txt` — the real captured output of running that demo.
- `jdk-version.txt` — the real `java -version` output confirming which JDK actually ran this.

## What this demo deliberately does NOT cover

The Java 8/11 examples here are minimal, self-contained illustrations for the version-timeline chapter's own per-LTS "in depth" section — they are deliberately small, not a replacement for this repository's dedicated, much deeper canonical chapters on the same features (`lambdas-and-functional-interfaces.md`, `streams-and-collectors.md`, `optional-and-null-strategy.md`), which the chapter links to for real depth rather than duplicating. Structured concurrency and scoped values (both still preview, or newly final only as of Java 25, depending on the exact JDK release — verified directly against the official JDK 25 JEP index while writing this chapter's latest update) are also not demonstrated here, since some releases require `--enable-preview` for them and this demo deliberately sticks to features that are unconditionally final and stable on a plain OpenJDK 21 install with no special flags — see the chapter itself for their real, current, cited status.

## Reproduce it yourself

```bash
cd practice/java/oop-fundamentals/java-version-features
javac -d out src/VersionFeaturesDemo.java
java -cp out VersionFeaturesDemo
```
