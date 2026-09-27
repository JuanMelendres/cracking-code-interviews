# Java File I/O and NIO.2 — Real Demo

Backs [`syllabus/02-java/language-core/java-file-io-and-nio2.md`](../../../../syllabus/02-java/language-core/java-file-io-and-nio2.md) (T-2429).

Pure JDK, no dependencies.

## Run it

```bash
mkdir -p out
javac -d out src/FileIoDemo.java
java -cp out FileIoDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **Buffered vs. unbuffered reads** — a real, measured 4.6x speedup reading
  the identical 200,000-line file char-by-char through a `BufferedReader`
  versus a raw `FileReader`, direct evidence of the syscall-overhead cost
  buffering exists to eliminate.
- **A real, silent charset mismatch** — writing `"café résumé naïve"` as
  UTF-8 and reading it back with `ISO-8859-1` produces genuine mojibake
  (`"cafÃ© rÃ©sumÃ© naÃ¯ve"`) with no exception thrown at all — the wrong
  charset is a silent-corruption bug, not a fail-fast one.
- **A real suppressed exception** — a `try`-with-resources body that throws,
  paired with a `close()` that also throws, proves the `close()` failure is
  recorded on `getSuppressed()` rather than replacing or silently dropping
  the original exception.
- **NIO.2 basics** — `Files.write()`/`Files.readAllLines()`/`Files.size()`
  round-tripping real content, as the modern, `Path`-based replacement for
  the equivalent `java.io.File`-based code.
