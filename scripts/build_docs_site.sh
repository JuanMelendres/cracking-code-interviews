#!/bin/bash
# Mirrors the real content directories into docs/ as plain files (not
# symlinks) so mkdocs-awesome-pages-plugin's .pages files are discoverable
# -- it does not follow symlinks when scanning for them, even though
# MkDocs' own content walker does. The mirrored folders are gitignored and
# fully regenerated every run; the only hand-authored files under docs/
# (index.md, .pages, stylesheets/) are left untouched.
set -euo pipefail
cd "$(dirname "$0")/.."

MIRROR_DIRS=(syllabus study-packs cheat-sheets flashcards architecture-atlas production-cookbook)

for d in "${MIRROR_DIRS[@]}"; do
  rm -rf "docs/$d"
  rsync -a --exclude='.git' "$d/" "docs/$d/"
done

echo "Mirrored ${#MIRROR_DIRS[@]} directories into docs/. Run: .venv-docs/bin/mkdocs serve"
