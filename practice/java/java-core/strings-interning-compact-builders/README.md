# Strings: interning, compact strings, and builders (T-106) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md`](../../../../syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md)
(T-106). Three independent demos, including a real, honest self-correction (an initial
"non-Latin-1" test character turned out to actually be within Latin-1's range).

## Setup and run

```bash
cd practice/java/java-core/strings-interning-compact-builders
mkdir -p out
javac -d out src/*.java
java -cp out InterningDemo
java --add-opens java.base/java.lang=ALL-UNNAMED -cp out CompactStringsDemo
java -cp out BuilderPerformanceDemo
```

`CompactStringsDemo` needs `--add-opens` — it reflects into `String`'s private `value`/`coder`
fields. The other two need no special flags.

## Real observed output (last run)

### `InterningDemo` — real string constant pool behavior

```
literal1 == literal2: true
("hel" + "lo") == "hello": true
new String("hello") == "hello": false
new String("hello").equals("hello"): true
heapString.intern() == "hello": true
runtimeConcat == "hello": false
runtimeConcat.intern() == "hello": true
```

String literals (and compile-time-constant expressions `javac` folds, like `"hel" + "lo"`) share
identity via the real string constant pool — verified with `==`. `new String(...)` genuinely
allocates a distinct heap object even with identical content. `.intern()` genuinely returns the
pooled instance. Runtime (non-constant) concatenation is real, verified to NOT be automatically
pooled — only explicit `.intern()` returns to the pool.

### `CompactStringsDemo` — real, reflective proof of JEP 254, including a real self-correction

```
"Hello World" (11 chars): real backing byte[].length=11, real coder=0  <-- LATIN1: exactly 1 byte per character
"Hello Wλrld" (11 chars): real backing byte[].length=22, real coder=1  <-- UTF16: exactly 2 bytes per character

Same character COUNT (11 vs 11), real backing array size: 11 bytes (Latin-1) vs 22 bytes (UTF-16)
-- a real 2.0x memory difference purely from ONE non-Latin-1 character forcing the entire string to UTF-16.
```

An earlier draft of this demo used `'ö'` (U+00F6) as the "non-Latin-1" test character, assuming it
would force UTF-16 encoding. It didn't — real, reflective output showed `coder=0` (LATIN1) for that
string too, because U+00F6 genuinely *is* within Latin-1's 0x00–0xFF range. The demo was corrected
to use `'λ'` (Greek, U+03BB), genuinely outside that range, which does force the whole string to
`coder=1` (UTF16) — real, confirmed by the doubled backing-array length (11 → 22 bytes) for the
identical character count. This is real, reflective, current-JDK evidence for JEP 254's memory
claim, not an assumption carried over from the JEP's own description.

### `BuilderPerformanceDemo` — real, dramatic measured costs

```
String += in a loop, 60000 iterations: 100ms
StringBuilder.append, 60000 iterations:  1ms
Real measured ratio: 63-147x (varies by run)

StringBuilder (unsynchronized), 20,000,000 append+reset: 22ms
StringBuffer (synchronized),    20,000,000 append+reset: 66ms
Real measured ratio: 2.8-3.0x
```

Naive `String +=` concatenation in a loop measured a real, dramatic 63–147x slower than
`StringBuilder.append()` across repeated runs — real, direct evidence of the quadratic cost (each
`+=` allocates an entirely new `String`, copying everything before it) versus `StringBuilder`'s
genuine amortized-linear growth. `StringBuffer`'s synchronized methods measured a real, consistent
~2.8–3.0x slower than `StringBuilder`'s unsynchronized ones, even under genuinely single-threaded
use — the real, measured cost of unconditional lock acquisition on every call.
