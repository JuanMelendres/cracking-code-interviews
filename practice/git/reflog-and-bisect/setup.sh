#!/bin/sh
# Real, reproducible demo of two "oh no" recovery/diagnosis tools:
# reflog (recovering from a destructive reset) and bisect (finding the exact
# commit that introduced a regression via automated binary search).
set -e
export LC_ALL=C

echo "########## PART A: reflog recovery from a hard reset ##########"
REFLOG_DIR="$(mktemp -d)"
cd "$REFLOG_DIR"
git init -q -b main
git config user.email "demo@example.com"
git config user.name "Demo"

echo "v1" > work.txt
git add . && git commit -q -m "important work: v1"
echo "v2" > work.txt
git add . && git commit -q -m "important work: v2"
echo "v3" > work.txt
git add . && git commit -q -m "important work: v3"

echo "=== before disaster: 3 commits ==="
git log --oneline

echo
echo "=== disaster: git reset --hard to the FIRST commit, discarding v2 and v3 from the branch tip ==="
FIRST_HASH=$(git log --oneline | tail -1 | cut -d' ' -f1)
git reset --hard "$FIRST_HASH"
echo "branch now shows only:"
git log --oneline

echo
echo "=== v2 and v3 are NOT gone -- reflog still has every ref update, including the reset itself ==="
git reflog

echo
echo "=== recover: reset --hard to the reflog entry BEFORE the disaster ==="
git reset --hard 'HEAD@{1}'
echo "=== v3 is back ==="
git log --oneline

echo
echo "########## PART B: bisect finds the exact commit that introduced a bug ##########"
BISECT_DIR="$(mktemp -d)"
cd "$BISECT_DIR"
git init -q -b main
git config user.email "demo@example.com"
git config user.name "Demo"

cat > check.sh << 'SCRIPT'
#!/bin/sh
# "the test": exits 0 (pass) if the value line equals 5, exits 1 (fail) otherwise
VALUE=$(head -1 result.txt)
[ "$VALUE" = "5" ]
SCRIPT
chmod +x check.sh

printf "5\n# C1\n" > result.txt
git add . && git commit -q -m "C1: add(2,3) returns 5, correct"
printf "5\n# C2 refactor\n" > result.txt
git add . && git commit -q -m "C2: refactor, still correct"
printf "6\n# C3 bug\n" > result.txt
git add . && git commit -q -m "C3: BUG -- off-by-one introduced, add(2,3) now returns 6"
printf "6\n# C4 docs\n" > result.txt
git add . && git commit -q -m "C4: unrelated docs change, bug still present"
printf "6\n# C5 formatting\n" > result.txt
git add . && git commit -q -m "C5: unrelated formatting, bug still present"

echo "=== 5 commits, bug introduced somewhere in the middle, unknown to bisect ==="
git log --oneline

FIRST=$(git log --oneline | tail -1 | cut -d' ' -f1)
echo
echo "=== git bisect run: automated binary search using check.sh as the pass/fail oracle ==="
git bisect start HEAD "$FIRST"
git bisect run ./check.sh
echo
git bisect reset

echo
echo "Reflog-demo repo: $REFLOG_DIR"
echo "Bisect-demo repo: $BISECT_DIR"
