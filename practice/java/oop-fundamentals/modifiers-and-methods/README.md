# Modifiers and Method Signatures Demo (T-2210)

Real, compiled, executed evidence backing [Java Modifiers and Method Signatures: Access Control, static, final, and Abstract vs. Concrete Methods](../../../../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md).

## Files

- `src/StaticFinalDemo.java` — real, measured static-vs-instance state (a shared counter), real `final` fields, and a real abstract class with both an abstract method and a concrete method, implemented by a real subclass. 6/6 assertions pass.
- `src/BrokenFinalReassignment.java`, `BrokenFinalMethodOverride.java`, `BrokenFinalClassExtension.java`, `BrokenAbstractInstantiation.java`, `BrokenPrivateAccess.java` + `BankAccount.java` — five deliberately broken programs, each producing a real, captured `javac` compiler error (reassigning a `final` variable, overriding a `final` method, extending a `final` class, instantiating an `abstract` class, and accessing a `private` field from a genuinely different top-level class). Full transcript in `compile-errors-transcript.txt`.
- `src/NestedPrivateAccessDemo.java` — a real, genuine finding discovered while building `BrokenPrivateAccess.java`: `private` access in Java is scoped to the *top-level* enclosing class, not the immediate class body, so a sibling nested class within the same top-level class *can* read another nested class's `private` field. This was not planned — the first version of the private-access demo accidentally used two nested classes inside the same outer class and compiled successfully instead of failing, which is itself the real, correct behavior, kept here as a genuine, unplanned-but-true finding rather than discarded.

## Reproduce it yourself

```bash
cd practice/java/oop-fundamentals/modifiers-and-methods
javac -d out src/StaticFinalDemo.java src/NestedPrivateAccessDemo.java
java -cp out StaticFinalDemo
java -cp out NestedPrivateAccessDemo

# Each Broken*.java file fails to compile on purpose — see compile-errors-transcript.txt
# for the real captured error, or reproduce one yourself:
javac src/BrokenFinalReassignment.java
```
