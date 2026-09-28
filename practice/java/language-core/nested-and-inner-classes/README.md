# Nested and Inner Classes — Real Demos

Backs [`syllabus/02-java/language-core/nested-and-inner-classes.md`](../../../../syllabus/02-java/language-core/nested-and-inner-classes.md) (T-2431).

Pure JDK, no dependencies.

## Run it

```bash
mkdir -p out
javac -d out src/NestedClassesDemo.java src/InnerClassLeakDemo.java
java -cp out NestedClassesDemo
java -cp out InnerClassLeakDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

**`NestedClassesDemo.java` — the four forms and how they actually differ**

- A static nested class constructs with plain `new Config(3)`; an inner class requires an enclosing instance (`outer.new Session()`).
- Reflection over both classes prints their real declared fields: `Config` has only its own `int retries`, while `Session` carries `NestedClassesDemo this$0  synthetic=true`. The demo then proves that field points at the exact outer instance (`referenced == outer` is `true`).
- `NestedClassesDemo.this.label` returns `OUTER-label` where the unqualified `label` returns `INNER-label` — real shadowing, real disambiguation.
- Serializing a `Serializable` **static nested** class succeeds; serializing a `Serializable` **inner** class whose outer is not serializable throws a real `NotSerializableException: NestedClassesDemo`, because the synthetic outer reference is part of the object's state.
- Local and anonymous classes capture an effectively-final local and report their real runtime names: `NestedClassesDemo$1Multiplier` and `NestedClassesDemo$1`. Neither appears in `getDeclaredClasses()`.

**`InnerClassLeakDemo.java` — when the outer reference is actually retained**

Three services, each owning an 8 MB buffer, each registering a callback into a long-lived static list, each then tracked through a `WeakReference` across GC:

- **A — inner class that reads an enclosing field:** `this$0` is emitted (`javap` confirms `final InnerClassLeakDemo$LeakyService this$0;`), and the service is **not** collected. The 8 MB stays reachable through a callback nobody unregistered.
- **B — inner class that never touches the outer instance:** javac 21 emits **no** `this$0` field at all, and the service **is** collected. The constructor still takes the outer instance, but discards it — `javap` output for both callbacks is included in the transcript.
- **C — static nested class:** no outer reference by construction, collected.

Case B is worth knowing precisely because it contradicts the common blanket claim that "an inner class always retains its outer instance." On a modern JDK that retention is conditional on actually using the enclosing instance. The leak in case A is real; the folklore version of it is not universal.

A second real detail discovered while building this: the first version of case A used `private final String name = "leaky"`, a compile-time constant, which javac inlined into the inner class body — so no outer access remained, no `this$0` was emitted, and the "leak" did not reproduce. Assigning the field in the constructor instead made it a genuine runtime read, and the retention appeared. Constant-folding can hide this whole effect.
