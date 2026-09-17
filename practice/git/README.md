# Git — Practice

Real Git internals demos referenced from `18-engineering-practices/git-internals-and-collaboration-workflows.md`. Each directory has a `setup.sh` that builds a real, throwaway repository and a `transcript.txt` capturing the real command output.

| Directory | Contents |
|---|---|
| [`object-model/`](object-model/) | Real `git cat-file`/`git hash-object` inspection of blobs, trees, and commits |
| [`merge-vs-rebase/`](merge-vs-rebase/) | The same real branch divergence resolved both ways, with the resulting real commit graphs compared |
| [`reflog-and-bisect/`](reflog-and-bisect/) | Real recovery of a "lost" commit via `git reflog`, and a real `git bisect` run against a deliberately introduced regression |
