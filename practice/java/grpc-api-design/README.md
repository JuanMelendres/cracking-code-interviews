# gRPC API Design — Real, Executed Demo

Backs [gRPC API Design](../../../syllabus/07-api-design/grpc-api-design.md) (T-918). A real `.proto` file compiled by real `protoc` + the real grpc-java codegen plugin, wired into a real gRPC server and client stub, run over an in-process transport (no sockets needed for the demo; the wire semantics — streaming, deadlines, status codes — are the real gRPC runtime, not a mock).

## Setup

```bash
./fetch-deps.sh          # protoc + grpc-java plugin binaries, grpc-java runtime jars
./generate.sh            # protoc compiles src/main/proto/bookstore.proto -> generated/
mkdir -p out
javac -cp "lib/*" -d out generated/demo/grpc/*.java src/demo/*.java
java -cp "out:lib/*" demo.GrpcApiDemo
```

`fetch-deps.sh` pins the `protoc` and `protoc-gen-grpc-java` binaries to macOS arm64 (`osx-aarch_64`); swap the classifier for another OS/arch.

## Reproduce the transcript

`output-transcript.txt` is the complete, real, unedited output of the last run, matching the chapter's Core Concepts and Comparisons sections — all four gRPC call shapes against the same service defined in `src/main/proto/bookstore.proto`:

1. **Unary** (`GetBook`) — one request, one response.
2. **Unary error path** — a real `StatusRuntimeException` carrying the real `NOT_FOUND` status code, not an HTTP status shoehorned into a body.
3. **Server streaming** (`ListBooks`) — one request, the server pushes multiple `Book` messages over the same call before completing.
4. **Client streaming** (`AddBooks`) — the client sends three `Book` messages over one call; the server's own counter (`serviceImpl.addBooksReceived`) confirms it really received three separate `onNext()` calls before returning one summary.
5. **Bidirectional streaming** (`Chat`) — client and server exchange messages on the same open call, each side's `StreamObserver` firing independently.

## Files

- `src/main/proto/bookstore.proto` — the real `.proto` service definition.
- `generate.sh` — regenerates `generated/` from the proto (gitignored; not committed).
- `src/demo/BookServiceImpl.java` — the real service implementation (all four RPCs).
- `src/demo/GrpcApiDemo.java` — the real client driving all four RPCs and printing real results.
