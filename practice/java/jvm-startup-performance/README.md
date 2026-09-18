# JVM Startup Performance — Real, Executed Demo

Backs [JVM Startup Performance: CDS, AppCDS, and GraalVM Native Image](../../../syllabus/16-performance-jvm/jvm-startup-performance-cds-and-native-image.md).
Real OpenJDK 21.0.12, real Oracle GraalVM for JDK 21.0.12 (`native-image`), Apple M4 (arm64), macOS.

The same tiny real HTTP server ([`src/Main.java`](src/Main.java) — `com.sun.net.httpserver`,
no framework, so the measurement is JVM/runtime startup, not Spring's own
component-scanning cost) is run three ways: plain JVM, JVM with a real
dynamic AppCDS archive, and a real GraalVM native-image binary compiled from
the identical source.

## Setup and run

```bash
./build.sh                              # compiles Main.java, builds main.jar,
                                         # creates a real app.jsa (AppCDS archive),
                                         # and builds main-native if native-image
                                         # is on PATH (see below to get it)
./measure.sh "label" <port> <command...>          # real wall-clock, launch -> first real HTTP response
./memory-measure.sh "label" <port> <command...>   # real peak RSS via /usr/bin/time -l
```

Getting `native-image`: this demo used Oracle's own GraalVM for JDK 21 tarball
(no Homebrew cask sudo step needed):

```bash
curl -fL "https://download.oracle.com/graalvm/21/latest/graalvm-jdk-21_macos-aarch64_bin.tar.gz" -o graalvm.tar.gz
tar xzf graalvm.tar.gz
export PATH="$PWD/graalvm-jdk-21.0.12+7.1/Contents/Home/bin:$PATH"
```

(URL/paths are for macOS aarch64; see [Oracle's GraalVM downloads](https://www.oracle.com/java/graalvm/) for other platforms.)

Real captured output: [`output-transcript.txt`](output-transcript.txt).

## What it proves

**1. Real startup-to-first-response time, 8 runs each:**

```
Plain JVM, java -jar (baseline):           average 136ms
JVM + AppCDS (-XX:SharedArchiveFile):       average 141ms
GraalVM native-image binary:                average 9ms
```

Native-image is a real, measured **~15x faster** cold start than the plain
JVM for this app. **AppCDS's real, honest result: no measurable improvement
for this specific app** — within run-to-run noise, sometimes even slightly
slower. This is not a demo bug; it's the real, honest finding: this app
loads only a handful of classes (`Main`, the HTTP server internals, core
`java.base` classes JVM startup already loads regardless). AppCDS's actual
mechanism — skipping re-parsing and re-verifying already-loaded classes —
has essentially nothing to save here. The real-world case where AppCDS
earns its ~20-40% startup improvements (commonly cited for Spring Boot
apps) is exactly the opposite: hundreds to thousands of framework classes
loaded on every single startup, all of which AppCDS can skip re-parsing.

**2. Real peak memory (RSS):**

```
Plain JVM:              52,559,872 bytes  (~50.1MB)
GraalVM native-image:   16,482,304 bytes  (~15.7MB)
```

A real, measured **~3.2x smaller memory footprint** for the native binary
— no JIT compiler, no bytecode interpreter, no class-loading machinery
resident at runtime, because none of it exists in a native-image binary;
the Graal compiler already did all of that work once, at build time.

**3. Real artifact sizes:**

```
main.jar:      1,681 bytes    (needs a separate JVM installed to run)
app.jsa:       1,835,008 bytes (a supplementary archive, still needs the JVM + main.jar)
main-native:   17,611,240 bytes (fully self-contained -- no JVM needed on the target machine)
```

The native binary is much larger on disk than the jar alone, but it is the
**entire runtime**, not just the application — a genuinely different
deployment shape (no JVM to install or version-match on the target).
