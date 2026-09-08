# Spring MVC Fundamentals — Real, Executed Demo

Backs [Spring MVC Fundamentals](../../../syllabus/05-spring/spring-mvc-fundamentals.md) (T-2203). A real Spring Boot 3.5.16 app (`demo.TaskApplication`), embedded Tomcat, running on `localhost:8080` — plain jars fetched from Maven Central, no Maven/Gradle install, same dependency set as `practice/java/full-stack-integration-backend`.

Three layers, each a real Spring-managed bean: `TaskController` (`@RestController`) → `TaskService` (`@Service`) → `TaskRepository` (`@Repository`, in-memory), wired entirely by constructor-based dependency injection — no field `@Autowired` anywhere.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/demo/*.java
java -cp "out:lib/*" demo.TaskApplication
```

## Reproduce the transcript

With the app running in another terminal:

```bash
curl -s http://localhost:8080/tasks
curl -s -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -d '{"title":"Write chapter"}'
curl -s http://localhost:8080/tasks/1
curl -s -w "\n%{http_code}\n" http://localhost:8080/tasks/999
```

`curl-transcript.txt` is the full, real output of the last run, matching the chapter's Section 7. `curl-transcript-before-fix.txt` is a genuine artifact, not a staged one: `TaskController`'s original `@PathVariable Long id` (no explicit name) produced a real `500` with `IllegalArgumentException: Name for argument of type [java.lang.Long] not specified, and parameter name information not available via reflection` on both `GET /tasks/1` and `GET /tasks/999` — captured before the fix (`@PathVariable("id") Long id`) was applied. The chapter's Section 8 (Common Mistakes) and Section 17 (Debugging Exercises) both use this exact, real failure.
