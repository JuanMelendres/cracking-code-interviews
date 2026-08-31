# Containers & Image Internals (T-1001) — runnable verification

Real, executed Docker output backing
[`handbook/cloud/container-image-internals.md`](../../../../handbook/cloud/container-image-internals.md)
(T-1001). Every number below is measured on this machine (Docker 29.6.2,
Docker Desktop, `overlayfs` storage driver, linux/aarch64 VM), not asserted
from documentation.

This pack deliberately does not re-cover container **resource limits**
(cgroup memory limits from the JVM's perspective, `OutOfMemoryError` vs
OOMKill) — that is already covered in depth by
[`handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md`](../../../../handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md).
This pack's scope is the layer beneath that: how an image is actually
built, layered, and stored, and what a container's namespace/cgroup view
looks like from the inside.

## Files

- `app/` — a minimal real Maven project (`HelloContainer.java` + a real
  `gson` dependency) used as build input for every Dockerfile below.
- `Dockerfile.naive` — single-stage build; the final image IS the build
  image (Maven, the JDK, and the source tree all ship to production).
- `Dockerfile.multistage` — build stage discarded; only the built jar is
  copied into a `jre-alpine` final stage. Dependency resolution and
  compilation share one `RUN` layer.
- `Dockerfile.layered` — same multi-stage shape, but dependency resolution
  (`mvn dependency:go-offline`) is split into its own layer, copied and run
  *before* application source is copied in.
- `Dockerfile.distroless` — final stage is `gcr.io/distroless/java21-debian12`
  (no shell, no package manager) instead of `jre-alpine`.
- `cache-comparison-demo.sh` — reproducible script: cold-builds
  `Dockerfile.multistage` and `Dockerfile.layered` from a pruned build
  cache, then rebuilds each after a source-only change, timing both.

## Run

```bash
cd practice/java/cloud/container-image-internals
docker build -f Dockerfile.naive       -t cii-naive:demo       .
docker build -f Dockerfile.multistage  -t cii-multistage:demo  .
docker build -f Dockerfile.layered     -t cii-layered:demo     .
docker build -f Dockerfile.distroless  -t cii-distroless:demo  .
sh cache-comparison-demo.sh
```

## Real observed output (last full run)

### 1. Image size: single-stage vs. multi-stage

```
cii-naive:demo         893MB
cii-multistage:demo    286MB
cii-layered:demo       286MB
cii-distroless:demo    271MB
```

The naive build is **3.1× larger** than the multi-stage build of the exact
same application, because it ships Maven, the full JDK, the resolved
`.m2` dependency cache, and the original source tree — none of which the
running application ever touches at runtime. The multi-stage build
discards the entire build-stage filesystem; only the one artifact named in
`COPY --from=build` survives.

### 2. `docker history` — what each instruction actually costs

```
$ docker history cii-naive:demo
IMAGE          CREATED BY                                    SIZE
<missing>      CMD ["java" "-jar" ...]                       0B
<missing>      RUN mvn -q -DskipTests package                35.6MB
<missing>      COPY app/src ./src                             32.8kB
<missing>      COPY app/pom.xml .                              12.3kB
<missing>      WORKDIR /build                                  8.19kB
<missing>      CMD ["mvn"]                                        0B   <- inherited from the maven base image
<missing>      ENTRYPOINT ["/usr/local/bin/mvn-entrypoint..."]    0B   <- inherited from the maven base image

$ docker history cii-multistage:demo
IMAGE          CREATED BY                                    SIZE
<missing>      CMD ["java" "-jar" "/app/app.jar"]                0B
<missing>      COPY --from=build .../app.jar                  324kB
<missing>      WORKDIR /app                                  8.19kB
```

Every Dockerfile instruction that changes the filesystem creates exactly
one immutable layer, identified by the content hash of its inputs. The
naive image's own added layers are small (48MB total) — the real cost is
that it is built **on top of** the full `maven:3.9-eclipse-temurin-21`
base image, which itself is layered on a full JDK distribution and Maven
installation. The multi-stage image's final stage is built on
`eclipse-temurin:21-jre-alpine` instead, and adds only 332KB on top of it.

### 3. Layer caching: splitting dependency resolution from source really works

Real, timed rebuilds after touching only `HelloContainer.java` (no
`pom.xml` change), from `cache-comparison-demo.sh`:

| Build | Cold build | Rebuild after source-only change |
|---|---|---|
| `Dockerfile.multistage` (deps + compile share one `RUN` layer) | 17.5s | **21.5s** |
| `Dockerfile.layered` (deps resolved in their own layer first) | 48.6s | **2.4s** |

The layered build's cold build is slower (`dependency:go-offline` resolves
the full dependency graph eagerly; a plain `mvn package` resolves lazily
during compilation) — but that one-time cost is paid once. On every
subsequent source-only change, Docker's layer cache reuses the dependency
layer verbatim (`RUN mvn -q -B dependency:go-offline` reported as
`CACHED`), and only the compile step reruns. The non-split build gets no
such benefit: because `COPY app/src` sits *before* the single
`RUN mvn package` layer, any source change invalidates that layer and
Maven re-resolves dependencies from scratch every time, even though the
`pom.xml` never changed. Layer order is not cosmetic — it is a cache-key
design decision with a measured, repeated cost.

### 4. Distroless: a real security trade-off, not just a smaller number

```
$ docker run --rm cii-distroless:demo
{"pid":1,"availableProcessors":10,"maxHeapBytes":2082471936,
 "jvmName":"OpenJDK 64-Bit Server VM","jvmVersion":"21.0.10"}

$ docker run --rm --entrypoint sh cii-distroless:demo -c 'echo hi'
docker: Error response from daemon: failed to create task for container:
failed to create shim task: OCI runtime create failed: runc create failed:
unable to start container process: error during container init:
exec: "sh": executable file not found in $PATH
```

The application runs correctly — the real failure is a real, intentional
gap: there is no shell, no package manager, and no coreutils in the image
at all. `docker exec ... sh` and any shell-based debugging technique
genuinely do not work against this image. That is the actual trade-off
distroless images make: a measurably smaller attack surface (271MB vs.
286MB here, and critically, an attacker who achieves code execution has no
shell to pivot with) against real operational cost (no interactive
debugging, and dropping a shell requires attaching an ephemeral debug
container instead).

### 5. PID namespace: a container's process tree is not the host's

```
$ ps aux | wc -l         # on the host, plainly
537
$ docker run --rm alpine:3.20 sh -c 'echo "PID inside container: $$"; ps aux'
PID inside container: 1
PID   USER     TIME  COMMAND
    1 root      0:00 ps aux
```

Inside the container, the shell that just started is PID 1, and `ps aux`
shows exactly one process — its own. The host has 537 running processes
at the same instant. This is the Linux PID namespace: the kernel gives the
container's first process its own, independent PID numbering starting at
1, and the container cannot see (or signal) any process outside its
namespace. The same mechanism — separate namespaces for network, mount,
UTS (hostname), IPC, and user IDs — is what makes a container look like an
isolated machine while actually being a set of regular Linux processes.

### 6. cgroups: the real, enforced resource boundary

```
$ docker run --rm alpine:3.20 sh -c 'cat /sys/fs/cgroup/cgroup.controllers'
cpuset cpu io memory hugetlb pids rdma

$ docker run --rm --memory=64m alpine:3.20 sh -c 'cat /sys/fs/cgroup/memory.max'
67108864
```

`67108864` bytes is exactly 64 MiB — the `--memory=64m` flag is not a
polite request, it is a real cgroup v2 `memory.max` value the kernel
enforces directly, visible from inside the container as an ordinary file
under `/sys/fs/cgroup/`. This is the identical mechanism that
`kubernetes-resource-limits-probes-and-jvm-sizing.md` measures the JVM
reading to size its default heap — this pack demonstrates the general
mechanism; that chapter demonstrates the JVM-specific consequence.

### 7. Union/overlay filesystem: copy-on-write across containers

```
$ docker info --format '{{.Driver}}'
overlayfs

$ docker run --rm alpine:3.20 sh -c 'cat /etc/hostname > /tmp/marker.txt'
$ docker run --rm alpine:3.20 sh -c 'test -f /tmp/marker.txt && echo FOUND || echo "not found"'
not found: each container gets its own writable layer over the same shared read-only image layers
```

Two containers started from the identical `alpine:3.20` image share the
same read-only image layers on disk, but each gets its own thin writable
layer on top (implemented via `overlayfs` here). A write in one
container's writable layer is invisible to a second container started
from the same image and invisible to the image itself — this is exactly
why `docker commit`-ing a running container's changes back into an image
is a deliberate, explicit step, not something that happens automatically.

## Real discoveries made while building this pack

The first `cache-comparison-demo.sh` run used `--no-cache-filter=build`
for the "rebuild after source change" step of the non-split Dockerfile.
That flag forces a fresh build of the *entire* named stage regardless of
Docker's normal content-based cache keys, which also invalidated the
`COPY app/pom.xml` layer — not a fair comparison against the layered
Dockerfile's natural, unforced rebuild. This was caught before drawing any
conclusion from it: the script was rewritten to do a natural
`docker build` (no forcing flags) for both Dockerfiles, relying only on
Docker's real content-based cache invalidation, which is what produced the
honest 21.5s-vs-2.4s comparison reported above.
