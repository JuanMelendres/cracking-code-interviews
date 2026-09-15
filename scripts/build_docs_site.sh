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
    --exclude='*.jar' \
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

# mkdocs-git-revision-date-localized-plugin resolves each page's real commit
# history via `os.path.realpath()` on its docs_dir path, then runs `git log`
# on that resolved path -- an rsync *copy* under docs/ has no git history of
# its own (it's gitignored and regenerated every run), so the plugin would
# silently fall back to today's build date for every single page. Fix: any
# mirrored .md file whose content is byte-identical to its real source (i.e.
# untouched by the README.md rewrite above) is replaced with a relative
# symlink to that source file instead of a copy -- realpath() then resolves
# straight through to the real, git-tracked file, and the plugin sees its
# actual commit history. Files the rewrite step *did* change are left as
# real copies (symlinking would serve the original, un-rewritten content and
# reintroduce the README.md 404 this script already fixes for them); those
# pages fall back to the build date, a known and accepted gap. Directories
# and .pages files are never touched, so awesome-pages-plugin's discovery
# (which does not follow symlinked directories) is unaffected.
python3 - <<PYEOF
import os
import pathlib

mirror_dirs = "${MIRROR_DIRS[@]}".split()
symlinked = 0
for d in mirror_dirs:
    for mirrored in pathlib.Path("docs", d).rglob("*.md"):
        source = pathlib.Path(d, mirrored.relative_to(pathlib.Path("docs", d)))
        if not source.is_file():
            continue
        if mirrored.read_bytes() != source.read_bytes():
            continue
        mirrored.unlink()
        relative_target = os.path.relpath(source, start=mirrored.parent)
        mirrored.symlink_to(relative_target)
        symlinked += 1
print(f"Symlinked {symlinked} unmodified mirrored files back to their real source (real git history for git-revision-date-localized)")
PYEOF

# Regenerates docs/assets/pack-schedule-index.json (a real link reverse-
# index, not committed -- see that script's own docstring) from
# study-packs/**/README.md, consumed by javascripts/chapter-context.js.
python3 scripts/generate_pack_schedule_index.py

# Regenerates docs/assets/learning-path-next-index.json (also not
# committed) from syllabus/00-overview/learning-paths/*.md's own
# real, already-ordered topic lists -- consumed by the same script.
python3 scripts/generate_learning_path_next_index.py

echo "Mirrored ${#MIRROR_DIRS[@]} directories into docs/. Run: .venv-docs/bin/mkdocs serve"
