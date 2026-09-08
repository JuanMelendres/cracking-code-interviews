# Unit Testing Fundamentals with JUnit — Real, Executed Demo

Backs [Unit Testing Fundamentals with JUnit](../../../../syllabus/08-testing/unit-testing-fundamentals-with-junit.md) (T-2204). Real JUnit 5 tests, run with the `junit-platform-console-standalone` shaded jar (1.10.3) — no Maven/Gradle install required.

## Setup

```bash
mkdir -p lib
curl -sfL "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.3/junit-platform-console-standalone-1.10.3.jar" -o lib/junit-platform-console-standalone.jar
mkdir -p out
javac -cp "lib/*" -d out src/*.java
java -jar lib/junit-platform-console-standalone.jar execute -cp out --scan-classpath --details=tree
```

`test-run-output.txt` is the full, real, unedited output of the last run: 17/17 tests passing, across `@Test`, `@BeforeEach`, `assertThrows`, and two `@ParameterizedTest` forms (`@ValueSource`, `@CsvSource`).

`real-failure-output.txt` is a genuine artifact, not a staged one: `addsTwoPositiveNumbers()`'s assertion was temporarily changed to `assertEquals(6, calculator.add(2, 3))` (a deliberately wrong expected value), the suite re-run, and the real JUnit failure output — `expected: <6> but was: <5>`, full stack trace included — captured before the file was reverted to its correct, committed state. The chapter's Section 7 and Section 8 both use this exact, real failure text.
