#!/usr/bin/env bash
# Real git transcripts contrasting trunk-based development with a release-branch
# model, and showing what a hotfix costs in each. Output captured in
# output-transcript.txt.
set -u
export GIT_AUTHOR_NAME="Demo" GIT_AUTHOR_EMAIL="demo@example.com"
export GIT_COMMITTER_NAME="Demo" GIT_COMMITTER_EMAIL="demo@example.com"

work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT

c() { echo "$2" >> "$1"; git add -A; git commit -q -m "$3"; }

echo "================================================================"
echo "A. TRUNK-BASED: short-lived branches, one integration point"
echo "================================================================"
mkdir -p "$work/trunk" && cd "$work/trunk" && git init -q -b main
c app.txt "v1 base" "feat: initial version"
git tag -a v1.0.0 -m "1.0.0"

git checkout -q -b feat/pricing
c app.txt "pricing" "feat: pricing rules"
git checkout -q main
git merge -q --no-ff feat/pricing -m "Merge feat/pricing"
git branch -q -d feat/pricing
git tag -a v1.1.0 -m "1.1.0"

git checkout -q -b feat/tax
c app.txt "tax" "feat: tax table"
git checkout -q main
git merge -q --no-ff feat/tax -m "Merge feat/tax"
git branch -q -d feat/tax
git tag -a v1.2.0 -m "1.2.0"

echo "\$ git log --oneline --graph --decorate --all"
git log --oneline --graph --decorate --all
echo
echo "\$ git tag -n1"
git tag -n1
echo
echo "Branches alive right now:"
git branch --format='  %(refname:short)'
echo "Each feature lived hours-to-days and merged into one line. There is exactly"
echo "one place to ask 'what is in production': the tag on main."

echo
echo "================================================================"
echo "B. RELEASE-BRANCH MODEL: a hotfix must be applied twice"
echo "================================================================"
mkdir -p "$work/release" && cd "$work/release" && git init -q -b main
c app.txt "v1 base" "feat: initial version"
git checkout -q -b release/1.0
git tag -a v1.0.0 -m "1.0.0"

git checkout -q main
c app.txt "new feature for 1.1" "feat: work continues on main"

echo "Production bug found in 1.0, while main has already moved on."
git checkout -q release/1.0
c app.txt "hotfix: clamp negative totals" "fix: clamp negative totals"
git tag -a v1.0.1 -m "1.0.1"
hotfix="$(git rev-parse --short HEAD)"
echo "\$ git log --oneline release/1.0"
git log --oneline release/1.0

echo
echo "The fix exists ONLY on release/1.0. main is still broken:"
echo "\$ git log --oneline main"
git log --oneline main
echo
echo "\$ git checkout main && git cherry-pick $hotfix"
git checkout -q main
git cherry-pick "$hotfix" 2>&1 | sed 's/^/  /'
echo
echo "The cherry-pick CONFLICTED -- back-porting is not free. Resolving it the way"
echo "a human would, keeping both main's new work and the hotfix line:"
printf 'v1 base\nnew feature for 1.1\nhotfix: clamp negative totals\n' > app.txt
git add app.txt
git -c core.editor=true cherry-pick --continue >/dev/null 2>&1
echo "\$ git log --oneline --graph --decorate --all"
git log --oneline --graph --decorate --all
echo
echo "Note the two DIFFERENT commit hashes for the identical change:"
git log --all --oneline --grep="clamp negative totals"
echo "A cherry-pick copies the change onto a new commit, so the two lines now"
echo "carry the same fix under different identities -- which is why a forgotten"
echo "back-merge silently reintroduces a fixed bug in the next release."

echo
echo "================================================================"
echo "C. SEMANTIC VERSIONING: what each bump is a PROMISE about"
echo "================================================================"
printf '%-10s %-42s %s\n' "BUMP" "MEANING" "EXAMPLE"
printf '%-10s %-42s %s\n' "MAJOR" "a caller must change to keep working" "1.2.3 -> 2.0.0"
printf '%-10s %-42s %s\n' "MINOR" "new capability, existing callers unaffected" "1.2.3 -> 1.3.0"
printf '%-10s %-42s %s\n' "PATCH" "bug fix only, no interface change" "1.2.3 -> 1.2.4"
echo
echo "The version is a compatibility claim to consumers, not a measure of effort."
echo "A one-line change that alters a response field is MAJOR; a thousand-line"
echo "internal refactor with no API change is PATCH."
