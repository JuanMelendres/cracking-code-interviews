# GraphQL API Design — Real, Executed Demo

Backs [GraphQL API Design](../../../syllabus/07-api-design/graphql-api-design.md) (T-917). A real graphql-java 26.1 schema, resolvers, and `GraphQL.execute()` calls — no mocked output.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/demo/GraphQLApiDemo.java
java -cp "out:lib/*" demo.GraphQLApiDemo
```

## Reproduce the transcript

`output-transcript.txt` is the complete, real, unedited output of the last run, matching the chapter's Core Concepts and Failure Modes sections:

1. A client-shaped query asking for only `title` — no REST-style envelope to over- or under-fetch.
2. The naive per-object resolver pattern really does call the "author backend" once per book: real counter shows `3` calls for 3 books (N+1).
3. The identical query with a `DataLoader`-batched resolver: real counter shows exactly `1` call.
4. A mutation (`addBook`) executed and its result read back.
5. A validation-time error (unknown field) — real proof that `data` comes back `null` for the *entire* request, not just the bad field.
6. An execution-time error under a **non-null** field (`author: Author!`) — real proof that GraphQL's null-bubbling wipes the whole response when a non-null field fails, contrary to the common "GraphQL always partially succeeds" assumption.
7. The identical failure with `author` declared **nullable** — real proof that partial success only happens when the schema's nullability allows it.
