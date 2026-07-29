# Week 3 Java — Tree Problems — runnable verification

Compiled and run on OpenJDK 21.0.12. Same hand-rolled `Check` harness as Weeks 1–2.

## Reproduce

```bash
cd practice/java/week-03/trees
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

## Files

| File | Corresponds to |
|---|---|
| `TreeNode.java` | Shared binary tree node |
| `TreeProblems.java` | LC 104, 226, 98, 235, 102, 199 |
| `Main.java` | Runs all 11 assertions |

## Real output (last run)

```
  PASS  LC104 maxDepth on the canonical example
  PASS  LC104 maxDepth of empty tree
  PASS  LC226 root.left is now the old root.right
  PASS  LC226 root.right is now the old root.left
  PASS  LC226 grandchild swapped correctly
  PASS  LC98 simple valid BST
  PASS  LC98 catches the ancestor-bound trap (local check alone would miss this)
  PASS  LC235 LCA of 2 and 8 is the root
  PASS  LC235 LCA of 2 and 4 is 2 itself (ancestor case)
  PASS  LC102 level order on the canonical example
  PASS  LC199 right side view on the canonical example
Week 3 tree suite: 11/11 assertions passed
```
