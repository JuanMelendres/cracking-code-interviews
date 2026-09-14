package demo;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Real, executed graphql-java 26.1 demo backing
 * syllabus/07-api-design/graphql-api-design.md (T-917).
 *
 * Three things this proves with real counters, not narration:
 *   1. A client can ask for exactly the fields it wants (no REST-style
 *      over/under-fetching envelope).
 *   2. The naive per-object resolver pattern really does call the author
 *      "backend" once per book -- the N+1 problem is not theoretical.
 *   3. Swapping the same resolver for a DataLoader-batched one collapses
 *      those N calls into exactly 1, for the identical query and data.
 */
public class GraphQLApiDemo {

    // ---- fake "database" ----------------------------------------------

    private static final List<Map<String, Object>> BOOKS = new ArrayList<>(List.of(
            Map.of("id", "b1", "title", "Designing Data-Intensive Applications", "authorId", "a1"),
            Map.of("id", "b2", "title", "Database Internals", "authorId", "a2"),
            Map.of("id", "b3", "title", "Release It!", "authorId", "a3")
    ));

    private static final Map<String, Map<String, Object>> AUTHORS = Map.of(
            "a1", Map.of("id", "a1", "name", "Martin Kleppmann"),
            "a2", Map.of("id", "a2", "name", "Alex Petrov"),
            "a3", Map.of("id", "a3", "name", "Michael Nygard")
    );

    // Counts real calls into the "author backend" -- this is the number
    // the N+1 section of the chapter measures directly.
    private static final AtomicInteger authorBackendCalls = new AtomicInteger(0);

    private static Map<String, Object> fetchAuthorFromBackend(String authorId) {
        authorBackendCalls.incrementAndGet();
        return AUTHORS.get(authorId);
    }

    private static final String SCHEMA = """
        type Query {
          books: [Book!]!
          book(id: ID!): Book
        }

        type Mutation {
          addBook(title: String!, authorId: ID!): Book!
        }

        type Book {
          id: ID!
          title: String!
          author: Author!
        }

        type Author {
          id: ID!
          name: String!
        }
        """;

    public static void main(String[] args) {
        System.out.println("=== 1. Client-shaped query: only `title`, nothing else ===");
        runUnbatched("{ books { title } }");

        System.out.println();
        System.out.println("=== 2. Naive per-object resolver: N+1 in action (3 books -> author backend calls) ===");
        authorBackendCalls.set(0);
        runUnbatched("{ books { title author { name } } }");
        System.out.println(">>> author backend was called " + authorBackendCalls.get() + " times for 3 books (N+1)");

        System.out.println();
        System.out.println("=== 3. Same query, DataLoader-batched resolver: exactly 1 backend call ===");
        authorBackendCalls.set(0);
        runBatched("{ books { title author { name } } }");
        System.out.println(">>> author backend was called " + authorBackendCalls.get() + " time(s) for the same 3 books");

        System.out.println();
        System.out.println("=== 4. Mutation: addBook ===");
        runUnbatched("""
            mutation {
              addBook(title: "The Phoenix Project", authorId: "a3") {
                id
                title
                author { name }
              }
            }
            """);

        System.out.println();
        System.out.println("=== 5. Validation-time error: rejects the WHOLE request, data is null (still HTTP 200) ===");
        runUnbatched("{ books { title } missingTopLevelField }");

        System.out.println();
        System.out.println("=== 6. Execution error under a NON-NULL field: null bubbles up and wipes the whole response ===");
        runFailingFieldQuery(true);

        System.out.println();
        System.out.println("=== 7. Same failure, `author` declared NULLABLE: the rest of the data really does survive ===");
        runFailingFieldQuery(false);
    }

    // ---- wiring: a resolver that throws for one specific book ---------

    private static final String SCHEMA_NULLABLE_AUTHOR = """
        type Query {
          books: [Book!]!
        }

        type Book {
          id: ID!
          title: String!
          author: Author
        }

        type Author {
          id: ID!
          name: String!
        }
        """;

    private static void runFailingFieldQuery(boolean nonNullAuthor) {
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeRegistry = schemaParser.parse(nonNullAuthor ? SCHEMA : SCHEMA_NULLABLE_AUTHOR);

        DataFetcher<List<Map<String, Object>>> booksFetcher = env -> BOOKS;
        DataFetcher<Map<String, Object>> authorFetcher = env -> {
            Map<String, Object> book = env.getSource();
            if ("b2".equals(book.get("id"))) {
                throw new RuntimeException("author service unavailable for book b2");
            }
            return fetchAuthorFromBackend((String) book.get("authorId"));
        };

        RuntimeWiring.Builder wiringBuilder = RuntimeWiring.newRuntimeWiring()
                .type("Query", b -> b.dataFetcher("books", booksFetcher))
                .type("Book", b -> b.dataFetcher("author", authorFetcher));
        if (nonNullAuthor) {
            wiringBuilder.type("Mutation", b -> b); // schema has Mutation; leave unwired, unused here
        }

        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiringBuilder.build());
        GraphQL graphQL = GraphQL.newGraphQL(schema).build();
        ExecutionResult result = graphQL.execute("{ books { title author { name } } }");
        printResult(result);
    }

    // ---- wiring: unbatched (naive) resolver ----------------------------

    private static void runUnbatched(String query) {
        GraphQLSchema schema = buildSchema(false);
        GraphQL graphQL = GraphQL.newGraphQL(schema).build();
        ExecutionResult result = graphQL.execute(query);
        printResult(result);
    }

    // ---- wiring: DataLoader-batched resolver ---------------------------

    private static void runBatched(String query) {
        GraphQLSchema schema = buildSchema(true);
        GraphQL graphQL = GraphQL.newGraphQL(schema).build();

        BatchLoader<String, Map<String, Object>> batchLoader = authorIds -> {
            authorBackendCalls.incrementAndGet(); // one call for the whole batch
            List<Map<String, Object>> loaded = authorIds.stream()
                    .map(AUTHORS::get)
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(loaded);
        };
        DataLoader<String, Map<String, Object>> authorLoader = DataLoaderFactory.newDataLoader(batchLoader);
        DataLoaderRegistry registry = new DataLoaderRegistry();
        registry.register("authorLoader", authorLoader);

        graphql.ExecutionInput input = graphql.ExecutionInput.newExecutionInput()
                .query(query)
                .dataLoaderRegistry(registry)
                .build();

        ExecutionResult result = graphQL.execute(input);
        printResult(result);
    }

    private static GraphQLSchema buildSchema(boolean useDataLoader) {
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeRegistry = schemaParser.parse(SCHEMA);

        DataFetcher<List<Map<String, Object>>> booksFetcher = env -> BOOKS;

        DataFetcher<Map<String, Object>> bookByIdFetcher = env -> {
            String id = env.getArgument("id");
            return BOOKS.stream().filter(b -> b.get("id").equals(id)).findFirst().orElse(null);
        };

        DataFetcher<CompletionStage<Map<String, Object>>> addBookFetcher = env -> {
            String title = env.getArgument("title");
            String authorId = env.getArgument("authorId");
            Map<String, Object> newBook = Map.of("id", "b" + (BOOKS.size() + 1), "title", title, "authorId", authorId);
            BOOKS.add(newBook);
            return CompletableFuture.completedFuture(newBook);
        };

        DataFetcher<?> authorFetcher = useDataLoader
                ? (DataFetcher<CompletionStage<Map<String, Object>>>) env -> {
                    Map<String, Object> book = env.getSource();
                    DataLoader<String, Map<String, Object>> loader = env.getDataLoader("authorLoader");
                    return loader.load((String) book.get("authorId"));
                  }
                : (DataFetcher<Map<String, Object>>) env -> {
                    Map<String, Object> book = env.getSource();
                    return fetchAuthorFromBackend((String) book.get("authorId"));
                  };

        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", b -> b
                        .dataFetcher("books", booksFetcher)
                        .dataFetcher("book", bookByIdFetcher))
                .type("Mutation", b -> b
                        .dataFetcher("addBook", addBookFetcher))
                .type("Book", b -> b
                        .dataFetcher("author", authorFetcher))
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
    }

    private static void printResult(ExecutionResult result) {
        System.out.println("data:   " + result.getData());
        if (!result.getErrors().isEmpty()) {
            System.out.println("errors: " + result.getErrors());
        }
    }
}
