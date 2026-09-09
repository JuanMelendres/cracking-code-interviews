#!/bin/bash
# Mirrors the real content directories into docs/ as plain files (not
# symlinks) so mkdocs-awesome-pages-plugin's .pages files are discoverable
# -- it does not follow symlinks when scanning for them, even though
# MkDocs' own content walker does. The mirrored folders are gitignored and
# fully regenerated every run; the only hand-authored files under docs/
# (index.md, .pages, stylesheets/) are left untouched.
set -euo pipefail
cd "$(dirname "$0")/.."

MIRROR_DIRS=(syllabus study-packs cheat-sheets flashcards architecture-atlas production-cookbook practice)

for d in "${MIRROR_DIRS[@]}"; do
  rm -rf "docs/$d"
  rsync -a \
    --exclude='.git' \
    --exclude='node_modules' \
    --exclude='dist' \
    --exclude='.next' \
    --exclude='out' \
    --exclude='target' \
    --exclude='*.class' \
    "$d/" "docs/$d/"
done

# MkDocs turns every README.md into that directory's index.html, but a
# markdown link that explicitly targets ".../README.md" doesn't get
# rewritten to match -- it 404s. Linking to the bare directory ("path/")
# instead works and is also perfectly valid on GitHub, so rewrite only the
# link *target* (not the visible label text) in the mirrored copy -- the
# canonical source files keep the explicit README.md links, since those
# are correct for normal GitHub browsing.
python3 - <<PYEOF
import re, pathlib

mirror_dirs = "${MIRROR_DIRS[@]}".split()
pattern = re.compile(r'(\]\([^)\s]*?)/README\.md(\))')
count = 0
for d in mirror_dirs:
    for path in pathlib.Path("docs", d).rglob("*.md"):
        text = path.read_text(encoding="utf-8")
        new_text, n = pattern.subn(r"\1/\2", text)
        if n:
            path.write_text(new_text, encoding="utf-8")
            count += n
print(f"Rewrote {count} README.md link targets to bare directory links (site build only)")
PYEOF

echo "Mirrored ${#MIRROR_DIRS[@]} directories into docs/. Run: .venv-docs/bin/mkdocs serve"
