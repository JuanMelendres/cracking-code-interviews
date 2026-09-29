# Mockito `@Mock` vs. `@Spy` — Real, Executed Demo

Backs the `@Mock` vs. `@Spy` material in [Test Strategy and Test Doubles](../../../../syllabus/08-testing/test-strategy-and-test-doubles.md).

Mockito 5.11.0 and JUnit 5.10.2 on OpenJDK 21.0.12 — plain jars from Maven Central, no Maven/Gradle install. Seven tests, all passing; real output in `test-transcript.txt`.

`AuditLog` is deliberately stateful (a call counter and an accumulating list), so every claim below is measured from observable side effects rather than inferred from a return value.

## Run it

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java
CP="out:$(ls lib/*.jar | tr '\n' ':')"
java -jar lib/junit-platform-console-standalone.jar execute \
    --class-path "$CP" --select-class MockVsSpyTest --details=tree --disable-ansi-colors
```

The JUnit console launcher does not expand a `lib/*` classpath wildcard the way `java -cp` does — hence the explicit `CP` construction above.

## What it proves

**`mock()` runs nothing.** `write("hello")` returned `null` and `realCallCount()` returned `0` — the second `0` is itself a mocked return, not a reading of the real field. On a mock, every method is stubbed, including the accessors a test might use to check state.

**`spy()` runs the real thing.** The same call returned `"WROTE:hello"` and left `realCallCount() == 1`.

**The trap, measured.** `when(log.write("hello")).thenReturn("STUBBED")` left `realCallCount() == 1` and `written() == [hello]` *before any test action had run*. Mockito must evaluate the argument to `when(...)`, and evaluating it means really calling `write("hello")` on the spy. On a spy, `when()` executes the method it is trying to stub — so the side effect leaks, and if the real method throws or hits a database, the stubbing line itself is what fails.

**The fix, measured.** `doReturn("STUBBED").when(log).write("hello")` left `realCallCount() == 0` and `written() == []`. The stub still applies afterwards. This is the reason `doReturn`/`doThrow`/`doAnswer` exist as an alternative syntax at all.

**Self-invocation: the stub *does* apply.** `writeTwice("x")` calls `this.write(...)` twice internally. With `write("x")` stubbed via `doReturn`, it returned `"STUBBED|STUBBED"` with `realCallCount() == 0`. A Mockito spy is a proxy subclass whose real method bodies execute with `this` bound to the proxy, so internal self-calls are intercepted. This is the **opposite** of Spring's `@Transactional` self-invocation behavior, where an internal `this.method()` call bypasses the proxy entirely — see [Transactional Proxy Mechanics and Propagation](../../../../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md). Two proxies, two different answers to the same-looking question; neither can be reasoned about from "it's a proxy" alone.

**A `final` method on a spy stubs fine.** `doReturn("STUBBED").when(log).sealedWrite("x")` on a `final` method returned `"STUBBED"` with the real method never running. Mockito 5 ships the inline mock maker as its default, instrumenting via a Java agent rather than by subclassing — so the widely repeated "you cannot mock final methods" rule is pre-Mockito-5 folklore, not current behavior. (The launcher does print a real `WARNING: A Java agent has been loaded dynamically`, which is that mechanism being visible.)

**`verify()` is identical on both.** `verify(x, times(2)).write("a")` passed unchanged against a mock and a spy — invocation recording is independent of whether real code ran.
