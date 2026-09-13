#!/bin/sh
# Real, reproducible demo contrasting `git merge` and `git rebase` on the
# SAME starting divergence, including a genuine conflict on both paths.
set -e
export LC_ALL=C
BASE_DIR="$(mktemp -d)"
cd "$BASE_DIR"

git init -q -b main
git config user.email "demo@example.com"
git config user.name "Demo"

echo "line1" > shared.txt
git add . && git commit -q -m "C1: initial file"
echo "line1" > feature-only.txt
git add . && git commit -q -m "C2: unrelated feature groundwork"

git checkout -q -b feature
printf "line1\nline2 (feature)\n" > shared.txt
git add . && git commit -q -m "C3 (feature): add line2"

git checkout -q main
printf "line1\nline2 (main, different)\n" > shared.txt
git add . && git commit -q -m "C4 (main): diverging change to same line"

echo "=== history graph BEFORE merge/rebase: two divergent branches ==="
git log --oneline --graph --all

echo
echo "########## PATH A: git merge ##########"
cp -r "$BASE_DIR" "$BASE_DIR-merge"
cd "$BASE_DIR-merge"
git checkout -q main
echo "=== git merge feature (real conflict, both touched the same line) ==="
git merge feature 2>&1 || true
echo
echo "=== conflict markers in shared.txt ==="
cat shared.txt
printf "line1\nline2 (resolved: keep both intents)\n" > shared.txt
git add shared.txt
git commit -q -m "M: merge feature into main, resolve conflict"
echo "=== history AFTER merge: a merge commit with TWO parents preserves both branch histories exactly as they happened ==="
git log --oneline --graph --all
echo "=== merge commit parent list (real DAG fact) ==="
git cat-file -p HEAD | grep parent

echo
echo "########## PATH B: git rebase (same starting point) ##########"
cd "$BASE_DIR"
git checkout -q feature
echo "=== git rebase main (from feature branch): replay C3 on top of C4 ==="
git rebase main 2>&1 || true
echo
echo "=== same conflict, but hit during the replay, not at a merge point ==="
cat shared.txt
printf "line1\nline2 (resolved: keep both intents)\n" > shared.txt
git add shared.txt
git rebase --continue 2>&1
echo
echo "=== history AFTER rebase: LINEAR -- feature's commit is a NEW object replayed on top of main, no merge commit ==="
git log --oneline --graph --all

echo
echo "Merge-path repo:  $BASE_DIR-merge"
echo "Rebase-path repo: $BASE_DIR"
