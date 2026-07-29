#!/usr/bin/env python3
"""
Repository validation for cracking-code-interviews.

Invoked by scripts/validate.sh. Exit 0 = pass (warnings allowed), 1 = errors.
"""
from __future__ import annotations
import os, re, sys, subprocess, shutil
from collections import defaultdict

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.chdir(ROOT)

errors: list[str] = []
warnings: list[str] = []
notes: list[str] = []


def err(m):  errors.append(m);   print(f"  ERROR   {m}")
def warn(m): warnings.append(m); print(f"  WARN    {m}")
def note(m): notes.append(m);    print(f"  note    {m}")
def ok(m):   print(f"  ok      {m}")
def head(n, t): print(f"\n[{n}] {t}")


def md_files():
    out = []
    for dirpath, dirnames, filenames in os.walk("."):
        dirnames[:] = [d for d in dirnames if d not in {".git", "node_modules", ".venv"}]
        for fn in filenames:
            if fn.endswith(".md"):
                out.append(os.path.join(dirpath, fn))
    return sorted(out)


def strip_fences(text):
    """Yield (lineno, line) for lines outside fenced code blocks."""
    inside = False
    for i, line in enumerate(text.split("\n"), 1):
        if line.lstrip().startswith("```"):
            inside = not inside
            continue
        if not inside:
            yield i, line


FILES = md_files()

print("=" * 60)
print(" cracking-code-interviews · validation")
print(f" {len(FILES)} markdown files")
print("=" * 60)

# ---------------------------------------------------------------- 1
head(1, "Readability (UTF-8)")
texts = {}
bad = False
for f in FILES:
    try:
        texts[f] = open(f, encoding="utf-8").read()
    except UnicodeDecodeError as e:
        err(f"invalid UTF-8: {f} ({e})"); bad = True
    except OSError as e:
        err(f"unreadable: {f} ({e})"); bad = True
if not bad:
    ok(f"all {len(FILES)} files readable, valid UTF-8")

# ---------------------------------------------------------------- 2
head(2, "Code fence balance")
bad = False
for f, t in texts.items():
    n = sum(1 for l in t.split("\n") if l.lstrip().startswith("```"))
    if n % 2:
        err(f"unbalanced fences ({n}) in {f}"); bad = True
if not bad:
    ok("all code fences balanced")

# ---------------------------------------------------------------- 3
head(3, "Diagram fences")
DIAGRAM = re.compile(r"^(graph |flowchart |sequenceDiagram|classDiagram|erDiagram|gantt|stateDiagram)")
bad = False
mermaid_files = 0
for f, t in texts.items():
    lines = t.split("\n")
    if "```mermaid" in t:
        mermaid_files += 1
    for i, l in enumerate(lines):
        if l.strip() == "```" and i + 1 < len(lines) and DIAGRAM.match(lines[i + 1].strip()):
            err(f"unlabelled diagram fence at {f}:{i+1}"); bad = True
if not bad:
    ok(f"all diagrams use ```mermaid ({mermaid_files} files)")

# ---------------------------------------------------------------- 4
head(4, "Java / SQL fence labels")
JAVA_HINT = re.compile(r"^(public |private |protected |class |interface |import java|@Entity|@Override|@Test|@Service)")
SQL_HINT = re.compile(r"^(SELECT |CREATE TABLE|CREATE INDEX|CREATE SCHEMA|INSERT INTO|EXPLAIN |ALTER TABLE|DROP TABLE)", re.I)
java_files = sum(1 for t in texts.values() if "```java" in t)
sql_files = sum(1 for t in texts.values() if "```sql" in t)
bad = False
for f, t in texts.items():
    lines = t.split("\n")
    for i, l in enumerate(lines):
        if l.strip() == "```" and i + 1 < len(lines):
            nxt = lines[i + 1].strip()
            if JAVA_HINT.match(nxt):
                warn(f"possible unlabelled java block at {f}:{i+1}"); bad = True
            elif SQL_HINT.match(nxt):
                warn(f"possible unlabelled sql block at {f}:{i+1}"); bad = True
ok(f"java fences in {java_files} files, sql fences in {sql_files} files")

# ---------------------------------------------------------------- 5
head(5, "Heading structure")
# Accepted house style: an h1 document title may be followed immediately by an
# h3 subtitle line. That is intentional, not a skipped level.
H = re.compile(r"^(#{1,6}) \S")
bad = False
for f, t in texts.items():
    hs = [(ln, len(m.group(1))) for ln, line in strip_fences(t) if (m := H.match(line))]
    if not hs:
        warn(f"no headings at all in {f}"); bad = True
        continue
    if hs[0][1] != 1:
        warn(f"{f} does not start with an h1 (starts h{hs[0][1]})"); bad = True
    prev = 0
    for idx, (ln, lvl) in enumerate(hs):
        if prev and lvl - prev > 1:
            if idx == 1 and prev == 1 and lvl == 3:
                continue  # documented title/subtitle pattern
            warn(f"heading level skip h{prev}->h{lvl} at {f}:{ln}"); bad = True
        prev = lvl
if not bad:
    ok("heading outlines well-formed (h1 title + h3 subtitle accepted as house style)")

# ---------------------------------------------------------------- 6
head(6, "Duplicate filenames")
by_name = defaultdict(list)
for f in FILES:
    by_name[os.path.basename(f)].append(f)
dupes = {k: v for k, v in by_name.items() if len(v) > 1}
if dupes:
    for name, paths in sorted(dupes.items()):
        parents = {p.split(os.sep)[-2] for p in paths}
        # Same basename in different week/domain dirs is the intended layout.
        if len(parents) == len(paths):
            note(f"'{name}' appears in {len(paths)} directories (expected: per-week layout)")
        else:
            warn(f"'{name}' duplicated within one directory: {paths}")
else:
    ok("no duplicate basenames")

# ---------------------------------------------------------------- 7
head(7, "Relative link resolution")
LINK = re.compile(r"\[[^\]]*\]\(([^)]+)\)")
bad = False
for f, t in texts.items():
    d = os.path.dirname(f)
    for _, line in strip_fences(t):
        for target in LINK.findall(line):
            if target.startswith(("http://", "https://", "mailto:", "#")):
                continue
            path = target.split("#")[0]
            if not path:
                continue
            if not os.path.exists(os.path.normpath(os.path.join(d, path))):
                err(f"broken relative link in {f} -> {target}"); bad = True
if not bad:
    ok("all relative markdown links resolve")

# ---------------------------------------------------------------- 8
head(8, "Secret scan")
PATTERNS = [
    (r"eyJ[A-Za-z0-9_-]{20,}", "JWT"),
    (r"AKIA[0-9A-Z]{16}", "AWS access key"),
    (r"BEGIN [A-Z ]*PRIVATE KEY", "private key"),
    (r"ghp_[A-Za-z0-9]{30,}", "GitHub PAT"),
    (r"xox[baprs]-[A-Za-z0-9-]{10,}", "Slack token"),
    (r"(access_token|id_token|api[_-]?key|client_secret)=[A-Za-z0-9._-]{15,}", "token in URL"),
    (r"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.(com|net|org|io|es|mx|co\.uk)\b", "email address"),
]
ALLOW = re.compile(r"example\.com|@param|@return|@Entity|@Table|@Override|@Test|@Id\b|@Column|@Service")
scan_ext = (".md", ".java", ".sql", ".json", ".yml", ".yaml", ".sh", ".py")
hits = 0
for dirpath, dirnames, filenames in os.walk("."):
    dirnames[:] = [d for d in dirnames if d != ".git"]
    for fn in filenames:
        if not fn.endswith(scan_ext):
            continue
        p = os.path.join(dirpath, fn)
        try:
            content = open(p, encoding="utf-8", errors="replace").read()
        except OSError:
            continue
        for i, line in enumerate(content.split("\n"), 1):
            if ALLOW.search(line):
                continue
            for pat, label in PATTERNS:
                if re.search(pat, line):
                    err(f"possible {label} at {p}:{i}")
                    hits += 1
if hits == 0:
    ok("no secret patterns detected")
    note("coarse regex only — run gitleaks for real coverage")

if shutil.which("gitleaks"):
    print("\n[8b] Gitleaks")
    r = subprocess.run(["gitleaks", "detect", "--source", ".", "--no-git", "--redact"],
                       capture_output=True, text=True)
    if r.returncode == 0:
        ok("gitleaks: clean")
    else:
        err("gitleaks reported findings — inspect before committing")
        print(r.stdout[-2000:])
else:
    warn("gitleaks not installed — install for proper secret detection")

# ---------------------------------------------------------------- 9
head(9, "Stale prose cross-references")
STALE = re.compile(
    r"`[0-9]{2}-[A-Z][A-Za-z0-9-]*\.md`"
    r"|`Week-0[0-9]/[0-9]{2}`"
    r"|`00-Roadmap\.md`"
    r"|`Blueprint-v1\.1-Corrections\.md`"
    r"|`00-Knowledge-Base-Audit-Report\.md`"
    r"|`01-Knowledge-Architecture-Blueprint\.md`"
)
stale_total = 0
stale_by_file = {}
for f, t in texts.items():
    if f.endswith(("file-mapping.md", "CHANGELOG.md", "validate.py")):
        continue
    n = len(STALE.findall(t))
    if n:
        stale_by_file[f] = n
        stale_total += n
if stale_total:
    warn(f"{stale_total} prose references use pre-normalization filenames across {len(stale_by_file)} files")
    note("documented in 00-project/file-mapping.md; scheduled for fix/normalize-cross-references")
else:
    ok("no stale prose cross-references")

# ---------------------------------------------------------------- summary
print("\n" + "=" * 60)
print(f" errors: {len(errors)}   warnings: {len(warnings)}   notes: {len(notes)}")
print("=" * 60)
sys.exit(1 if errors else 0)
