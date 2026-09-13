#!/bin/sh
# Real, reproducible demo of git's object model: blobs, trees, commits, and
# content-addressable storage. Run in a scratch directory -- this script
# creates its own repo under /tmp and does not touch anything else.
set -e
DEMO_DIR="$(mktemp -d)"
cd "$DEMO_DIR"

git init -q -b main
git config user.email "demo@example.com"
git config user.name "Demo"

echo "hello world" > file.txt
BLOB_HASH=$(git hash-object -w file.txt)
echo "=== blob hash for 'hello world' ==="
echo "$BLOB_HASH"

echo "=== git cat-file -t (type) and -p (content) ==="
git cat-file -t "$BLOB_HASH"
git cat-file -p "$BLOB_HASH"

git add file.txt
git commit -q -m "first commit"

echo
echo "=== identical content in a second file produces the SAME blob hash (content-addressable) ==="
cp file.txt file-copy.txt
git add file-copy.txt
git commit -q -m "add duplicate content file"
git cat-file -p HEAD^{tree}

echo
echo "=== commit object content: tree pointer + parent pointer + author/committer ==="
git cat-file -p HEAD

echo
echo "=== the commit hash is itself derived from its content (tree+parent+message+timestamps) ==="
echo "HEAD commit hash: $(git rev-parse HEAD)"

echo
echo "=== a commit's hash changes if ANY of that content changes -- amend and compare ==="
OLD_HASH=$(git rev-parse HEAD)
git commit -q --amend -m "add duplicate content file (amended message)"
NEW_HASH=$(git rev-parse HEAD)
echo "before amend: $OLD_HASH"
echo "after amend:  $NEW_HASH"
echo "same tree, different commit hash (message changed): $([ "$OLD_HASH" != "$NEW_HASH" ] && echo true || echo false)"

echo
echo "Demo repo left at: $DEMO_DIR (delete manually when done inspecting)"
